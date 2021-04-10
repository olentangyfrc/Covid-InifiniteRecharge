package frc.common.commands;

import java.util.function.Supplier;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.common.control.Trajectory;
import frc.common.math.Vector2;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;

/**
 * This command is what actually causes the robot to follow a supplied trajectory.
 */
public class FollowTrajectoryCommand extends CommandBase {
    private final Supplier<Trajectory> trajectorySupplier;
    private DrivetrainSubsystem2910 driveTrain;

    private Trajectory trajectory;


    static Logger logger = Logger.getLogger(FollowTrajectoryCommand.class.getName());

    /**
     * @param trajectory Use a trajectory from the autonomousTrajectories class. This is not a wpi trajectory!
    */
    public FollowTrajectoryCommand(Trajectory trajectory) {
        this(() -> trajectory);
    }

    public FollowTrajectoryCommand(Supplier<Trajectory> trajectorySupplier) {
        this.trajectorySupplier = trajectorySupplier;

        addRequirements(DrivetrainSubsystem2910.getInstance());
        //this.setRunWhenDisabled(true);
    }

    @Override
    public void initialize() {
        trajectory = trajectorySupplier.get();
        driveTrain = DrivetrainSubsystem2910.getInstance();
        driveTrain.resetKinematics(Vector2.ZERO, Timer.getFPGATimestamp());
        driveTrain.getFollower().follow(trajectory);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interupted) {
        if (interupted) {
            DrivetrainSubsystem2910.getInstance().getFollower().cancel();
        }
        DrivetrainSubsystem2910.getInstance().setSnapRotation(trajectory.calculateSegment(trajectory.getDuration()).rotation.toRadians());
    }

    @Override
    public boolean isFinished() {
        // Only finish when the trajectory is completed
        return DrivetrainSubsystem2910.getInstance().getFollower().getCurrentTrajectory().isEmpty();
    }
}
