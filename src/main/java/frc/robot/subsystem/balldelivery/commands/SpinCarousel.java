package frc.robot.subsystem.balldelivery.commands;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;
import java.time.Instant;
import java.time.Duration;

import java.time.Instant;
import java.time.Duration;

public class SpinCarousel extends CommandBase {
  private BallDelivery ballDelivery;
  private boolean stop;
  private boolean isFirstTime;
  private static Logger logger = Logger.getLogger(SpinCarousel.class.getName());
  private Instant startTime;

  public SpinCarousel(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates SpinCarousel");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts SpinCarousel");
    stop = false;
    isFirstTime = true;
    startTime = Instant.now();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("spinning carousel");
    if(isFirstTime == true)
    {
      ballDelivery.spinCarousel();
      isFirstTime = false;
    }

    if(Duration.between(Instant.now(), startTime).toMillis() >= 500){
      stop = ballDelivery.stopCarousel(false);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = ballDelivery.stopCarousel(true);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {

    return stop;  
  }
}

