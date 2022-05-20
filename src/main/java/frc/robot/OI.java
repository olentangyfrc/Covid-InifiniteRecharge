package frc.robot;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.subsystem.SubsystemFactory;
import frc.common.math.MathUtils;



/**
 * This is where we create all of out buttonsA and joysticks and set up the "UI"
 * of the bot for the drivers. You're gonna end up here a lot when people
 * complain about buttonsA needing to be changed
 */

public class OI {
    private static OI   me;

    private Joystick leftJoy;
    private Joystick rightJoy;
    private Joystick auxJoy;
    private GenericHID input;
    private Joystick leftButtonBox;
    private Joystick rightButtonBox;
    private XboxController xbox;  

    static Logger logger = Logger.getLogger(SubsystemFactory.class.getName());
    
    private double  deadzone    = 0.09;
    private double  scaleFactor = 1.2;

    private HashMap<Integer, String> allocatedJoyButtons = new HashMap<Integer, String>();

    private OI() {
        // private constructor to enforce Singleton pattern
    }

    static public OI getInstance() {
        if (me == null) {
            me= new OI();
            me.init();
        }

        return me;
    }

    public static final int LeftJoyButton1  = 1;
    public static final int LeftJoyButton2  = 2;
    public static final int LeftJoyButton3  = 3;
    public static final int LeftJoyButton4  = 4;
    public static final int LeftJoyButton5  = 5;
    public static final int LeftJoyButton6  = 6;
    public static final int LeftJoyButton7  = 7;
    public static final int LeftJoyButton8  = 8;
    public static final int LeftJoyButton9  = 9;
    public static final int LeftJoyButton10  = 10;
    public static final int LeftJoyButton11 = 11;

    public static final int RightJoyButton1  = 12;
    public static final int RightJoyButton2  = 13;
    public static final int RightJoyButton3  = 14;
    public static final int RightJoyButton4  = 15;
    public static final int RightJoyButton5  = 16;
    public static final int RightJoyButton6  = 17;
    public static final int RightJoyButton7  = 18;
    public static final int RightJoyButton8  = 19;
    public static final int RightJoyButton9  = 20;
    public static final int RightJoyButton10  = 21;
    public static final int RightJoyButton11  = 22;

    public static final int AuxJoyButton1  = 23;
    public static final int AuxJoyButton2  = 24;
    public static final int AuxJoyButton3  = 25;
    public static final int AuxJoyButton4  = 26;
    public static final int AuxJoyButton5  = 27;
    public static final int AuxJoyButton6  = 28;
    public static final int AuxJoyButton7  = 29;
    public static final int AuxJoyButton8  = 30;
    public static final int AuxJoyButton9  = 31;
    public static final int AuxJoyButton10  = 32;
    public static final int AuxJoyButton11  = 33;

    public static final int LeftButtonBox1 = 34;
    public static final int LeftButtonBox2 = 35;
    public static final int LeftButtonBox3 = 36; 
    public static final int LeftButtonBox4 = 37; 
    public static final int LeftButtonBox5 = 38; 
    public static final int LeftButtonBox6 = 39;  //comment
    public static final int LeftButtonBox7 = 40; 
    public static final int LeftButtonBox8 = 41;
    public static final int LeftButtonBox9 = 42; 
    public static final int LeftButtonBox10 = 43; 
    public static final int LeftButtonBox11 = 44;

    public static final int RightButtonBox1 = 45;
    public static final int RightButtonBox2 = 46; 
    public static final int RightButtonBox3 = 47; 
    public static final int RightButtonBox4 = 48; 
    public static final int RightButtonBox5 = 49; 
    public static final int RightButtonBox6 = 50; 
    public static final int RightButtonBox7 = 51;
    public static final int RightButtonBox8 = 52; 
    public static final int RightButtonBox9 = 53;
    public static final int RightButtonBox10 = 54;
    public static final int RightButtonBox11 = 55; 

