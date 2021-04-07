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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import frc.robot.subsystem.PortMan;
import frc.robot.subsystem.balldelivery.commands.AngleHood;

/**
 * Add your docs here.
 */
public class BallDelivery extends SubsystemBase{

    private static Logger logger = Logger.getLogger(BallDelivery.class.getName());
    private WPI_TalonFX shootingMotorLeft; //master
    private WPI_TalonFX shootingMotorRight; //follower
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

    //private DigitalInput carouselReceiverSwitch;
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
        
        shootingMotorRight.follow(shootingMotorLeft);
        shootingMotorLeft.setInverted(true);

        //carouselReceiverSwitch = new DigitalInput(portMan.acquirePort(PortMan.digital1_label, "CarouselSensor1"));

        //shootingMotorLeft.setInverted(false);

        //add p value variables for each motor? 

        pValue = 0.4;
        iValue = 0;
        dValue = 0.2;
        fValue = 0;
        targetEatingVelocity = 200;
        targetShootingVelocity = 100;
        targetHoodPosition = 310.0;

        shootingTol = 100;

        shootingMotorLeft.setNeutralMode(NeutralMode.Coast);
        shootingMotorLeft.configFactoryDefault();
        shootingMotorLeft.configAllowableClosedloopError(0, 5);
        shootingMotorLeft.setSelectedSensorPosition(0, 0, 0);
        shootingMotorLeft.config_kP(0, 0.9, 0);
        shootingMotorLeft.config_kI(0, iValue, 0);
        shootingMotorLeft.config_kD(0, dValue, 0);
        shootingMotorLeft.config_kF(0, 0.045, 0);
        shootingMotorLeft.configClosedloopRamp(.9);

        shootingMotorRight.setNeutralMode(NeutralMode.Coast);
        shootingMotorRight.configFactoryDefault();
        shootingMotorRight.configAllowableClosedloopError(0, 5);
        shootingMotorRight.setSelectedSensorPosition(0, 0, 0);
        shootingMotorRight.config_kP(0, 0.9, 0);
        shootingMotorRight.config_kI(0, iValue, 0);
        shootingMotorRight.config_kD(0, dValue, 0);
        shootingMotorRight.config_kF(0, 0.045, 0);
        shootingMotorRight.configClosedloopRamp(.9);

        eatingMotor.setNeutralMode(NeutralMode.Coast);
        eatingMotor.configFactoryDefault();
        eatingMotor.configAllowableClosedloopError(0, 5);
        eatingMotor.setSelectedSensorPosition(0, 0, 0);
        eatingMotor.config_kP(0, 0.5, 0);
        eatingMotor.config_kI(0, iValue, 0);
        eatingMotor.config_kD(0, dValue, 0);
        eatingMotor.config_kF(0, 0, 0);
        eatingMotor.configClosedloopRamp(.9);
        
        carouselMotor.setNeutralMode(NeutralMode.Brake);
        carouselMotor.configFactoryDefault();
        carouselMotor.configAllowableClosedloopError(0, 5);
        carouselMotor.setSelectedSensorPosition(0, 0, 0);
        carouselMotor.config_kP(0, 1, 0);
        carouselMotor.config_kI(0, iValue, 0);
        carouselMotor.config_kD(0, dValue, 0);
        carouselMotor.config_kF(0, 0, 0);
        carouselMotor.configClosedloopRamp(.9);

