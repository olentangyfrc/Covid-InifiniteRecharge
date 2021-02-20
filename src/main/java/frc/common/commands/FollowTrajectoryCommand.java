package frc.common.commands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.SubsystemFactory;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.common.control.Trajectory;
import frc.common.math.Vector2;
import frc.common.util.HolonomicDriveSignal;
import frc.common.math.RigidTransform2;
import frc.common.math.Rotation2;
import frc.robot.subsystem.telemetry.Pigeon;
import frc.common.drivers.NavX.Axis;

import java.util.function.Supplier;
import java.util.Optional;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowTrajectoryCommand extends CommandBase {
    private final Supplier<Trajectory> trajectorySupplier;
    private DrivetrainSubsystem2910 driveTrain;
    private Pigeon pigeon;

    private Trajectory trajectory;

    private double previousUpdate;

    static Logger logger = Logger.getLogger(FollowTrajectoryCommand.class.getName());

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
        pigeon = SubsystemFactory.getInstance().getGyro();
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
