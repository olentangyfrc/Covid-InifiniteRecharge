package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class AngleHood extends CommandBase {
  private BallDelivery ballDelivery;
  private double targetHoodPosition;
  private boolean firstTime = true; 
  private boolean stop;
  private static Logger logger = Logger.getLogger(StopShooting.class.getName());

  //private int direction = 0;

  public AngleHood(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates AngleHood");
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
    targetHoodPosition = ballDelivery.getTargetHoodPosition(); 
    logger.info("targetHood position [" + targetHoodPosition + "]");
    if(!firstTime)
    {
      logger.info("angling hood");
      ballDelivery.angleHood(targetHoodPosition);   
    }

    firstTime = false;
     
  }

  public boolean isFinished() {
    /*if (Math.abs(Math.abs(ballDelivery.getCurrentHoodPosition()) - Math.abs(targetHoodPosition)) < 10) {
      ballDelivery.stopAngling();
      return true;
    } else {
      logger.info("current hood position " + ballDelivery.getCurrentHoodPosition());
      return false;
    }*/
    return true;
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = true;
  }

}
