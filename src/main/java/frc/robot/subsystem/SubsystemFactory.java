package frc.robot.subsystem;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

import frc.robot.OI;
import frc.robot.OzoneException;
import frc.robot.subsystem.climber.Climber;
import frc.robot.subsystem.controlpanel.ControlPanel;
import frc.robot.subsystem.controlpanel.commands.RotateToColor;
import frc.robot.subsystem.controlpanel.commands.SpinManual;
import frc.robot.subsystem.controlpanel.commands.SpinRotations;
import frc.robot.subsystem.controlpanel.commands.SpinnerRetract;
import frc.robot.subsystem.controlpanel.commands.SpinnerUp;
import frc.robot.subsystem.controlpanel.commands.Stop;
import frc.robot.subsystem.intake.Intake;
import frc.robot.subsystem.intake.commands.IntakeDown;
import frc.robot.subsystem.intake.commands.IntakeSpinBack;
import frc.robot.subsystem.intake.commands.IntakeSpinForward;
import frc.robot.subsystem.intake.commands.IntakeStop;
import frc.robot.subsystem.intake.commands.IntakeUp;
import frc.robot.subsystem.telemetry.Pigeon;
import frc.robot.subsystem.telemetry.Telemetry;
import frc.robot.subsystem.telemetry.commands.ChaseBall;
import frc.robot.subsystem.telemetry.commands.DriveToBall;
import frc.robot.subsystem.telemetry.commands.GoToHorizontalDistance;
import frc.robot.subsystem.telemetry.commands.GoToVerticalDistance;
import frc.robot.subsystem.telemetry.commands.RotateTowardsBall;
import frc.robot.subsystem.telemetry.commands.SquareSelf;
import frc.robot.subsystem.onewheelshooter.OneWheelShooter;
import frc.robot.subsystem.winch.Winch;
import frc.robot.subsystem.winch.commands.WinchUp;
import frc.robot.subsystem.onewheelshooter.commands.OneWheelReverse;
import frc.robot.subsystem.onewheelshooter.commands.OneWheelShoot;
import frc.robot.subsystem.onewheelshooter.commands.OneWheelStop;
import frc.robot.subsystem.pixylinecam.PixyLineCam;
import frc.robot.subsystem.pixylinecam.commands.PollPixyLine;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.climber.commands.Climb;
import frc.robot.subsystem.climber.commands.ClimberControl;
import frc.robot.subsystem.climber.commands.ClimberControlBack;
import frc.robot.subsystem.climber.commands.ClimberRetract;
import frc.robot.subsystem.commandgroups.CollectionMode;
import frc.robot.subsystem.commandgroups.ControlPanelMode;
//import frc.robot.subsystem.commandgroups.ControlPanelMode;
import frc.robot.subsystem.commandgroups.MoveMode;
import frc.robot.subsystem.commandgroups.ScoreLowMode;
import frc.robot.subsystem.commandgroups.SpitBallsMode;
import frc.robot.subsystem.commandgroups.ScoreHighMode;
import frc.robot.subsystem.commandgroups.StartingConfiguration;
//import frc.robot.subsystem.commandgroups.StartingConfiguration;
import frc.robot.subsystem.transport.commands.ScoreLow;
import frc.robot.subsystem.transport.Transport;
import frc.robot.subsystem.transport.commands.*;
import frc.robot.subsystem.transport.commands.TakeIn;
import frc.robot.subsystem.transport.commands.StopTransport;
import frc.robot.subsystem.swerve.DrivetrainSubsystem;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.common.drivers.Gyroscope;
import frc.common.drivers.NavX;
import edu.wpi.first.wpilibj.SPI;

public class SubsystemFactory {

    private static SubsystemFactory me;

    static Logger logger = Logger.getLogger(SubsystemFactory.class.getName());

    private static String botName;
    private HashMap<String, String> allMACs; // will contain mapping of MACs to Bot Names

    private static DisplayManager displayManager;

    private PowerDistributionPanel pdp;

    /**
     * keep all available subsystem declarations here.
     */

    private Transport transport;
    private ControlPanel controlPanel;
    private Climber climber;
    private OneWheelShooter oneWheelShooter;
    private Telemetry telemetry;
    private PixyLineCam pixyLineCam;
    private DrivetrainSubsystem2910 driveTrain;
    private Intake intake;
    private Winch winch;
    private Pigeon pigeon;
    private NavX navX;
    
    
    private static ArrayList<SBInterface> subsystemInterfaceList;