        hoodMotor.configFactoryDefault();
        hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        hoodMotor.setSensorPhase(true);
        hoodMotor.setNeutralMode(NeutralMode.Brake);
        hoodMotor.setInverted(true);
        //hoodMotor.configAllowableClosedloopError(0, 0);
        hoodMotor.setSelectedSensorPosition(0, 0, 0);
        hoodMotor.config_kP(0, 1.0, 0);
        hoodMotor.config_kI(0, 0, 0);
        hoodMotor.config_kD(0, 100, 0);
        hoodMotor.config_kF(0, 0, 0);

    }
    @Override
    public void periodic() {
        if(DriverStation.getInstance().isEnabled()) {
            if (Math.abs(getCurrentHoodPosition() - targetHoodPosition) > 5) {
                setHoodPercentOutput((targetHoodPosition - getCurrentHoodPosition() > 0 ) ? 0.2 :-0.2);   
            } else {
                setHoodPercentOutput(0.0);
            }
        }
    }

    public void setHoodPercentOutput(double output) {
        hoodMotor.set(ControlMode.PercentOutput, output);
    }

    /**
     * this should all parameters related to a zone. hood position, shooter velocity, etc
     * @param zone
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
                targetHoodPosition  = 314;
                break;
            case Red:
                targetHoodPosition = 310;

                break;
        }
    }
    
    //spin the carousel
    public void spinCarousel(){
        carouselMotor.set(ControlMode.Velocity, 600);
    }

    //this is the same as spinCarousel() ??
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

    //angle the shooter
    public void angleHood(double pos){
        logger.info("angle hood");
        logger.info("angle [" + pos + "]");

        hoodMotor.set(ControlMode.Position, pos);
    }

    public void stopAngling(){
        logger.info("stop angling hood");

        hoodMotor.set(ControlMode.PercentOutput, 0);
    }

    public void putHoodDown(){
        logger.info("putting hood down");

        while(stopHoodMotor.get() != true)
        {
            hoodMotor.set(ControlMode.Velocity, 100);
        }
        hoodMotor.set(ControlMode.PercentOutput, 0);
    }   


    public void eatBall(){
        //logger.info("eat ball");
        //logger.info("spin green wheels [" + targetEatingVelocity + "]");
        eatingMotor.set(ControlMode.Velocity, targetEatingVelocity);
        //logger.info("[" + targetEatingVelocity + "]");
    }

    public void spitOut(){
        //logger.info("spit out ball");
        //logger.info("spin green wheels [" + - targetEatingVelocity + "]");
        eatingMotor.set(ControlMode.Velocity, - targetEatingVelocity);
        //logger.info("[" + - targetEatingVelocity + "]");
    }

    public void stopEating(){
        //stop eating
        //logger.info("stop eating");
        eatingMotor.set(ControlMode.PercentOutput, 0);
    }

    public void shootBall(){
        //logger.info("shoot ball");
        //logger.info("shoot ball [" + targetShootingVelocity + "]");
        shootingMotorLeft.set(ControlMode.Velocity, targetShootingVelocity);
        //logger.info("[" + targetShootingVelocity + "]" );
        
    }

    public void reverseShooter(){
        //logger.info("reverse shooter");
        //logger.info("reverse shooter [" + - targetShootingVelocity + "]");
        shootingMotorLeft.set(ControlMode.Velocity, - targetShootingVelocity);
        //logger.info("[" + - targetShootingVelocity + "]"); 
    }

    public void stopShooting(){
        //stop the shooter
        //logger.info("stop shooting");
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

    public double getCurrentCarouselVelocity(){
        return carouselMotor.getSelectedSensorVelocity();
    }

    public double getCurrentEatingVelocity(){
        return eatingMotor.getSelectedSensorVelocity();
    }
    public void zeroHoodEncoder() {
        hoodMotor.setSelectedSensorPosition(0);
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

    /*public double getPValue(){
        return pValue;
    }

    public double getIValue(){
        return iValue;
    }

    public double getDValue(){
        return dValue;
    }
    
    public double getFValue(){
        return fValue;
    }*/

    public double getEatingTolerance(){
        return eatingTol;
    }
    
    public double getShootingTolerance(){
        return shootingTol;
    }

    public boolean isCarouselSwitchOn(){
        return beamBreakerReceiver.get();
    }

    /*public void setPValue(double p){
        pValue = p;
    }

    public void setIValue(double i){
        iValue = i;
    }

    public void setDValue(double d){
        dValue = d;
    }

    public void setFValue(double f){
        fValue = f;
    }*/

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

    /*public void setShootingTolerance(double tol){
        shootingTol = tol;
    }*/

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

    public boolean isHoodLimitSwitchHit(){
        if(stopHoodMotor.get() == true)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isAtHoodPosition(){
        if(Math.abs(getCurrentHoodPosition() - targetHoodPosition) <= 5)
            atTargetHoodPos = true;
        logger.info(atTargetHoodPos + "");
        return atTargetHoodPos;
    }

    /*public double getCurrent(){
        //return motor.getSupplyCurrent();
    }*/
}
