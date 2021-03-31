/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.Auton.PowerPortAuton;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystem.SubsystemFactory;
import frc.robot.subsystem.balldelivery.BallDelivery;

// This subsystem determines when to schedule the robot to go and shoot during the PowerPort challenge.


public class PowerPortDirector extends SubsystemBase {
  private PowerPortAutonSegment segment;
  private BallDelivery bd;
  /**
   * Creates a new PowerPortDirector.
   */
  public PowerPortDirector(BallDelivery bd) {
    this.bd = bd;
  }

  public void init() {
    segment = new PowerPortAutonSegment(bd);
    segment.schedule();
  }

  @Override
  public void periodic() {
    //if the robot isn't already shooting and the time isn't up... go again.
    if(segment != null) {
      if(!segment.isScheduled() && SubsystemFactory.getInstance().getTimer().get() <= 60) {
        segment.schedule();
      }
    }
  }
}