    public static final int XboxA = 56;
    public static final int XboxB = 57;
    public static final int XboxX = 58;
    public static final int XboxY = 59;
    public static final int XboxLB = 60;
    public static final int XboxRB = 61;
    public static final int XboxView = 62;
    public static final int XboxMenu = 63;
    public static final int XboxLeftStick = 64;
    public static final int XboxRightStick = 65;

    public static final int WhenPressed         = 1;
    public static final int WhenReleased        = 2;
    public static final int WhileHeld           = 3;
    public static final int ToggleWhenPressed   = 4;
    public static final int CancelWhenPressed   = 5;

    private int leftJoyIndex = 1;
    private int rightJoyIndex   = 2;
    private int auxJoyIndex  = 2;
    private int leftButtonBoxIndex  = 3;
    private int rightButtonBoxIndex = 4;
    private int xboxIndex   = 0;

    private static final GenericHID.HIDType XBOX_TYPE = GenericHID.HIDType.kHIDGamepad;
    private static final GenericHID.HIDType JOYSTICK_TYPE = GenericHID.HIDType.kHIDJoystick;
    private static final GenericHID.HIDType UNKNOWN_TYPE = GenericHID.HIDType.kUnknown;
    
    private static final ArrayList<String> XBOX_NAMES = new ArrayList<String>();
    private static final ArrayList<String> JOYSTICK_NAMES = new ArrayList<String>();

    private GenericHID.HIDType inputType;

    public void init() {

        XBOX_NAMES.add("Controller (Xbox One For Windows)");
        XBOX_NAMES.add("Bluetooth XINPUT compatible input device");
        XBOX_NAMES.add("Wireless Controller");

        JOYSTICK_NAMES.add("Logitech Attack 3");

        if(DriverStation.isJoystickConnected(leftJoyIndex) && DriverStation.isJoystickConnected(rightJoyIndex)) {
            if(getJoystickType(leftJoyIndex) == JOYSTICK_TYPE && getJoystickType(rightJoyIndex) == JOYSTICK_TYPE) {
                inputType = JOYSTICK_TYPE;
                leftJoy = new Joystick(1);
                rightJoy = new Joystick(2);
            } else {
                inputType = UNKNOWN_TYPE;
                DriverStation.reportError("Incorrect Joystick format. Check Inputs.", false);
            }
        } else if(DriverStation.isJoystickConnected(xboxIndex)) {
            if(getJoystickType(xboxIndex) == XBOX_TYPE) {
                inputType = XBOX_TYPE;
                xbox = new XboxController(0);
            } else {
                inputType = UNKNOWN_TYPE;
                DriverStation.reportError("Incorrect Joystick format. Check Inputs.", false);
            }
        } else {
            inputType = UNKNOWN_TYPE;
            DriverStation.reportError("Incorrect Joystick format. Check Inputs.", false);
        }

    }

    public double getLeftXValue() {
        double value = 0;
        if(inputType == XBOX_TYPE) {
            value = xbox.getLeftX();
        } else if(inputType == JOYSTICK_TYPE) {
            value = leftJoy.getX();
        }
        
        return getFilteredValue(value);
    }

    public double getLeftYValue() {
        double value = 0;
        if(inputType == XBOX_TYPE) {
            value = xbox.getLeftY();
        } else if(inputType == JOYSTICK_TYPE) {
            value = leftJoy.getY();
        }
        
        return getFilteredValue(value);
    }

    public double getRightXValue() {
        double value = 0;
        if(inputType == XBOX_TYPE) {
            value = xbox.getRightX();
        } else if(inputType == JOYSTICK_TYPE) {
            value = rightJoy.getX();
        }
        
        return getFilteredValue(value);
    }

    public double getRightYValue() {
        double value = 0;
        if(inputType == XBOX_TYPE) {
            value = xbox.getRightY();
        } else if(inputType == JOYSTICK_TYPE) {
            value = rightJoy.getY();
        }
        
        return getFilteredValue(value);
    }
    
