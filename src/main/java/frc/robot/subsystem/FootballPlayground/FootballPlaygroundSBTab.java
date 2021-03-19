package frc.robot.subsystem.FootballPlayground;

import java.util.logging.Logger;

import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.subsystem.SBInterface;

public class FootballPlaygroundSBTab implements SBInterface{
    public FootballPlayground footballPlayground;
    public ShuffleboardTab tab;

    private static Logger logger = Logger.getLogger(FootballPlaygroundSBTab.class.getName());

    public NetworkTableEntry currentMotorPosition;
    public NetworkTableEntry currentMotorVelocity;
    public NetworkTableEntry isBeamBroken;

    public FootballPlaygroundSBTab(FootballPlayground fp){
        footballPlayground = fp;

        tab = Shuffleboard.getTab("Football Playground");

        currentMotorPosition = tab.add("Current Motor Position", 0.0).getEntry();
        currentMotorVelocity = tab.add("Current Motor Velocity", 0.0).getEntry();
        isBeamBroken = tab.add("Is Beam Broken", false).getEntry();
    }

    public void update(){
        currentMotorPosition.setDouble(footballPlayground.getCurrentMotorPosition());
        currentMotorVelocity.setDouble(footballPlayground.getCurrentMotorVelocity());
        isBeamBroken.setBoolean(footballPlayground.getIsBeamBroken());
    }
}
