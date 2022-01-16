package frc.robot.subsystem.swerve;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.common.commands.HolonomicDriveCommand;
import frc.common.drivers.Mk2SwerveModule;
import frc.common.drivers.Mk2SwerveModuleBuilder;
import frc.common.control.*;
import frc.common.drivers.Gyroscope;
import frc.common.drivers.SwerveModule;
import frc.common.drivers.NavX.Axis;
import frc.common.math.RigidTransform2;
import frc.common.math.Rotation2;
import frc.common.math.Vector2;
import frc.common.subsystems.SwerveDrivetrain;
import frc.common.util.DrivetrainFeedforwardConstants;
import frc.common.util.HolonomicDriveSignal;
import frc.common.util.HolonomicFeedforward;
import frc.robot.subsystem.SubsystemFactory;
import frc.robot.subsystem.telemetry.Pigeon;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import frc.robot.OzoneException;
import frc.robot.subsystem.PortMan;

public class DrivetrainSubsystem2910 extends SwerveDrivetrain {

    private static final double TRACKWIDTH = 23.5;
    private static final double WHEELBASE = 23.5;

    private static final double MAX_VELOCITY = 12.0 * 12.0;

    static Logger logger = Logger.getLogger(DrivetrainSubsystem2910.class.getName());

    private static final PidConstants SNAP_ROTATION_CONSTANTS = new PidConstants(0.5, 0.0, 0.0);
    private PidController snapRotationController = new PidController(SNAP_ROTATION_CONSTANTS);
    private double snapRotation = Double.NaN;

    private Gyroscope gyro = SubsystemFactory.getInstance().getGyro();

    private SwerveModule frontLeftModule;
    private SwerveModule frontRightModule;
    private SwerveModule backLeftModule;
    private SwerveModule backRightModule;

    private PortMan pm;

    private boolean keepSquare = false;

    private static final PidConstants FOLLOWER_TRANSLATION_CONSTANTS = new PidConstants(0.05, 0.01, 0.0);
    private static final PidConstants FOLLOWER_ROTATION_CONSTANTS = new PidConstants(1, 0.01, 0.0);
    private static final HolonomicFeedforward FOLLOWER_FEEDFORWARD_CONSTANTS = new HolonomicFeedforward(
            new DrivetrainFeedforwardConstants(1.0 / (14.0 * 12.0), 0.0, 0.0)
    );

