package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class PutHoodDown extends CommandBase {
  private BallDelivery ballDelivery;
  private boolean stop;
  private static Logger logger = Logger.getLogger(PutHoodDown.class.getName());

  //private int direction = 0;

  public PutHoodDown(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates PutHoodDown");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts PutHoodDown");
    stop = false;

    ballDelivery.putHoodDown(); 
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("putting hood down");  
    if(ballDelivery.isHoodLimitSwitchHit()) {
      stop = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return stop;
  }
}
