/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.Auton.PowerPortAuton;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.common.auton.AutonomousTrajectories;
import frc.common.commands.FollowTrajectoryCommand;
import frc.robot.OI;
import frc.robot.subsystem.Auton.InterstellarAccuracyAuton.commands.WaitForInputCommand;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.robot.subsystem.balldelivery.commands.PutHoodDown;
import frc.robot.subsystem.balldelivery.commands.ShootZone;
import frc.robot.subsystem.balldelivery.commands.StopEating;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;


public class PowerPortAutonSegment extends SequentialCommandGroup {
  
  private AutonomousTrajectories trajectories = new AutonomousTrajectories(DrivetrainSubsystem2910.CONSTRAINTS);
  private BallDelivery bd;

  /**
   * Creates a new PowerPortAuton.
   */
  public PowerPortAutonSegment(BallDelivery bd) {
    this.bd = bd;

    addRequirements(bd);

    addCommands(
      new PutHoodDown(bd),
      new ShootZone(bd, BallDelivery.ShootingZone.Blue),
      new StopEating(bd),
      new FollowTrajectoryCommand(trajectories.getBlueZonetoReIntroductionZone()),
      new WaitForInputCommand(OI.getButton(OI.XboxX)),
      new FollowTrajectoryCommand(trajectories.getReIntroductionZoneToBlueZone())
    );
  }
}