    public static final ITrajectoryConstraint[] CONSTRAINTS = {
            new MaxVelocityConstraint(MAX_VELOCITY * 0.3),
            new MaxAccelerationConstraint(13.0 * 12.0),
            new CentripetalAccelerationConstraint(25.0 * 12.0)
    };
    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            new Translation2d(TRACKWIDTH / 2.0, WHEELBASE / 2.0),
            new Translation2d(TRACKWIDTH / 2.0, -WHEELBASE / 2.0),
            new Translation2d(-TRACKWIDTH / 2.0, WHEELBASE / 2.0),
            new Translation2d(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0)
    );
    //Old bot offsets
    /*
    private static final double FRONT_LEFT_ANGLE_OFFSET = -Math.toRadians(1.1);
    private static final double FRONT_RIGHT_ANGLE_OFFSET = -Math.toRadians(311.24);
    private static final double BACK_LEFT_ANGLE_OFFSET = -Math.toRadians(119.6);
    private static final double BACK_RIGHT_ANGLE_OFFSET = -Math.toRadians(262.9);
    */
    private double FRONT_LEFT_ANGLE_OFFSET ;
    private double FRONT_RIGHT_ANGLE_OFFSET;
    private double BACK_LEFT_ANGLE_OFFSET;
    private double BACK_RIGHT_ANGLE_OFFSET;

    private static DrivetrainSubsystem2910 instance;

    private SwerveModule[] swerveModules;

    private HolonomicMotionProfiledTrajectoryFollower follower = new HolonomicMotionProfiledTrajectoryFollower(
            FOLLOWER_TRANSLATION_CONSTANTS,
            FOLLOWER_ROTATION_CONSTANTS,
            FOLLOWER_FEEDFORWARD_CONSTANTS
    );

    private double lastTimestamp = 0;

    private final Object lock = new Object();
    private HolonomicDriveSignal signal = new HolonomicDriveSignal(Vector2.ZERO, 0.0, false);
    private Trajectory.Segment segment = null;

    private DrivetrainSubsystem2910() throws Exception {
        pm = PortMan.getInstance();
    }

    public void setSnapRotation(double snapRotation) {
        synchronized (lock) {
            this.snapRotation = snapRotation;
        }
    }

    public void stopSnap() {
        synchronized (lock) {
            this.snapRotation = Double.NaN;
        }
    }

    @Override
    public void holonomicDrive(Vector2 translation, double rotation, boolean fieldOriented) {
        synchronized (lock) {
            this.signal = new HolonomicDriveSignal(translation, rotation, fieldOriented);
        }
    }
    public void holonomicDrive(HolonomicDriveSignal sig) {
        synchronized (lock) {
            this.signal = sig;
        }
    }

    @Override
    public synchronized void updateKinematics(double timestamp) {
        super.updateKinematics(timestamp);

        double dt = timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        double localSnapRotation;
        synchronized (lock) {
            localSnapRotation = snapRotation;
        }

        RigidTransform2 currentPose = new RigidTransform2(
                new Vector2(getKinematicPosition().x * 0.941, getKinematicPosition().y * 0.941),
                Rotation2.fromDegrees(getGyroscope().getAngle().toDegrees())
        );

        Optional<HolonomicDriveSignal> optSignal = follower.update(currentPose, getKinematicVelocity(), gyro.getRate(),
                timestamp, dt);
        HolonomicDriveSignal localSignal;

        if (optSignal.isPresent()) {
            localSignal = optSignal.get();

            synchronized (lock) {
                segment = follower.getLastSegment();
            }
        } else {
            synchronized (lock) {
                localSignal = this.signal;
            }
        }
        if(!keepSquare) {
            if (Math.abs(localSignal.getRotation()) < 0.1 && Double.isFinite(localSnapRotation)) {
                snapRotationController.setSetpoint(localSnapRotation);
    
                localSignal = new HolonomicDriveSignal(localSignal.getTranslation(),
                        snapRotationController.calculate(getGyroscope().getAngle().toRadians(), dt),
                        localSignal.isFieldOriented());
            } else {
                synchronized (lock) {
                    snapRotation = Double.NaN;
                }
            }
        } else {
            localSignal = new HolonomicDriveSignal(localSignal.getTranslation(),
                        snapRotationController.calculate(getGyroscope().getAngle().toRadians(), dt),
                        localSignal.isFieldOriented());
        }
        //logger.log(Level.INFO, "Rotation point: [" + snapRotationController.getSetpoint() + "]");
        drive(new Translation2d(localSignal.getTranslation().x, localSignal.getTranslation().y), localSignal.getRotation(), localSignal.isFieldOriented());
        outputToSmartDashboard();
    }

    @Override
    public void outputToSmartDashboard() {
        super.outputToSmartDashboard();

        HolonomicDriveSignal localSignal;
        Trajectory.Segment localSegment;
        synchronized (lock) {
            localSignal = signal;
            localSegment = segment;
        }

        SmartDashboard.putNumber("Gyro Angle", gyro.getAngle().toDegrees());
        SmartDashboard.putNumber("Drivetrain Follower Forwards", localSignal.getTranslation().x);
        SmartDashboard.putNumber("Drivetrain Follower Strafe", localSignal.getTranslation().y);
        SmartDashboard.putNumber("Drivetrain Follower Rotation", localSignal.getRotation());
        SmartDashboard.putBoolean("Drivetrain Follower Field Oriented", localSignal.isFieldOriented());
        SmartDashboard.putNumber("Target Angle", Math.toDegrees(snapRotation));

        if (follower.getCurrentTrajectory().isPresent() && localSegment != null) {
            SmartDashboard.putNumber("Drivetrain Follower Target Angle", localSegment.rotation.toDegrees());

            Vector2 position = getKinematicPosition();

            SmartDashboard.putNumber("Drivetrain Follower X Error", localSegment.translation.x - position.x);
            SmartDashboard.putNumber("Drivetrain Follower Y Error", localSegment.translation.y - position.y);
            SmartDashboard.putNumber("Drivetrain Follower Angle Error", localSegment.rotation.toDegrees() - getGyroscope().getAngle().toDegrees());
        }

        for (SwerveModule module : swerveModules) {
            SmartDashboard.putNumber(String.format("%s Module Drive Current Draw", module.getName()), module.getDriveCurrent());
        }
    }

    public void init(PortMan pm, HashMap<String, String> canAssignments, double flOffset, double blOffset, double frOffset, double brOffset) throws OzoneException {

        FRONT_LEFT_ANGLE_OFFSET = flOffset;
        FRONT_RIGHT_ANGLE_OFFSET = frOffset;
        BACK_LEFT_ANGLE_OFFSET = blOffset;
        BACK_RIGHT_ANGLE_OFFSET = brOffset;

        frontLeftModule = new Mk2SwerveModuleBuilder(
            new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog0_label, "FL.Swerve.Encoder")), FRONT_LEFT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("FL.Swerve.angle"), "FL.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("FL.Swerve.drive"), "FL.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();

        frontRightModule = new Mk2SwerveModuleBuilder(
            new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog1_label, "FR.Swerve.Encoder")), FRONT_RIGHT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("FR.Swerve.angle"), "FR.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("FR.Swerve.drive"), "FR.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();
            
        backLeftModule = new Mk2SwerveModuleBuilder(
            new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog2_label, "BL.Swerve.Encoder")), BACK_LEFT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("BL.Swerve.angle"), "BL.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("BL.Swerve.drive"), "BL.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();

        backRightModule = new Mk2SwerveModuleBuilder(
            new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
            .angleEncoder(new AnalogInput(pm.acquirePort(PortMan.analog3_label, "BR.Swerve.Encoder")), BACK_RIGHT_ANGLE_OFFSET)
            .angleMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("BR.Swerve.angle"), "BR.Swerve.angle"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .driveMotor(new CANSparkMax(pm.acquirePort(canAssignments.get("BR.Swerve.drive"), "BR.Swerve.drive"), CANSparkMaxLowLevel.MotorType.kBrushless),
                    Mk2SwerveModuleBuilder.MotorType.NEO)
            .build();

        frontLeftModule.setName("Front Left");
        frontRightModule.setName("Front Right");
        backLeftModule.setName("Back Left");
        backRightModule.setName("Back Right");

        snapRotationController.setInputRange(0.0, 2.0 * Math.PI);
        snapRotationController.setContinuous(true);
        snapRotationController.setOutputRange(-0.5, 0.5);

        swerveModules = new SwerveModule[]{
                frontLeftModule,
                frontRightModule,
                backLeftModule,
                backRightModule,
        };
    }

    public static DrivetrainSubsystem2910 getInstance() {
        if(instance == null) {
            try {
                instance = new DrivetrainSubsystem2910();
                instance.setDefaultCommand(new HolonomicDriveCommand());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }

    @Override
    public SwerveModule[] getSwerveModules() {
        return swerveModules;
    }
    public void drive(Translation2d translation, double rotation, boolean fieldOriented) {

        rotation *= 2.0 / Math.hypot(WHEELBASE, TRACKWIDTH);
        ChassisSpeeds speeds;
        if (fieldOriented) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(translation.getX(), translation.getY(), rotation,
                    Rotation2d.fromDegrees(gyro.getAngle().toDegrees()));
        } else {
            speeds = new ChassisSpeeds(translation.getX(), translation.getY(), rotation);
        }
        //logger.log(Level.INFO, String.format("Forward:[%f] Strafe:[%f] Rotation:[%f]", translation.getX(), translation.getY(), rotation));
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
        frontLeftModule.setTargetVelocity(states[0].speedMetersPerSecond, states[0].angle.getRadians());
        frontRightModule.setTargetVelocity(states[1].speedMetersPerSecond, states[1].angle.getRadians());
        backLeftModule.setTargetVelocity(states[2].speedMetersPerSecond, states[2].angle.getRadians());
        backRightModule.setTargetVelocity(states[3].speedMetersPerSecond, states[3].angle.getRadians());
        if(frontRightModule.getCurrentVelocity() != 0) {
            logger.info("FR Velocity: " + frontRightModule.getCurrentVelocity());
        }
    }
    @Override
    public Gyroscope getGyroscope() {
        return gyro;
    }

    @Override
    public double getMaximumVelocity() {
        return 0;
    }

    @Override
    public double getMaximumAcceleration() {
        return 0;
    }

    @Override
    public void stop() {
        super.stop();
        synchronized (lock) {
            snapRotation = Double.NaN;
        }
    }

    public HolonomicMotionProfiledTrajectoryFollower getFollower() {
        return follower;
    }
    public void toggleKeepSquare() {
        if(!keepSquare) {
            logger.info("KEEP SQUARE ON");
            setSnapRotation(gyro.getAngle().toRadians());
            keepSquare = true;
        } else {
            logger.info("KEEP SQUARE OFF");
            stopSnap();
            keepSquare = false;
        }
        
    }
}
