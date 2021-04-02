package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class AngleHoodWithJoystick extends CommandBase {
  private BallDelivery ballDelivery;
  private double targetHoodPosition;
  private boolean firstTime = true; 
  double p = 0.2;
  private double direction;
  private double percentOutput = 0.3;
  private boolean stop;
  private static Logger logger = Logger.getLogger(AngleHoodWithJoystick.class.getName());

  //private int direction = 0;

  public AngleHoodWithJoystick(BallDelivery bd) {
    // Use addRequirements() here to declare subsystem dependencies.
    ballDelivery = bd;
    addRequirements(bd);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    logger.info("starts AngleHoodWithJoystick");
    stop = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    logger.info("Angle Hood With Joystick");
    if(OI.getInstance().getAuxYValue() > 0)
        direction = 1;
    else if(OI.getInstance().getAuxYValue() < 0)
        direction = -1;
    else
        direction = 0;

    ballDelivery.angleHoodWithJoystick(percentOutput * direction);
    
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
