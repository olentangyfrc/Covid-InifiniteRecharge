package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ShootBall extends CommandBase {
  private BallDelivery ballDelivery;
  private boolean stop;
  private static Logger logger = Logger.getLogger(ShootBall.class.getName());

  //private int direction = 0;

  public ShootBall(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates ShootBall");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts ShootBall");
    stop = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("shooting ball");
    ballDelivery.shootBall();    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if(interrupted) {
      logger.info("Shooting Interrupted");
    }
    stop = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(ballDelivery.isAtShootingVelocity())
      stop = true;
    return stop;
  }
}