    private SubsystemFactory() {
        // private constructor to enforce Singleton pattern
        botName = "unknown";
        allMACs = new HashMap<>();
        // add all the mappings from MACs to names here
        // as you add mappings here:
        // 1) update the select statement in the init method
        // 2) add the init method for that robot
        allMACs.put("00:80:2F:17:BD:76", "zombie"); // usb0
        allMACs.put("00:80:2F:17:BD:75", "zombie"); // eth0
        allMACs.put("00:80:2F:28:64:39", "plank"); //usb0
        allMACs.put("00:80:2F:28:64:38", "plank"); //eth0
        allMACs.put("00:80:2F:27:04:C7", "RIO3"); //usb0 
        allMACs.put("00:80:2F:27:04:C6", "RIO3"); //eth0
        allMACs.put("00:80:2F:17:D7:4B", "RIO2"); //eth0
        allMACs.put("00:80:2F:17:D7:4C", "RIO2"); //usb0
    }

    public static SubsystemFactory getInstance() {

        if (me == null) {
            me = new SubsystemFactory();
        }

        return me;
    }

    public void init(DisplayManager dm, PortMan portMan) throws Exception {

        logger.info("initializing");

        botName = getBotName();

        logger.info("Running on " + botName);

        displayManager = dm;
        subsystemInterfaceList = new ArrayList<SBInterface>();
        pdp = new PowerDistributionPanel(1);
        //botName = "RIO3";


        try {

            // Note that you should update this switch statement as you add bots to the list
            // above
            switch (botName) {
           
            case "RIO3":
                initRIO3(portMan);
                break;
            default:
                initCovid(portMan); // default to football if we don't know better
            }

            initCommon(portMan);

        } catch (Exception e) {
            throw e;
        }
    }
    public void initCovid(PortMan portMan) throws Exception {
        logger.info("initializing");
        navX = new NavX(SPI.Port.kMXP);
        navX.calibrate();
        navX.setInverted(true);
        
        HashMap<String, String> canAssignments = new HashMap<String, String>();
        canAssignments.put("FL.Swerve.angle", PortMan.can_35_label);
        canAssignments.put("FL.Swerve.drive", PortMan.can_34_label);

        canAssignments.put("FR.Swerve.angle", PortMan.can_32_label);
        canAssignments.put("FR.Swerve.drive", PortMan.can_33_label);

        canAssignments.put("BL.Swerve.angle", PortMan.can_36_label);
        canAssignments.put("BL.Swerve.drive", PortMan.can_37_label);

        canAssignments.put("BR.Swerve.angle", PortMan.can_31_label);
        canAssignments.put("BR.Swerve.drive", PortMan.can_30_label);

        driveTrain = DrivetrainSubsystem2910.getInstance();
        driveTrain.init(portMan, canAssignments);

    }

    /**
     * 
     * init subsystems that are common to all bots
     * 
     */

    private void initCommon(PortMan portMan) {
    }

