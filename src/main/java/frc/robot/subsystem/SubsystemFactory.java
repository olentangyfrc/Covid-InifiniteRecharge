package frc.robot.subsystem;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

import frc.common.drivers.Gyroscope;
import frc.common.drivers.NavX;

import frc.robot.OI;

import frc.robot.subsystem.FootballPlayground.FootballPlayground;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.robot.subsystem.balldelivery.commands.DeliverBall;
import frc.robot.subsystem.balldelivery.commands.PutHoodDown;
import frc.robot.subsystem.balldelivery.commands.ShootZone;
import frc.robot.subsystem.balldelivery.commands.StopDelivery;
import frc.robot.subsystem.climber.Climber;
import frc.robot.subsystem.controlpanel.ControlPanel;
import frc.robot.subsystem.intake.Intake;
import frc.robot.subsystem.onewheelshooter.OneWheelShooter;
import frc.robot.subsystem.pixylinecam.PixyLineCam;
import frc.robot.subsystem.swerve.DrivetrainSubsystem2910;
import frc.robot.subsystem.swerve.commands.ToggleKeepSquare;
import frc.robot.subsystem.telemetry.Pigeon;
import frc.robot.subsystem.telemetry.Telemetry;
import frc.robot.subsystem.telemetry.commands.ZeroGyro;
import frc.robot.subsystem.transport.Transport;
import frc.robot.subsystem.winch.Winch;

public class SubsystemFactory {

    private static SubsystemFactory me;

    static Logger logger = Logger.getLogger(SubsystemFactory.class.getName());

    private static String botName;
    private HashMap<String, String> allMACs; // will contain mapping of MACs to Bot Names

    private static DisplayManager displayManager;

    private PowerDistributionPanel pdp;

    private Timer timer = new Timer();

    /**
     * keep all available subsystem declarations here.
     */

    private Transport transport;
    private ControlPanel controlPanel;
    private Climber climber;
    private OneWheelShooter oneWheelShooter;
    private Telemetry telemetry;
    private BallDelivery ballDelivery;
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
        allMACs.put("00:80:2F:30:DB:F8", "COVID"); // usb0
        allMACs.put("00:80:2F:30:DB:F9", "COVID"); // eth0
        allMACs.put("00:80:2F:28:64:39", "RIO99"); //usb0
        allMACs.put("00:80:2F:28:64:38", "RIO99"); //eth0
        allMACs.put("00:80:2F:27:04:C7", "RIO3"); //usb0 
        allMACs.put("00:80:2F:27:04:C6", "RIO3"); //eth0
        allMACs.put("00:80:2F:17:D7:4B", "RIO2"); //eth0
        allMACs.put("00:80:2F:17:D7:4C", "RIO2"); //usb0
        allMACs.put("00:80:2F:25:B4:CA", "CALIFORNIA"); //usb0
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
        //pdp = new PowerDistributionPanel(2);

