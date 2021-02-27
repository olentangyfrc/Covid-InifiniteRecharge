/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.balldelivery;

import java.util.logging.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import frc.robot.subsystem.PortMan;

/**
 * Add your docs here.
 */
public class BallDelivery extends SubsystemBase{

    private static Logger logger = Logger.getLogger(BallDelivery.class.getName());
    private TalonFX shootingMotorLeft;
    private TalonFX shootingMotorRight;
    private TalonSRX eatingMotor;
    private TalonSRX carouselMotor;
    private TalonSRX hoodMotor;

    private DigitalInput stopCarousel;
    private DigitalInput zeroShooter;

    private double pValue;
    private double iValue;
    private double dValue;

    private double carouselVelocity;
    private double eatingVelocity;
    private double shootingVelocity;
    private double anglePosition;
    
    public void init(final PortMan portMan) throws Exception {
        logger.info("init");
        shootingMotorLeft = new TalonFX(portMan.acquirePort(PortMan.can_43_label, "ShootingMotorLeft"));
        shootingMotorRight = new TalonFX(portMan.acquirePort(PortMan.can_42_label, "ShootingMotorRight"));
        eatingMotor = new TalonSRX(portMan.acquirePort(PortMan.can_12_label, "EatingMotor"));
        carouselMotor = new TalonSRX(portMan.acquirePort(PortMan.can_11_label, "CarouselMotor"));
        hoodMotor = new TalonSRX(portMan.acquirePort(PortMan.can_27_label, "HoodMotor"));
        
        shootingMotorLeft.setInverted(true);
        shootingMotorRight.follow(shootingMotorLeft);
        //shootingMotorLeft.setInverted(false);

        pValue = .9;
        iValue = 0;
        dValue = .02;
        carouselVelocity = 100; //don't know if this value is right
        eatingVelocity = 100;
        shootingVelocity = 100;
        anglePosition = 0.0;

        shootingMotorLeft.setNeutralMode(NeutralMode.Coast);
        shootingMotorLeft.configFactoryDefault();
        shootingMotorLeft.configAllowableClosedloopError(0, 5);
        shootingMotorLeft.setSelectedSensorPosition(0, 0, 0);
        shootingMotorLeft.config_kP(0, pValue, 0);
        shootingMotorLeft.config_kI(0, iValue, 0);
        shootingMotorLeft.config_kD(0, dValue, 0);
        shootingMotorLeft.config_kF(0, 0, 0);
        shootingMotorLeft.configClosedloopRamp(.9);

        shootingMotorRight.setNeutralMode(NeutralMode.Coast);
        shootingMotorRight.configFactoryDefault();
        shootingMotorRight.configAllowableClosedloopError(0, 5);
        shootingMotorRight.setSelectedSensorPosition(0, 0, 0);
        shootingMotorRight.config_kP(0, pValue, 0);
        shootingMotorRight.config_kI(0, iValue, 0);
        shootingMotorRight.config_kD(0, dValue, 0);
        shootingMotorRight.config_kF(0, 0, 0);
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
        
        carouselMotor.setNeutralMode(NeutralMode.Coast);
        carouselMotor.configFactoryDefault();
        carouselMotor.configAllowableClosedloopError(0, 5);
        carouselMotor.setSelectedSensorPosition(0, 0, 0);
        carouselMotor.config_kP(0, pValue, 0);
        carouselMotor.config_kI(0, iValue, 0);
        carouselMotor.config_kD(0, dValue, 0);
        carouselMotor.config_kF(0, 0, 0);
        carouselMotor.configClosedloopRamp(.9);

        hoodMotor.setNeutralMode(NeutralMode.Coast);
        hoodMotor.configFactoryDefault();
        hoodMotor.configAllowableClosedloopError(0, 5);
        hoodMotor.setSelectedSensorPosition(0, 0, 0);
        hoodMotor.config_kP(0, pValue, 0);
        hoodMotor.config_kI(0, iValue, 0);
        hoodMotor.config_kD(0, dValue, 0);
        hoodMotor.config_kF(0, 0, 0);
        hoodMotor.configClosedloopRamp(.9);
        
    }
    
    //spin the carousel
    public void spinCarousel(double vel){
        carouselVelocity = vel;

        logger.info("spin carousel");
        logger.info("spin [" + carouselVelocity + "]");

        //spin carousel
        carouselMotor.set(ControlMode.Velocity, carouselVelocity);
        logger.info("[" + carouselVelocity + "]");

        // if switch is triggered, set percent output to 0 to stop spinning
        if(!stopCarousel.get())
        {
            logger.info("stop carousel");
            carouselMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    //angle the shooter
    public void angleShooter(double pos){
        logger.info("angle shooter");
        logger.info("angle [" + pos + "]");
        
        //set off switch to start (be at bottom)
        //start motors, stop when it's in the right position
    }

    public void eatBall(double vel){
        eatingVelocity = vel;

        logger.info("eat ball");
        logger.info("spin green wheels [" + eatingVelocity + "]");
        eatingMotor.set(ControlMode.Velocity, eatingVelocity);
        logger.info("[" + eatingVelocity + "]");
    }

    public void spitOut(double vel){
        eatingVelocity = vel; 

        logger.info("spit out ball");
        logger.info("spin green wheels [" + eatingVelocity + "]");
        eatingMotor.set(ControlMode.Velocity, eatingVelocity);
        logger.info("[" + eatingVelocity + "]");
    }

    public void stopEating(){
        //stop eating
        logger.info("stop eating");
        eatingMotor.set(ControlMode.PercentOutput, 0);
    }

    public void shootBall(double vel){
        shootingVelocity = vel; 

        logger.info("shoot ball");
        logger.info("shoot ball [" + shootingVelocity + "]");
        shootingMotorLeft.set(ControlMode.Velocity, shootingVelocity);
        logger.info("[" + shootingVelocity + "]" );
    }

    public void reverseShooter(double vel){
        //enter regular positive velocity, method will spin it backwards. can also assume velocity parameter is negative
        shootingVelocity = -vel; 

        logger.info("reverse shooter");
        logger.info("reverse shooter [" + shootingVelocity + "]");
        shootingMotorLeft.set(ControlMode.Velocity, shootingVelocity);
        logger.info("[" + shootingVelocity + "]"); 
    }

    public void stopShooting(){
        //stop the shooter
        logger.info("stop shooting");
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

    public double getCurrentShootingVelocity(){
        return shootingMotorLeft.getSelectedSensorVelocity();
    }
    
    public double getCurrentAnglePosition(){
        return hoodMotor.getSelectedSensorPosition();
    }

    public double getCarouselVelocity(){
        return carouselVelocity;
    }

    public double getEatingVelocity(){
        return eatingVelocity;
    }

    public double getShootingVelocity(){
        return shootingVelocity;
    }

    public double getAnglePosition(){
        return anglePosition;
    }

    public double getPValue(){
        return pValue;
    }

    public double getIValue(){
        return iValue;
    }

    public double getDValue(){
        return dValue;
    }

    /*public double getCurrent(){
        //return motor.getSupplyCurrent();
    }*/
}
