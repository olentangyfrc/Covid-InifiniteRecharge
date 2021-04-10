/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.balldelivery;

import java.util.logging.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import frc.robot.subsystem.PortMan;
import frc.robot.subsystem.balldelivery.commands.AngleHood;

/**
 * BallDelivery contains everything required to shoot the ball, including the 
 * carousel, the hood, the "eating wheels", and the "shooting wheels."
 * 
 * Carousel (carouselMotor): can transport three balls at a time, rotates to direct balls to eating wheels
 * Hood (hoodMotor): rotates to change angle of launch
 * Eating Wheels (eatingMotor): spin to direct balls to shooting wheels
 * Shooting Wheels (controlled by shootingMotorLeft): spin to shoot ball
 * 
 */
public class BallDelivery extends SubsystemBase{

    private static Logger logger = Logger.getLogger(BallDelivery.class.getName());
    private WPI_TalonFX shootingMotorLeft; //master --> tell this motor what to do
    private WPI_TalonFX shootingMotorRight; //follower --> will copy shootingMotorLeft
    private WPI_TalonSRX eatingMotor;
    private WPI_TalonSRX carouselMotor;
    private TalonSRX hoodMotor;

    private DigitalInput stopCarousel;
    private DigitalInput zeroShooter;
    private DigitalInput stopHoodMotor;

    private double pValue;
    private double iValue;
    private double dValue;
    private double fValue;

    private double targetCarouselVelocity;
    private double targetEatingVelocity;
    private double targetShootingVelocity;
    private double targetHoodPosition;

    private boolean atTargetEatingVel;
    private boolean atTargetShootingVel;
    private boolean atTargetHoodPos;

    public double eatingTol;
    public double shootingTol;
    public double hoodTol = 5;

    private DigitalInput beamBreakerReceiver;
    private boolean lastReading = false;
    private int count = 0;

    private CommandBase angleHood;

    public static enum ShootingZone {
        Green,
        Yellow,
        Blue,
        Red
    };
    
    public void init(final PortMan portMan) throws Exception {
        logger.info("init");
        shootingMotorLeft = new WPI_TalonFX(portMan.acquirePort(PortMan.can_43_label, "ShootingMotorLeft"));
        shootingMotorRight = new WPI_TalonFX(portMan.acquirePort(PortMan.can_42_label, "ShootingMotorRight"));
        eatingMotor = new WPI_TalonSRX(portMan.acquirePort(PortMan.can_12_label, "EatingMotor"));
        carouselMotor = new WPI_TalonSRX(portMan.acquirePort(PortMan.can_11_label, "CarouselMotor"));
        hoodMotor = new WPI_TalonSRX(portMan.acquirePort(PortMan.can_27_label, "HoodMotor"));

        stopHoodMotor = new DigitalInput(0);
        beamBreakerReceiver = new DigitalInput(portMan.acquirePort(PortMan.digital2_label, "Beam Breaker Receiver"));
        
        //shootingMotorRight copies shootingMotorLeft, but inverted so that the motors don't fight each other (look at placement)
        shootingMotorRight.follow(shootingMotorLeft);
        shootingMotorLeft.setInverted(true);

        //default pid values
        pValue = 0.4;
        iValue = 0;
        dValue = 0.2;
        fValue = 0;

        //default target values
        targetCarouselVelocity = 100; 
        targetEatingVelocity = 100;
        targetShootingVelocity = 100;
        targetHoodPosition = 0.0;
        shootingTol = 100;

        //motor configurations
        shootingMotorLeft.setNeutralMode(NeutralMode.Coast);
        shootingMotorLeft.configFactoryDefault();
        shootingMotorLeft.configAllowableClosedloopError(0, 5);
        shootingMotorLeft.setSelectedSensorPosition(0, 0, 0);
        shootingMotorLeft.config_kP(0, 0.3, 0);
        shootingMotorLeft.config_kI(0, iValue, 0);
        shootingMotorLeft.config_kD(0, dValue, 0);
        shootingMotorLeft.config_kF(0, 0.045, 0);
        shootingMotorLeft.configClosedloopRamp(.9);

        shootingMotorRight.setNeutralMode(NeutralMode.Coast);
        shootingMotorRight.configFactoryDefault();
        shootingMotorRight.configAllowableClosedloopError(0, 5);
        shootingMotorRight.setSelectedSensorPosition(0, 0, 0);
        shootingMotorRight.config_kP(0, 0.3, 0);
        shootingMotorRight.config_kI(0, iValue, 0);
        shootingMotorRight.config_kD(0, dValue, 0);
        shootingMotorRight.config_kF(0, 0.045, 0);
        shootingMotorRight.configClosedloopRamp(.9);

        eatingMotor.setNeutralMode(NeutralMode.Coast);
        eatingMotor.configFactoryDefault();
        eatingMotor.configAllowableClosedloopError(0, 5);
        eatingMotor.setSelectedSensorPosition(0, 0, 0);
        eatingMotor.config_kP(0, pValue, 0);
        eatingMotor.config_kI(0, iValue, 0);
        eatingMotor.config_kD(0, dValue, 0);
        eatingMotor.config_kF(0, 0, 0);
        eatingMotor.configClosedloopRamp(.9);
        
        carouselMotor.setNeutralMode(NeutralMode.Brake);
        carouselMotor.configFactoryDefault();
        carouselMotor.configAllowableClosedloopError(0, 5);
        carouselMotor.setSelectedSensorPosition(0, 0, 0);
        carouselMotor.config_kP(0, pValue, 0);
        carouselMotor.config_kI(0, iValue, 0);
        carouselMotor.config_kD(0, dValue, 0);
        carouselMotor.config_kF(0, 0, 0);
        carouselMotor.configClosedloopRamp(.9);

        hoodMotor.configFactoryDefault();
        hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        hoodMotor.setSensorPhase(true);
        hoodMotor.setNeutralMode(NeutralMode.Brake);
        hoodMotor.setInverted(true);
        hoodMotor.setSelectedSensorPosition(0, 0, 0);
        hoodMotor.config_kP(0, 1.0, 0);
        hoodMotor.config_kI(0, 0, 0);
        hoodMotor.config_kD(0, 100, 0);
        hoodMotor.config_kF(0, 0, 0);
    }

