/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.InterstellarAccuracyAuton.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.commands.DeliverBall;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.common.commands.FollowTrajectoryCommand;
import frc.robot.subsystem.SubsystemFactory;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class InterstellarAccuracyAuton extends SequentialCommandGroup {
  /**
   * Creates a new InterstellarAccuracyAuton.
   */

  private BallDelivery ballDelivery;

  public InterstellarAccuracyAuton(BallDelivery ballDelivery) {

    this.ballDelivery = ballDelivery; 

    addCommands(
      new DeliverBall(ballDelivery)
      //new FollowTrajectoryCommand(trajectory)
    );
  }
}
