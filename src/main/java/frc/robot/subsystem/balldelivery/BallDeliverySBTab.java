package frc.robot.subsystem.balldelivery;

import java.util.logging.Logger;

import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.subsystem.SBInterface;

public class BallDeliverySBTab implements SBInterface{

    public BallDelivery ballDelivery;
    public ShuffleboardTab tab;

    private static Logger logger = Logger.getLogger(BallDeliverySBTab.class.getName()); 

    public NetworkTableEntry currentCarouselVelocity;
    public NetworkTableEntry currentShootingVelocity;
    public NetworkTableEntry currentEatingVelocity;
    public NetworkTableEntry currentHoodPosition;
    public NetworkTableEntry targetCarouselVelocity;    
    public NetworkTableEntry targetShootingVelocity;
    public NetworkTableEntry targetEatingVelocity;
    public NetworkTableEntry targetHoodPosition;
    public NetworkTableEntry pValue;
    public NetworkTableEntry iValue;
    public NetworkTableEntry dValue;
    public NetworkTableEntry fValue;
    public NetworkTableEntry eatingTolerance;
    public NetworkTableEntry isCarouselSwitchOn;

    public BallDeliverySBTab(BallDelivery bd){
        ballDelivery = bd;

        tab = Shuffleboard.getTab("Ball Delivery");

        currentCarouselVelocity = tab.add("Current Carousel Velocity", 0.0).getEntry();
        currentShootingVelocity = tab.add("Current Shooting Velocity", 0.0).getEntry();
        currentEatingVelocity = tab.add("Current Eating Velocity", 0.0).getEntry();
        currentHoodPosition = tab.add("Current Hood Position", 0.0).getEntry();
        targetCarouselVelocity = tab.add("Target Carousel Velocity", 600.0).getEntry();
        targetShootingVelocity = tab.add("Target Shooting Velocity", 13000.0).getEntry();
        targetEatingVelocity = tab.add("Target Eating Velocity", 1000.0).getEntry();
        targetHoodPosition = tab.add("Target Hood Position", 0.0).getEntry();
        eatingTolerance = tab.add("Eating Tolerance", 0.0).getEntry();
        isCarouselSwitchOn = tab.add("Is Switch On", false).getEntry();

    }

    public void update(){
        currentCarouselVelocity.setDouble(ballDelivery.getCurrentCarouselVelocity());
        currentEatingVelocity.setDouble(ballDelivery.getCurrentEatingVelocity());
        targetHoodPosition.setDouble(ballDelivery.getTargetHoodPosition());
        currentShootingVelocity.setDouble(ballDelivery.getCurrentShootingVelocity());
        currentHoodPosition.setDouble(ballDelivery.getCurrentHoodPosition());
        ballDelivery.setTargetCarouselVelocity(targetCarouselVelocity.getDouble(600.0));
        ballDelivery.setTargetShootingVelocity(targetShootingVelocity.getDouble(100.0));
        ballDelivery.setTargetEatingVelocity(targetEatingVelocity.getDouble(600.0));
        ballDelivery.setEatingTolerance(eatingTolerance.getDouble(0.0));
        isCarouselSwitchOn.setBoolean(ballDelivery.isCarouselSwitchOn());
    }

}