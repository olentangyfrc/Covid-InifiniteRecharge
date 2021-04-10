/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.Auton.InterstellarAccuracyAuton.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.OI;

public class WaitForInputCommand extends CommandBase {

  static Logger logger = Logger.getLogger(WaitForInputCommand.class.getName());

  private boolean end = false;
  private int button;
  private XboxController xbox;
  private JoystickButton btn;

  private class RegisterInput implements Runnable {
    @Override
    public void run() {
      end = true;
    }
  }

  /**
   * 
   * This command is to be used in command groups to make the group wait for a controller input to continue with the next command.
   * @param button use OI.getButton(OI.[whichever button you want])
   */
  public WaitForInputCommand(int button) {
    this.button = button;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    xbox = OI.getInstance().getXbox();
    btn = new JoystickButton(xbox, button);
    btn.whenPressed( new RegisterInput());
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return end;
  }
}