    @Override
    public void periodic() {
        //maintain the target hood position at all times
        if (Math.abs(getCurrentHoodPosition() - targetHoodPosition) > 5) {
            setHoodPercentOutput((targetHoodPosition - getCurrentHoodPosition() > 0 ) ? 0.2 :-0.2);   
        } else {
            setHoodPercentOutput(0.0);
        }
    }

    public void setHoodPercentOutput(double output) {
        hoodMotor.set(ControlMode.PercentOutput, output);
    }

    /**
     * this should include all parameters related to a zone. hood position, shooter velocity, etc
     * @param zone
     * @return nothing
     */
    public void setShootingZone(BallDelivery.ShootingZone zone) {
        switch (zone) {
            case Green:
                targetHoodPosition  = 103;
                break;
            case Yellow:
                targetHoodPosition  = 254;
                break;
            case Blue:
                targetHoodPosition  = 301;
                break;
            case Red:
                targetHoodPosition = 310;

                break;
        }
    }
    
    /*
     * spin the carousel
     * @return nothing
     */
    public void spinCarousel(){
        carouselMotor.set(ControlMode.Velocity, 600);
    }

    /*
     * stop the carousel
     * a.) if forced to stop, or
     * b.) if the beam breaker is triggered and the carousel has completed one full rotation
     * @param forceStop This is true if driver manually tells carousel to stop, in which case
     *                  the carousel will stop where it is
     */
    public boolean stopCarousel(boolean forceStop){
        //false means beam is being broken ("switch" is on)
        if(forceStop)
            carouselMotor.set(ControlMode.PercentOutput, 0);

        boolean reading;
        reading = beamBreakerReceiver.get();
        if(lastReading != reading){
            lastReading = reading;
            if(reading == false)
            {
                logger.info("stop");
                carouselMotor.set(ControlMode.PercentOutput, 0);
                return true;
            }
        }
        return false;
    }

    /*
     * angle the hood using position control
     * @return nothing
     */
    public void angleHood(double pos){
        logger.info("angle hood");
        logger.info("angle [" + pos + "]");

        hoodMotor.set(ControlMode.Position, pos);
    }

    /*
     * stop moving the hood, set motors to 0% output
     * @return nothing
     */
    public void stopAngling(){
        logger.info("stop angling hood");

        hoodMotor.set(ControlMode.PercentOutput, 0);
    }
    
