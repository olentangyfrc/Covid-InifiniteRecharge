// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystem.balldelivery.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class SetShootingZone extends CommandBase {
  private BallDelivery.ShootingZone zone;
  private BallDelivery  bd;
  /** Creates a new AngleHoodToPosition. */
  public SetShootingZone(BallDelivery s, BallDelivery.ShootingZone z) {
    addRequirements(s);
    bd  = s;
    zone  = z;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    bd.setShootingZone(zone);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return bd.isHoodAtPosition(zone);
  }
}
