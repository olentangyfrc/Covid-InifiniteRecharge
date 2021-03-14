package frc.common.auton;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.common.commands.FollowTrajectoryCommand;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.common.control.ITrajectoryConstraint;
import frc.common.control.MaxAccelerationConstraint;
import frc.common.control.MaxVelocityConstraint;
import frc.common.math.MathUtils;
import frc.common.math.Rotation2;
import frc.common.math.Vector2;
import frc.common.util.Side;

import java.util.LinkedList;
import java.util.Queue;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AutonomousSelector {
    private final AutonomousTrajectories trajectories;

    private static SendableChooser<Side> sideChooser;
    private static SendableChooser<Rotation2> orientationChooser;
    private static SendableChooser<AutonomousMode> autonomousModeChooser;
    private static NetworkTableEntry onHab2Entry;
    private static NetworkTableEntry placeThirdPanelEntry;
    private static NetworkTableEntry placeFourthPanelEntry;
    private static NetworkTableEntry rocketAutoEntry;

    private Queue<Command> hybridCommandQueue = new LinkedList<>();

    static Logger logger = Logger.getLogger(AutonomousSelector.class.getName());
    
    public AutonomousSelector(AutonomousTrajectories trajectories) {
        this.trajectories = trajectories;
    }
    public Command getCommand() {
        SequentialCommandGroup group = new SequentialCommandGroup();
        
        group.addCommands(new FollowTrajectoryCommand(
            trajectories.getBoxyBounceTrajectory()
        ));
        return group;
    }

    public Queue<Command> getHybridQueue() {
        return hybridCommandQueue;
    }

    private enum AutonomousMode {
        DRIVEN,
        HYBRID,
        AUTONOMOUS
    }
}
