package frc.robot.subsystem.telemetry.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.telemetry.Telemetry;
import frc.robot.subsystem.SubsystemFactory;

import edu.wpi.first.math.geometry.Translation2d;

public class ChaseBall extends CommandBase {
  private Telemetry telemetry;
  private boolean stop;
  private static Logger logger = Logger.getLogger(ChaseBall.class.getName());

  //private int direction = 0;

  public ChaseBall(Telemetry sqs) {
    // Use addRequirements() here to declare subsystem dependencies.
    telemetry = sqs;
    addRequirements(sqs);
    logger.info("creates ChaseBall");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts ChaseBall");
    stop = false;

  //stop = true; why is there stop = true?
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    SubsystemFactory.getInstance().getDriveTrain().drive(new Translation2d(telemetry.getTranslationalSpeed(), 0), - telemetry.getRotationalSpeed(), true); 
    logger.info("going");
    if(telemetry.getBallDistance() <= telemetry.getTargetBallDistance() && telemetry.getBallDirection() == 0)
      stop = true;
    if(!telemetry.getSeeBall())
      SubsystemFactory.getInstance().getDriveTrain().drive(new Translation2d(0, 0), - telemetry.getRotationalSpeed(), true); 
    logger.info("checking if at ball");
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stop = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    logger.info("checking if at ball");
    if(telemetry.getBallDistance() <= telemetry.getTargetBallDistance() && telemetry.getBallDirection() == 0)
      return stop;
    else{
      return false;
    }
  }
}
