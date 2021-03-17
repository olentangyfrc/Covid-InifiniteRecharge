/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.InterstellarAccuracyAuton;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.commands.*;
import frc.robot.subsystem.balldelivery.commands.StopDelivery;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.common.commands.FollowTrajectoryCommand;
import frc.robot.subsystem.SubsystemFactory;
import frc.common.auton.AutonomousTrajectories;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.DelayCommand;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.WaitForInputCommand;
import frc.robot.OI;

import java.util.logging.Level;
import java.util.logging.Logger;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class InterstellarAccuracyAuton extends SequentialCommandGroup{
  /**
   * Creates a new InterstellarAccuracyAuton.
   */

  private static Logger logger = Logger.getLogger(InterstellarAccuracyAuton.class.getName());

  private BallDelivery ballDelivery;
  private AutonomousTrajectories trajectories = new AutonomousTrajectories(DrivetrainSubsystem2910.CONSTRAINTS);

  public InterstellarAccuracyAuton(BallDelivery ballDelivery) {


    this.ballDelivery = ballDelivery; 
    addCommands(
      new DeliverBall(ballDelivery),
      new DelayCommand(2),
      new StopDelivery(ballDelivery),
      new FollowTrajectoryCommand(trajectories.getGreenZoneToReIntroductionZone()),
      new WaitForInputCommand(OI.getButton(OI.XboxX)),
      new FollowTrajectoryCommand(trajectories.getReIntroductionZoneToYellowZone()),
      new DeliverBall(ballDelivery),
      new DelayCommand(2),
      new StopDelivery(ballDelivery),
      new FollowTrajectoryCommand(trajectories.getYellowZoneToReIntroductionZone())
    );
  }
}