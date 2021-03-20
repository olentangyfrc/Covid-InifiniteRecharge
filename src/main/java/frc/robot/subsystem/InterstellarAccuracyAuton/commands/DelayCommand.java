/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.InterstellarAccuracyAuton.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DelayCommand extends CommandBase {
  
  private long delayTime;

  /**
   * Creates a new DelayCommand. Used in command groups to put a pause in between commands.
   * @param delayTime Time to delay in seconds
   */
  //Delay time in seconds
  public DelayCommand(double delayTime) {
    //Convert to milliseconds
    this.delayTime = (long) Math.floor(delayTime * 1000);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    try {
      Thread.sleep(delayTime);
    } catch(Exception ex) {}
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
    return true;
  }
}
