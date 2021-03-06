package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

/**
 * stops the eating wheels by running stopEating() in BallDelivery
 */

public class StopEating extends CommandBase {
  private BallDelivery ballDelivery;
  private boolean stop;
  private static Logger logger = Logger.getLogger(StopEating.class.getName());

  public StopEating(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates StopEating");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts StopEating");
    stop = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("stopping to eat balls");
    ballDelivery.stopEating();    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
