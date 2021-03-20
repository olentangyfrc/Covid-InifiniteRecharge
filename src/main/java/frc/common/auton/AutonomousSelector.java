package frc.common.auton;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.common.commands.FollowTrajectoryCommand;

public class AutonomousSelector {
    private final AutonomousTrajectories trajectories;

    private Queue<Command> hybridCommandQueue = new LinkedList<>();

    private final SendableChooser<AutonomousPath> pathChooser = new SendableChooser<>();

    static Logger logger = Logger.getLogger(AutonomousSelector.class.getName());
  
    public AutonomousSelector(AutonomousTrajectories trajectories) {
        this.trajectories = trajectories;

        pathChooser.addOption("Barrel Racing Path", AutonomousPath.BARREL_RACING);
        pathChooser.addOption("Slalom Path", AutonomousPath.SLALOM);
        pathChooser.addOption("Bounce Path", AutonomousPath.BOUNCE);

        Shuffleboard.getTab("Auton").add("Path", pathChooser);
    }
    public Command getCommand() {
        SequentialCommandGroup group = new SequentialCommandGroup();
        switch(pathChooser.getSelected()) {
            case BARREL_RACING:
                group.addCommands(new FollowTrajectoryCommand(
                    trajectories.getBarrelRacingTrajectory()
                ));
            break;
            case SLALOM:
                group.addCommands(new FollowTrajectoryCommand(
                    trajectories.getSlalomTrajectory()
                ));
            break;
            case BOUNCE:
                group.addCommands(new FollowTrajectoryCommand(
                    trajectories.getBoxyBounceTrajectory()
                ));
            break;
        }
        group.addCommands(new FollowTrajectoryCommand(
            trajectories.getGreenZoneToReIntroductionZone()
        ));
        return group;
    }

    public Queue<Command> getHybridQueue() {
        return hybridCommandQueue;
    }

    private enum AutonomousPath {
        BARREL_RACING,
        SLALOM,
        BOUNCE
    }
}