        try {

            // Note that you should update this switch statement as you add bots to the list
            // above
            switch (botName) {
           
            case "RIO3":
                initRIO3(portMan);
                break;
            case "RIO2":
                initRIO2(portMan);
                break;
            case "RIO99":
                initRIO99(portMan);
                break;
            case "COVID":
                initCovid(portMan);
                break;
            case "CALIFORNIA":
                initCalifornia(portMan);
                break;
            default:
                initCovid(portMan); // default to football if we don't know better
            }

            initCommon(portMan);

        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 
     * init subsystems that are common to all bots
     * 
     */

    private void initCommon(PortMan portMan) {
    }

    public void initCovid(PortMan portMan) throws Exception {
        logger.info("initializing Covid");
        
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

        double flOff = -Math.toRadians(57.7);
        double frOff = -Math.toRadians(142.7);
        double blOff = -Math.toRadians(319);
        double brOff = -Math.toRadians(73);

        driveTrain = DrivetrainSubsystem2910.getInstance();
        driveTrain.init(portMan, canAssignments, flOff, blOff, frOff, brOff);

        ballDelivery = new BallDelivery();
        ballDelivery.init(portMan);

        displayManager.addBallDelivery(ballDelivery);
        
        OI.getInstance().bind(new DeliverBall(ballDelivery), OI.XboxMenu, OI.WhenPressed);
        OI.getInstance().bind(new StopDelivery(ballDelivery), OI.XboxMenu, OI.WhenReleased);
        
        OI.getInstance().bind(new ToggleKeepSquare(driveTrain), OI.XboxA, OI.WhenPressed);
        OI.getInstance().bind(new ZeroGyro(navX), OI.XboxY, OI.WhenPressed);
        

        //joystick buttons
        /*ShootBall cci = new ShootBall(ballDelivery);
        OI.getInstance().bind(cci, OI.LeftJoyButton7, OI.WhenPressed);

        ReverseShooter ccj = new ReverseShooter(ballDelivery);
        OI.getInstance().bind(ccj, OI.LeftJoyButton10, OI.WhenPressed);
        
        StopShooting cck = new StopShooting(ballDelivery);
        OI.getInstance().bind(cck, OI.LeftJoyButton11, OI.WhenPressed);

        EatBalls ccl = new EatBalls(ballDelivery);
        OI.getInstance().bind(ccl, OI.RightJoyButton11, OI.WhenPressed);

        StopEating ccm = new StopEating(ballDelivery);
        OI.getInstance().bind(ccm, OI.RightJoyButton10, OI.WhenPressed);

        SpitOutBalls ccn = new SpitOutBalls(ballDelivery);
        OI.getInstance().bind(ccn, OI.RightJoyButton3, OI.WhenPressed);
        
        SpinCarousel cco = new SpinCarousel(ballDelivery);
        OI.getInstance().bind(cco, OI.RightJoyButton6, OI.WhenPressed);

        AngleHood ccp = new AngleHood(ballDelivery);
        OI.getInstance().bind(ccp, OI.RightJoyButton4, OI.WhenPressed);

        StopCarousel ccq = new StopCarousel(ballDelivery);
        OI.getInstance().bind(ccq, OI.RightJoyButton7, OI.WhenPressed);

        StopAngling cct = new StopAngling(ballDelivery);
        OI.getInstance().bind(cct, OI.RightJoyButton5, OI.WhenPressed);
        */

        /*DeliverBall ccr = new DeliverBall(ballDelivery);
        OI.getInstance().bind(ccr, OI.LeftJoyButton6, OI.WhenPressed);
        
        StopDelivery ccs = new StopDelivery(ballDelivery);
        OI.getInstance().bind(ccs, OI.LeftJoyButton7, OI.WhenPressed);

        PutHoodDown ccu = new PutHoodDown(ballDelivery);
        OI.getInstance().bind(ccu, OI.LeftJoyButton11, OI.WhenPressed);

        //different ranges
        ShootZone ccv = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Green);
        OI.getInstance().bind(ccv, OI.RightJoyButton6, OI.WhenPressed);

        ShootZone ccw = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Yellow);
        OI.getInstance().bind(ccw, OI.RightJoyButton7, OI.WhenPressed);

        ShootZone ccx = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Blue);
        OI.getInstance().bind(ccx, OI.RightJoyButton11, OI.WhenPressed);

        ShootZone ccy = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Red);
        OI.getInstance().bind(ccy, OI.RightJoyButton10, OI.WhenPressed);*/
        

    }

    private void initRIO2(PortMan portMan) throws Exception {
        logger.info("Initializing RIO2");
        FootballPlayground footballPlayground = new FootballPlayground();
        footballPlayground.init(portMan);
        displayManager.addFootballPlayground(footballPlayground);
    }

    private void initRIO3(PortMan portMan ) throws Exception {

        logger.info("initializing RIO3");
        pigeon = new Pigeon(portMan.acquirePort(PortMan.can_21_label, "Pigeon"));

        HashMap<String, String> canAssignments = new HashMap<String, String>();
        canAssignments.put("FL.Swerve.angle", PortMan.can_09_label);
        canAssignments.put("FL.Swerve.drive", PortMan.can_07_label);

        canAssignments.put("FR.Swerve.angle", PortMan.can_03_label);
        canAssignments.put("FR.Swerve.drive", PortMan.can_62_label);

        canAssignments.put("BL.Swerve.angle", PortMan.can_61_label);
        canAssignments.put("BL.Swerve.drive", PortMan.can_11_label);

        canAssignments.put("BR.Swerve.angle", PortMan.can_58_label);
        canAssignments.put("BR.Swerve.drive", PortMan.can_06_label);

        double flOff = -Math.toRadians(1.1);
        double frOff = -Math.toRadians(311.24);
        double blOff = -Math.toRadians(119.6);
        double brOff = -Math.toRadians(262.9);

        driveTrain = DrivetrainSubsystem2910.getInstance();
        driveTrain.init(portMan, canAssignments, flOff, blOff, frOff, brOff);
    }

    private void initCalifornia(PortMan portMan) throws Exception {
        logger.info("Initializing CALIFORNIA");

        navX = new NavX(SPI.Port.kMXP);
        navX.calibrate();
        navX.setInverted(true);
        
        HashMap<String, String> canAssignments = new HashMap<String, String>();
        canAssignments.put("FL.Swerve.angle", PortMan.can_17_label);
        canAssignments.put("FL.Swerve.drive", PortMan.can_06_label);

        canAssignments.put("FR.Swerve.angle", PortMan.can_14_label);
        canAssignments.put("FR.Swerve.drive", PortMan.can_09_label);

        canAssignments.put("BL.Swerve.angle", PortMan.can_15_label);
        canAssignments.put("BL.Swerve.drive", PortMan.can_10_label);

        canAssignments.put("BR.Swerve.angle", PortMan.can_59_label);
        canAssignments.put("BR.Swerve.drive", PortMan.can_60_label);

        double flOff = -Math.toRadians(208.3);
        double frOff = -Math.toRadians(311.25);
        double blOff = -Math.toRadians(271.85);
        double brOff = -Math.toRadians(262.5);
      
        driveTrain  = DrivetrainSubsystem2910.getInstance();
        driveTrain.init(portMan, canAssignments, flOff, blOff, frOff, brOff);

        /**
         * All of the Telemery Stuff goes here
         */

        telemetry = new Telemetry();
        telemetry.init(portMan);
        displayManager.addTelemetry(telemetry);
        
        OI.getInstance().bind(new ToggleKeepSquare(driveTrain), OI.XboxA, OI.WhenPressed);
        OI.getInstance().bind(new ZeroGyro(navX), OI.XboxY, OI.WhenPressed);

        //SquareSelf ccc = new SquareSelf(telemetry, 2.34);
        //OI.getInstance().bind(ccc, OI.LeftJoyButton6, OI.WhenPressed);

        //GoToHorizontalDistance ccd= new GoToHorizontalDistance(telemetry, 2.34);
        //OI.getInstance().bind(ccd, OI.LeftJoyButton7, OI.WhenPressed);

        //GoToVerticalDistance cce = new GoToVerticalDistance(telemetry, 2.34);
        //OI.getInstance().bind(cce, OI.LeftJoyButton10, OI.WhenPressed);

        //DriveToBall ccf = new DriveToBall(telemetry);
        //OI.getInstance().bind(ccf, OI.RightJoyButton11, OI.WhileHeld);

        //RotateTowardsBall ccg = new RotateTowardsBall(telemetry);
        //OI.getInstance().bind(ccg, OI.LeftJoyButton11, OI.WhileHeld);

        /*ChaseBall cch = new ChaseBall(telemetry);
        OI.getInstance().bind(cch, OI.RightJoyButton10, OI.WhileHeld);
        */
        /**
        * shooter stuff goes here
        */
        ballDelivery = new BallDelivery();
        ballDelivery.init(portMan);
        displayManager.addBallDelivery(ballDelivery);

        OI.getInstance().bind(new DeliverBall(ballDelivery), OI.XboxMenu, OI.WhenPressed);
        OI.getInstance().bind(new StopDelivery(ballDelivery), OI.XboxMenu, OI.WhenReleased);

        //ShootBall cci = new ShootBall(ballDelivery);
        //OI.getInstance().bind(cci, OI.LeftJoyButton7, OI.WhenPressed);

        /*ReverseShooter ccj = new ReverseShooter(ballDelivery);
        OI.getInstance().bind(ccj, OI.LeftJoyButton10, OI.WhenPressed);
        
        StopShooting cck = new StopShooting(ballDelivery);
        OI.getInstance().bind(cck, OI.LeftJoyButton11, OI.WhenPressed);

        EatBalls ccl = new EatBalls(ballDelivery);
        OI.getInstance().bind(ccl, OI.RightJoyButton11, OI.WhenPressed);

        //StopEating ccm = new StopEating(ballDelivery);
        //OI.getInstance().bind(ccm, OI.RightJoyButton10, OI.WhenPressed);

        SpitOutBalls ccn = new SpitOutBalls(ballDelivery);
        OI.getInstance().bind(ccn, OI.RightJoyButton3, OI.WhenPressed);

        SpinCarousel cco = new SpinCarousel(ballDelivery);
        OI.getInstance().bind(cco, OI.RightJoyButton6, OI.WhenPressed);

        SetShootingZone ccp = new SetShootingZone(ballDelivery, BallDelivery.ShootingZone.Green);
        OI.getInstance().bind(ccp, OI.RightJoyButton4, OI.WhenPressed);

        StopCarousel ccq = new StopCarousel(ballDelivery);
        OI.getInstance().bind(ccq, OI.RightJoyButton7, OI.WhenPressed);

        DeliverBall ccr = new DeliverBall(ballDelivery);
        OI.getInstance().bind(ccr, OI.LeftJoyButton6, OI.WhenPressed);
        
        StopDelivery ccs = new StopDelivery(ballDelivery);
        OI.getInstance().bind(ccs, OI.LeftJoyButton7, OI.WhenPressed);
        */

        //different ranges
        /*ShootZone ccv = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Green);
        OI.getInstance().bind(ccv, OI.RightJoyButton6, OI.WhenPressed);

        ShootZone ccw = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Yellow);
        OI.getInstance().bind(ccw, OI.RightJoyButton7, OI.WhenPressed);

        ShootZone ccx = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Blue);
        OI.getInstance().bind(ccx, OI.RightJoyButton11, OI.WhenPressed);

        ShootZone ccy = new ShootZone(ballDelivery, BallDelivery.ShootingZone.Red);
        OI.getInstance().bind(ccy, OI.RightJoyButton10, OI.WhenPressed);

        DeliverBall cct = new DeliverBall(ballDelivery);
        OI.getInstance().bind(cct, OI.LeftJoyButton7, OI.WhenPressed);

        StopDelivery ccz = new StopDelivery(ballDelivery);
        OI.getInstance().bind(ccz, OI.LeftJoyButton6, OI.WhenPressed);*/
    }

    private void initRIO99(PortMan portMan) throws Exception {
        logger.info("Initializing RIO99");
        FootballPlayground footballPlayground = new FootballPlayground();
        footballPlayground.init(portMan);
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

    public BallDelivery getBallDelivery() {
        return ballDelivery;
    }

    public Timer getTimer() {
        return timer;
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