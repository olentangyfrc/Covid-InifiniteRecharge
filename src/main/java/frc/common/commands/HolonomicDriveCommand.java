package frc.common.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.SubsystemFactory;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.common.math.Rotation2;
import frc.common.math.Vector2;
import frc.robot.OI;
import java.util.logging.Level;
import java.util.logging.Logger;
import frc.common.drivers.NavX.Axis;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public class HolonomicDriveCommand extends CommandBase {

    static Logger logger = Logger.getLogger(HolonomicDriveCommand.class.getName());

    public HolonomicDriveCommand() {
        addRequirements(DrivetrainSubsystem2910.getInstance());
    }
    @Override
    public void execute() {
        double forward = - OI.getInstance().getLeftJoystickYValue();
        double strafe = - OI.getInstance().getLeftJoystickXValue();
        double rotation = - OI.getInstance().getRightJoystickXValue();

        Translation2d translation = new Translation2d(forward, strafe);
        //String output = String.format("Forward[%f], Strafe[%f], Rotation[%f], Gyro[%f]", forward, strafe, rotation, DrivetrainSubsystem2910.getInstance().getGyroscope().getAxis(Axis.YAW));
        //logger.log(Level.INFO, output);
        DrivetrainSubsystem2910.getInstance().drive(translation, rotation, false);
        String output = String.format("Translation: (%f,%f), Rotation: %f, FieldOriented: %b", translation.getX(), translation.getY(), rotation, false);
        logger.log(Level.INFO, output);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