    /*
     * move hood down until limit switch is hit
     * @return nothing
     */
    public void putHoodDown(){
        logger.info("putting hood down");

        while(stopHoodMotor.get() != true)
        {
            hoodMotor.set(ControlMode.Velocity, 100);
        }
        hoodMotor.set(ControlMode.PercentOutput, 0);
    }   

    /*
     * spin eating wheels
     * @return nothing
     */
    public void eatBall(){
        eatingMotor.set(ControlMode.Velocity, targetEatingVelocity);
    }

    /*
     * reverse eating wheels
     * @return nothing
     */
    public void spitOut(){
        eatingMotor.set(ControlMode.Velocity, - targetEatingVelocity);
    }

    /*
     * stop eating wheels
     * @return nothing
     */
    public void stopEating(){
        eatingMotor.set(ControlMode.PercentOutput, 0);
    }

    /*
     * spin shooting wheels
     * @return nothing
     */
    public void shootBall(){
        shootingMotorLeft.set(ControlMode.Velocity, targetShootingVelocity);
    }

    /*
     * reverse shooting wheels
     * @return nothing
     */
    public void reverseShooter(){
        shootingMotorLeft.set(ControlMode.Velocity, - targetShootingVelocity);
    }

    /*
     * stop shooting wheels
     * @return nothing
     */
    public void stopShooting(){
        shootingMotorLeft.set(ControlMode.PercentOutput, 0);
    }

    public void changePID(double p, double i, double d){
        if(pValue != p)
            pValue = p;
        if(iValue != i)
            iValue = i;
        if(dValue != d)
            dValue = d;
    }

    
    /*
     * return current values
     */
    public double getCurrentCarouselVelocity(){
        return carouselMotor.getSelectedSensorVelocity();
    }

    public double getCurrentEatingVelocity(){
        return eatingMotor.getSelectedSensorVelocity();
    }

    public double getCurrentShootingVelocity(){
        return shootingMotorLeft.getSelectedSensorVelocity();
    }
    
    public double getCurrentHoodPosition(){
        return hoodMotor.getSelectedSensorPosition();
    }

    public double getTargetCarouselVelocity(){
        return targetCarouselVelocity;
    }

    public double getTargetEatingVelocity(){
        return targetEatingVelocity;
    }

    public double getTargetShootingVelocity(){
        return targetShootingVelocity;
    }

    public double getTargetHoodPosition(){
        return targetHoodPosition;
    }

    public double getEatingTolerance(){
        return eatingTol;
    }
    
    public double getShootingTolerance(){
        return shootingTol;
    }

    public boolean isHoodLimitSwitchHit(){
        return stopHoodMotor.get();
    }

    public boolean isCarouselSwitchOn(){
        return beamBreakerReceiver.get();
    }


    /*
     * set target values
     * @return nothing
     */
    public void setTargetCarouselVelocity(double vel){
        targetCarouselVelocity = vel;
    }

    public void setTargetEatingVelocity(double vel){
        targetEatingVelocity = vel;
    }

    public void setTargetShootingVelocity(double vel){
        targetShootingVelocity = vel;
    }
    
    public void setTargetHoodPosition(double pos){
        targetHoodPosition = pos;
    }

    public void setEatingTolerance(double tol){
        eatingTol = tol;
    }

    //recalibrate hood so that current position is the new zero
    public void zeroHoodEncoder() {
        hoodMotor.setSelectedSensorPosition(0);
    }


    /*
     * compare current vs target values and return whether they're equal
     */
    public boolean isAtHoodPosition(){
        if(Math.abs(getCurrentHoodPosition() - targetHoodPosition) <= hoodTol)
            atTargetHoodPos = true;
        return atTargetHoodPos;
    }

    public boolean isAtShootingVelocity(){
        if(Math.abs(getCurrentShootingVelocity() - targetShootingVelocity) <= shootingTol)
            atTargetShootingVel = true;
        return atTargetShootingVel;
    }

    public boolean isAtEatingVelocity(){
        if(Math.abs(getCurrentEatingVelocity() - targetEatingVelocity) <= eatingTol)
            atTargetEatingVel = true;
        return atTargetEatingVel;
    }

}
