package frc.common.commands;

import java.util.Map;
import java.util.logging.Logger;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.common.math.MathUtils;
import frc.common.math.Vector2;
import frc.robot.OI;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;

public class HolonomicDriveCommand extends CommandBase {

    static Logger logger = Logger.getLogger(HolonomicDriveCommand.class.getName());

    //This is the speed multiplier, it should be between 0 and 1.
    private double percentSpeed;

    private NetworkTableEntry speedMultiplier = Shuffleboard.getTab("Easy Drive").add("Speed Multiplier", 1).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();

    /** This is the command that actually drives the robot in teleop mode.
     *  It uses inputs from the OI class
     * 
     */
    public HolonomicDriveCommand() {
        this.percentSpeed = MathUtils.clamp(speedMultiplier.getDouble(1), 0, 1);
        addRequirements(DrivetrainSubsystem2910.getInstance());
    }

    @Override
    public void execute() {
        /*
            Each value is first recieved from the OI class.
            Then, the values are squared. This causes the speed to increase more gradually rather then at a constant rate.
            Finally, the values are multiplied by the desired speed which is recieved from shuffleboard to allow the robot to be slowed
            down for demonstrations and precise movements.

        */
        

        double forward = - OI.getInstance().getLeftYValue();
        
        // Square the forward stick
        forward = Math.copySign(Math.pow(forward, 2.0), forward);

        forward *= percentSpeed;

        double strafe = - OI.getInstance().getLeftXValue();
        // Square the strafe stick
        strafe = Math.copySign(Math.pow(strafe, 2.0), strafe);

        strafe *= percentSpeed;

        double rotation = - OI.getInstance().getRightXValue();

        // Square the rotation stick
        rotation = Math.copySign(Math.pow(rotation, 2.0), rotation);

        rotation *= percentSpeed;

        //Send the values to the drivetrain
        Vector2 translation = new Vector2(forward, strafe);
        DrivetrainSubsystem2910.getInstance().holonomicDrive(translation, rotation, true);

        
    }

    @Override
    public boolean isFinished() {
        //False so that this command runs continuously.
        return false;
    }
}
