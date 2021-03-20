/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.InterstellarAccuracyAuton;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import frc.common.auton.AutonomousTrajectories;

import frc.common.commands.FollowTrajectoryCommand;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.DelayCommand;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.WaitForInputCommand;
import frc.robot.subsystem.balldelivery.commands.ShootBlue;
import frc.robot.subsystem.balldelivery.commands.ShootGreen;
import frc.robot.subsystem.balldelivery.commands.ShootRed;
import frc.robot.subsystem.balldelivery.commands.ShootYellow;
import frc.robot.subsystem.balldelivery.commands.DeliverBall;
import frc.robot.subsystem.balldelivery.commands.SetShootingZone;
import frc.robot.subsystem.balldelivery.commands.StopDelivery;

import frc.robot.OI;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.robot.subsystem.balldelivery.BallDelivery.ShootingZone;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class InterstellarAccuracyAuton extends SequentialCommandGroup{
  /**
   * Creates a new InterstellarAccuracyAuton.
   */

  private static Logger logger = Logger.getLogger(InterstellarAccuracyAuton.class.getName());

  private static final double shootingDelayTime = 7;

  private BallDelivery ballDelivery;
  private AutonomousTrajectories trajectories = new AutonomousTrajectories(DrivetrainSubsystem2910.CONSTRAINTS);

  public InterstellarAccuracyAuton(BallDelivery ballDelivery) {


    this.ballDelivery = ballDelivery; 
    addCommands(
      new ShootGreen(ballDelivery),
      new DelayCommand(shootingDelayTime),
      new StopDelivery(ballDelivery),
      new FollowTrajectoryCommand(trajectories.getGreenZoneToReIntroductionZone()),
      new WaitForInputCommand(OI.getButton(OI.XboxX)),
      new FollowTrajectoryCommand(trajectories.getReIntroductionZoneToYellowZone()),
      new ShootYellow(ballDelivery),
      new DelayCommand(shootingDelayTime),
      new StopDelivery(ballDelivery),
      new FollowTrajectoryCommand(trajectories.getYellowZoneToReIntroductionZone()),
      new WaitForInputCommand(OI.getButton(OI.XboxX)),
      new FollowTrajectoryCommand(trajectories.getReIntroductionZoneToBlueZone()),
      new ShootBlue(ballDelivery),
      new DelayCommand(shootingDelayTime),
      new StopDelivery(ballDelivery),
      new FollowTrajectoryCommand(trajectories.getBlueZonetoReIntroductionZone()),
      new WaitForInputCommand(OI.getButton(OI.XboxX)),
      new FollowTrajectoryCommand(trajectories.getReIntroductionZoneToRedZone()),
      new ShootRed(ballDelivery),
      new DelayCommand(shootingDelayTime),
      new StopDelivery(ballDelivery)
    );
  }
}
