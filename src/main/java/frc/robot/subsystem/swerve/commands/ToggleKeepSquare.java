/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.swerve.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;

/**
 * This command stops the robot from rotating away from 0.
 * It is intended to make driving the robot easier.
 */
public class ToggleKeepSquare extends CommandBase {
  /**
   * Creates a new ToggleKeepSquare.
   */
  private DrivetrainSubsystem2910 driveTrain;
  public ToggleKeepSquare(DrivetrainSubsystem2910 driveTrain) {
    this.driveTrain = driveTrain;
    addRequirements(driveTrain);
  }
  @Override
  public void initialize() {
    driveTrain.toggleKeepSquare();
  }

  @Override
  public void execute() {
  }

  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
