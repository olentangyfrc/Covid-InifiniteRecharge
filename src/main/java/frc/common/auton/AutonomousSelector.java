package frc.common.auton;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.common.commands.FollowTrajectoryCommand;

public class AutonomousSelector {
    private final AutonomousTrajectories trajectories;

    private Queue<Command> hybridCommandQueue = new LinkedList<>();

    static Logger logger = Logger.getLogger(AutonomousSelector.class.getName());

    public AutonomousSelector(AutonomousTrajectories trajectories) {
        this.trajectories = trajectories;
    }

    

    public Command getCommand() {
        SequentialCommandGroup group = new SequentialCommandGroup();
        
        group.addCommands(new FollowTrajectoryCommand(
            trajectories.getBarrelRacingTrajectory()
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
