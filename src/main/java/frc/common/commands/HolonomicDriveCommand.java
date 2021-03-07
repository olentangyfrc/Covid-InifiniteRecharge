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
        //These should be negated if not on Covid bot
        double forward = - OI.getInstance().getLeftJoystickYValue();
        double strafe = - OI.getInstance().getLeftJoystickXValue();
        double rotation = - OI.getInstance().getRightJoystickXValue();

        Vector2 translation = new Vector2(forward, strafe);
        //logger.log(Level.INFO, output);
        DrivetrainSubsystem2910.getInstance().holonomicDrive(translation, rotation, true);
        //String output = String.format("Translation: (%f,%f), Rotation: %f, FieldOriented: %b", translation.getX(), translation.getY(), rotation, false);
        //logger.log(Level.INFO, output);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
