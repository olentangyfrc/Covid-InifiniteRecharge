package frc.robot.subsystem.swerve.commands;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.common.math.Vector2;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;

public class DriveCommand2910 extends CommandBase {
    private Vector2 translation;
    private double rotation;
    private boolean fieldOriented;


    public DriveCommand2910(Vector2 translation, double rotation, boolean fieldOriented) {
        this.translation = translation;
        this.rotation = rotation;
        this.fieldOriented = fieldOriented;

        addRequirements(DrivetrainSubsystem2910.getInstance());

        //this.setRunWhenDisabled(true);
    }

    @Override
    public void initialize() {

        DrivetrainSubsystem2910.getInstance().holonomicDrive(translation, rotation, fieldOriented);
    }

    @Override
    public void end(boolean interupted) {
        DrivetrainSubsystem2910.getInstance().holonomicDrive(Vector2.ZERO, 0.0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
