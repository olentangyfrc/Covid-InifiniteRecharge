package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ReverseShooter extends CommandBase {
  private BallDelivery ballDelivery;
  private boolean stop;
  private static Logger logger = Logger.getLogger(ReverseShooter.class.getName());

  //private int direction = 0;

  public ReverseShooter(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
    logger.info("creates ReverseShooter");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts ReverseShooter");
    stop = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("reversing shooter");
    ballDelivery.reverseShooter();    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    //add conditional to finish command
    return true;
  }
}
