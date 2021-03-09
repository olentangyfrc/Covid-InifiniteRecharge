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
        double forward;
        if(OI.getInstance().getLeftXboxYValue() != 0) {
            forward = - OI.getInstance().getLeftXboxYValue();
        } else if(OI.getInstance().getAuxJoystickYValue() != 0) {
            forward = - OI.getInstance().getAuxJoystickYValue();
        } else {
            forward = - OI.getInstance().getLeftJoystickYValue();
        }
        // Square the forward stick
        forward = Math.copySign(Math.pow(forward, 2.0), forward);

        double strafe;
        if(OI.getInstance().getLeftXboxXValue() != 0) {
            strafe = - OI.getInstance().getLeftXboxXValue();
        } else if(OI.getInstance().getAuxJoystickXValue() != 0) {
            strafe = - OI.getInstance().getAuxJoystickXValue();
        } else {
            strafe = - OI.getInstance().getLeftJoystickXValue();
        }
        // Square the strafe stick
        strafe = Math.copySign(Math.pow(strafe, 2.0), strafe);

        double rotation;
        if(OI.getInstance().getRightXboxXValue() != 0) {
            rotation = - OI.getInstance().getRightXboxXValue();
        } else if(OI.getInstance().getAuxJoystickZValue() != 0) {
            rotation = - OI.getInstance().getAuxJoystickZValue();
        } else {
            rotation = - OI.getInstance().getRightJoystickXValue();
        }
        // Square the rotation stick
        rotation = Math.copySign(Math.pow(rotation, 2.0), rotation);


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