    public XboxController getXbox() {
        return xbox;
    }
    /**
     * this method binds a Command to a Joystick button for an action
     * @param c - the Command
     * @param button - which Joystick button to bind
     * @param action - the button action that invokes the Command
     */
    public void bind(Command c, int button, int action) throws OzoneException {
        GenericHID j;
        // see constants in this file LeftJoyButton1  = 1;
        // see constants in this file RightJoyButton1  = 11;
        // Joystick button values 1-10 are for left joystick
        // Joystick button values 11-20 are for right joystick
        
        if(allocatedJoyButtons.get(button) != null) {
            if(action == 2) {
                logger.info("ONLY OK BECAUSE THIS IS A WHEN RELEASED COMMAND");
            }
            else {
                throw new OzoneException((button >= 1 && button <= 11 ? "Left" : (button >= 12 && button <= 22) ? "Right" : (button >= 23 && button <= 33) ? "Aux" : "Buttons") +
                    " Joystick Button [" + (button >= 12 && button <= 21 ? (button-11) : button >= 23 && button <= 33 ? (button-22) : button >= 34 && button <= 44 ? (button-33) : button) + 
                    "] is already taken by [" + allocatedJoyButtons.get(button) + 
                    "] when asked for by [ " + c.getClass().getName() + "]");
                    //logger.log("MULTI BUTTON LINKAGE");
            }
        }
        
        allocatedJoyButtons.put(button, c.getClass().getName());

        if (button >= 1 && button <= 11) {
            j   = leftJoy;
        } else if (button >= 12 && button <= 22 ) {
            j   = rightJoy;
            button  -= 11; // adjust the actual button. joystick button ids start at 1
        } else if (button >= 23 && button <= 33) {
            j = auxJoy;
            button -= 22;
        }
        else if(button >= 34 && button <= 44){
            j = leftButtonBox;
            button -= 33;
        } 
        else if(button >= 45 && button <= 55){
            j = rightButtonBox;
            button -= 44;
        }
        else if(button >= 56 && button <= 65) {
            j = xbox;
            button -= 55;
        }
        else {
            throw new OzoneException ("Unrecognized joystick button [" + button + "]");
        }
		String []parts	= c.getClass().getName().split("\\.");
        logger.info("binding [" + parts[parts.length-1] + "] to joy[" + j.getPort() + "] b[" + button + "]");

        JoystickButton b = new JoystickButton(j, button);

        switch (action) {
            case OI.WhenPressed:
                b.whenPressed(c);
                break;
            case OI.WhenReleased:
                b.whenReleased(c);
                break;
            case OI.WhileHeld:
                b.whileHeld(c);
                break;
            case ToggleWhenPressed:
                b.toggleWhenPressed(c);
                break;
            case OI.CancelWhenPressed:
                b.cancelWhenPressed(c);
                break;
        }
    }

    /**
     * filters and scales a joytick value.
     * if value is too small, joystick will seem too sensitive.
     * apply a scaling factor to be able to adjust values
     * @param raw
     * @return
     */
    public double getFilteredValue(double raw)
    {
        if (Math.abs(raw) < deadzone) {
            return 0; 
        } else {
            return MathUtils.clamp(raw * (scaleFactor), -1, 1); // Set the output to a ceratin percent of of the input
        }
    }
    //Used for getting the actual assignment value of a button.
    public static int getButton(int button) {
        if (button >= 12 && button <= 22 ) {
            button  -= 11;
        } else if (button >= 23 && button <= 33) {
            button -= 22;
        }
        else if(button >= 34 && button <= 44){
            button -= 33;
        } 
        else if(button >= 45 && button <= 55){
            button -= 44;
        }
        else if(button >= 56 && button <= 65) {
            button -= 55;
        }
        return button;
    }
    public static GenericHID.HIDType getJoystickType(int port) {
        if(XBOX_NAMES.contains(DriverStation.getJoystickName(port))) {
            return GenericHID.HIDType.kHIDGamepad;
        } else if(JOYSTICK_NAMES.contains(DriverStation.getJoystickName(port))) {
            return GenericHID.HIDType.kHIDJoystick;
        } else {
            return GenericHID.HIDType.kUnknown;
        }
    }
}
