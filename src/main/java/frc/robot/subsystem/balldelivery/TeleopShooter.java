/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.balldelivery;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.OI;
import frc.robot.OzoneException;
import frc.robot.subsystem.balldelivery.commands.DeliverBall;
import frc.robot.subsystem.balldelivery.commands.StopDelivery;
import frc.common.math.MathUtils;
import java.time.Instant;
import java.util.logging.Logger;

public class TeleopShooter extends SubsystemBase {

  private BallDelivery bd;
  private final double hoodMovementSpeedMultiplier = 5;

  static Logger logger = Logger.getLogger(TeleopShooter.class.getName());

  /**
   * This subsystem is used to allow a human driver to start and stop the shooter and adjust the hood.
   * The robot will shoot while the menu button is held.
   * Right trigger will increase the hood position.
   * Leftt trigger will decrease it.
   */
  public TeleopShooter(BallDelivery bd) {
    this.bd = bd;
  }

  public void init() {
    //bind the start and stop shooter buttons
    try {
      OI.getInstance().bind(new DeliverBall(bd), OI.XboxMenu, OI.WhenPressed);
      OI.getInstance().bind(new StopDelivery(bd), OI.XboxMenu, OI.WhenReleased);
    } catch(OzoneException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void periodic() {
    if(DriverStation.getInstance().isEnabled()) {
      //Adjust the hood position based on the state of the right and left triggers and the variable hoodMovementSpeedMultiplier.
      bd.setTargetHoodPosition(MathUtils.clamp(bd.getTargetHoodPosition() + hoodMovementSpeedMultiplier * OI.getInstance().getRightTriggerValue(), 0, 651));
      bd.setTargetHoodPosition(bd.getTargetHoodPosition() - hoodMovementSpeedMultiplier * OI.getInstance().getLeftTriggerValue());
      logger.info(Instant.now().toString());
    }
  }
}