    private void initRIO3(PortMan portMan ) throws Exception {

        logger.info("initializing");
        
        HashMap<String, String> canAssignments = new HashMap<String, String>();
        canAssignments.put("FL.Swerve.angle", PortMan.can_09_label);
        canAssignments.put("FL.Swerve.drive", PortMan.can_07_label);

        canAssignments.put("FR.Swerve.angle", PortMan.can_03_label);
        canAssignments.put("FR.Swerve.drive", PortMan.can_62_label);

        canAssignments.put("BL.Swerve.angle", PortMan.can_61_label);
        canAssignments.put("BL.Swerve.drive", PortMan.can_11_label);

        canAssignments.put("BR.Swerve.angle", PortMan.can_58_label);
        canAssignments.put("BR.Swerve.drive", PortMan.can_06_label);

        driveTrain  = DrivetrainSubsystem2910.getInstance();
        driveTrain.init(portMan, canAssignments);

        /**
         * All of the Telemery Stuff goes here
         */

        telemetry = new Telemetry();
        telemetry.init(portMan);
        displayManager.addTelemetry(telemetry);

        SquareSelf ccc = new SquareSelf(telemetry, 2.34);
        OI.getInstance().bind(ccc, OI.LeftJoyButton6, OI.WhenPressed);

        GoToHorizontalDistance ccd= new GoToHorizontalDistance(telemetry, 2.34);
        OI.getInstance().bind(ccd, OI.LeftJoyButton7, OI.WhenPressed);

        GoToVerticalDistance cce = new GoToVerticalDistance(telemetry, 2.34);
        OI.getInstance().bind(cce, OI.LeftJoyButton10, OI.WhenPressed);

        DriveToBall ccf = new DriveToBall(telemetry);
        OI.getInstance().bind(ccf, OI.RightJoyButton11, OI.WhileHeld);

        RotateTowardsBall ccg = new RotateTowardsBall(telemetry);
        OI.getInstance().bind(ccg, OI.LeftJoyButton11, OI.WhileHeld);

        ChaseBall cch = new ChaseBall(telemetry);
        OI.getInstance().bind(cch, OI.RightJoyButton10, OI.WhileHeld);

        /* DrivetrainSubsystem2910 changes
        frontLeftModule = new Mk2SwerveModuleBuilder(
            new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog0_label, "FL.Swerve.Encoder")), FRONT_LEFT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(PortMan.can_09_label, "FL.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(PortMan.can_07_label, "FL.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();

        frontRightModule = new Mk2SwerveModuleBuilder(
            new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog1_label, "FR.Swerve.Encoder")), FRONT_RIGHT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(PortMan.can_03_label, "FR.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(PortMan.can_62_label, "FR.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();
            
        backLeftModule = new Mk2SwerveModuleBuilder(
            new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog2_label, "BL.Swerve.Encoder")), BACK_LEFT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(PortMan.can_61_label, "BL.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(PortMan.can_11_label, "BL.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();

        backRightModule = new Mk2SwerveModuleBuilder(
            new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog3_label, "BR.Swerve.Encoder")), BACK_RIGHT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(PortMan.can_58_label, "BR.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(PortMan.can_06_label, "BR.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();
        */
    }
    /**
     * 
     * init subsystems specific to Football
     * 
     */

    private void initFootball(PortMan portMan) throws Exception {
        logger.info("Initializing Football");
        
    }

    private void initZombie(PortMan portMan) throws OzoneException {
        logger.info("Initializing Zombie");
    }

    private void initRio2(PortMan portMan) throws OzoneException {
        logger.info("Initializing RIO2");
    }

    public PowerDistributionPanel getPDP(){
        return pdp;
    }
    public ControlPanel getControlPanel() {
        return controlPanel;
    }
    public DrivetrainSubsystem2910 getDriveTrain(){
        return driveTrain;
    }
    public Climber getClimber() {
        return climber;
    }

    public Transport getTransport() {
        return transport;
    }
    public Intake getIntake(){
        return intake;
    }
    public OneWheelShooter getShooter(){
        return oneWheelShooter;
    }

    public Gyroscope getGyro() {
        if(pigeon != null) {
            return pigeon;
        } else {
            return navX;
        }
    }

    private String getBotName() throws Exception {

        Enumeration<NetworkInterface> networks;
            networks = NetworkInterface.getNetworkInterfaces();

            String activeMACs = "";
            for (NetworkInterface net : Collections.list(networks)) {
                String mac = formatMACAddress(net.getHardwareAddress());
                activeMACs += (mac+" ");
                logger.info("Network #"+net.getIndex()+" "+net.getName()+" "+mac);
                if (allMACs.containsKey(mac)) {
                    botName = allMACs.get(mac);
                    logger.info("   this MAC is for "+botName);
                }
            }

            return botName;
        }

    /**
     * Formats the byte array representing the mac address as more human-readable form
     * @param hardwareAddress byte array
     * @return string of hex bytes separated by colons
     */
    private String formatMACAddress(byte[] hardwareAddress) {
        if (hardwareAddress == null || hardwareAddress.length == 0) {
            return "";
        }
        StringBuilder mac = new StringBuilder(); // StringBuilder is a premature optimization here, but done as best practice
        for (int k=0;k<hardwareAddress.length;k++) {
            int i = hardwareAddress[k] & 0xFF;  // unsigned integer from byte
            String hex = Integer.toString(i,16);
            if (hex.length() == 1) {  // we want to make all bytes two hex digits 
                hex = "0"+hex;
            }
            mac.append(hex.toUpperCase());
            mac.append(":");
        }
        mac.setLength(mac.length()-1);  // trim off the trailing colon
        return mac.toString();
    }

}