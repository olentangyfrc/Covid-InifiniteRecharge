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

    static {
        ShuffleboardTab sandstormTab = Shuffleboard.getTab("Sandstorm settings");

        sideChooser = new SendableChooser<>();
        sideChooser.addOption("Left", Side.LEFT);
        sideChooser.setDefaultOption("Right", Side.RIGHT);
        sandstormTab.add("Starting Side", sideChooser);

        orientationChooser = new SendableChooser<>();
        orientationChooser.setDefaultOption("Forward", Rotation2.ZERO);
        orientationChooser.addOption("Backwards", Rotation2.fromDegrees(180.0));
        orientationChooser.addOption("Left", Rotation2.fromDegrees(90.0));
        orientationChooser.addOption("Right", Rotation2.fromDegrees(270.0));
        sandstormTab.add("Starting Orientation", orientationChooser);

        autonomousModeChooser = new SendableChooser<>();
        autonomousModeChooser.setDefaultOption("Driven", AutonomousMode.DRIVEN);
        autonomousModeChooser.addOption("Hybrid", AutonomousMode.HYBRID);
        autonomousModeChooser.addOption("Autonomous", AutonomousMode.AUTONOMOUS);
        sandstormTab.add("Mode", autonomousModeChooser);

        onHab2Entry = sandstormTab.add("On HAB 2", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();
        placeThirdPanelEntry = sandstormTab.add("Place 3rd Hatch", false).withWidget(BuiltInWidgets.kToggleButton)
                .getEntry();
        placeFourthPanelEntry = sandstormTab.add("Place 4th Hatch", false).withWidget(BuiltInWidgets.kToggleButton)
                .getEntry();
        rocketAutoEntry = sandstormTab.add("Rocket Auto", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();
    }

    public AutonomousSelector(AutonomousTrajectories trajectories) {
        this.trajectories = trajectories;
    }

    private Command getFillRocketBottomCommand() {
        // First leave the hab and drive to the far rocket
        AutonomousMode mode = autonomousModeChooser.getSelected();
        Rotation2 startingOrientation = orientationChooser.getSelected();
        Side startingSide = sideChooser.getSelected();
        boolean onHab2 = onHab2Entry.getBoolean(false);

        SequentialCommandGroup group = new SequentialCommandGroup();

        if (onHab2) {
            group.andThen(new FollowTrajectoryCommand(
                    trajectories.getHab2ToRocketFarTrajectory(startingSide)
            ));
        } else {
            group.andThen(new FollowTrajectoryCommand(
                    trajectories.getHab1ToRocketFarTrajectory(startingSide)
            ));
        }

        // Go back to the loading station and grab the next hatch
        group.andThen(new FollowTrajectoryCommand(
                trajectories.getRocketFarToLoadingStationTrajectory(startingSide)
        ));
        // Go to the near rocket and place
        group.andThen(new FollowTrajectoryCommand(
                trajectories.getLoadingStationToRocketNearTrajectory(startingSide)
        ));
        return group;
    }

    public Command getCommand() {
        SequentialCommandGroup group = new SequentialCommandGroup();
        
        group.andThen(new FollowTrajectoryCommand(
            trajectories.getBasicLineTrajectory()
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
