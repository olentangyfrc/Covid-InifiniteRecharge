package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

/**
 * controls hoodMotor using position control, moving it to the target hood position
 */

public class AngleHood extends CommandBase {
  private BallDelivery ballDelivery;
  private double targetHoodPosition;
  private boolean firstTime = true; 
  double p = 0.2;
  private boolean stop;
  private static Logger logger = Logger.getLogger(AngleHood.class.getName());

  public AngleHood(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts AngleHood");
    stop = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("Angle Hood");
    targetHoodPosition = ballDelivery.getTargetHoodPosition(); 
    if (Math.abs(ballDelivery.getCurrentHoodPosition() - targetHoodPosition) > 5) {
      ballDelivery.setHoodPercentOutput(
          (targetHoodPosition - ballDelivery.getCurrentHoodPosition() > 0 ) ? p :-p
          );   
    } else {
      ballDelivery.setHoodPercentOutput(0.0);
    }
  }

  public boolean isFinished() {
    return false;
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = true;
  }

}
