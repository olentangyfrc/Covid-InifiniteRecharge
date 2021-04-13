/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem;

import java.util.logging.Logger;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.common.drivers.NavX;
import frc.common.math.MathUtils;
import frc.robot.OzoneException;

public class LEDStrip extends SubsystemBase {
  private static Logger logger = Logger.getLogger(LEDStrip.class.getName());
  private PortMan pm;

  private Spark LED;
  private double manualLightMode = Double.NaN;

  private final SendableChooser<LightMode> lightModeChooser = new SendableChooser<LightMode>();
  private NetworkTableEntry manualModeEntry;

  private static final double IDLE_SWAP = 0.53;
  private static final double RGB = -0.99;
  private static final double GREEN_COLOR = 0.77;

  private static final double VELOCITY_TOL = 5;

  /**
   * Creates a new LEDStrip on pwm port 0
   */
  public LEDStrip(PortMan pm) throws OzoneException{
    this.pm = pm;
  }

  /**
   * Initialize the LEDStrip
   * @throws OzoneException issue with the port
   */
  public void init() throws OzoneException {
    lightModeChooser.setDefaultOption("Static", LightMode.STATIC);
    lightModeChooser.addOption("Movement Indicator", LightMode.MOVEMENT_INDICATOR);
    Shuffleboard.getTab("LED").add(lightModeChooser);

    manualModeEntry = Shuffleboard.getTab("LED").add("Manual Light Mode", Double.NaN).getEntry();

    LED = new Spark(pm.acquirePort(PortMan.pwm0_label, "5v LED Strip"));
  }

  @Override
  public void periodic() {
    if(manualModeEntry.getDouble(Double.NaN) != manualLightMode && lightModeChooser.getSelected() == LightMode.STATIC) {
      setLightMode(manualModeEntry.getDouble(Double.NaN));
      manualLightMode = manualModeEntry.getDouble(Double.NaN);
    } else if(lightModeChooser.getSelected() == LightMode.MOVEMENT_INDICATOR && SubsystemFactory.getInstance().getGyro() != null) {
      NavX navx = (NavX) SubsystemFactory.getInstance().getGyro();
      if(Math.abs(navx.getYVelocity()) > 5 || Math.abs(navx.getXVelocity()) > 5 || Math.abs(navx.getRate()) > 5) {
        setLightMode(GREEN_COLOR);
      } else {
        setLightMode(0);
      }
    }
  }

  /**
   * Set the light mode of the LED
   * for modes, go to https://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf
   * @param value value to set the LED to. Should be between -1 and 1.
   */
  public void setLightMode(double value) {
    LED.set(MathUtils.clamp(value, -1, 1));
  }

  /**
   * The modes that the LED can be set to
   */
  public static enum LightMode {
    /**
     * Used to simply use one of the default modes
     */
    STATIC,
    /**
     * Have the lights react based on movement of the drivetrain.
     */
    MOVEMENT_INDICATOR,
  }
}
