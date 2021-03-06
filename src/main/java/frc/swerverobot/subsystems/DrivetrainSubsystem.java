package frc.swerverobot.subsystems;

import static frc.swerverobot.RobotMap.*;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.drivers.SwerveModule;
import org.frcteam2910.common.kinematics.ChassisVelocity;
import org.frcteam2910.common.kinematics.SwerveKinematics;
import org.frcteam2910.common.kinematics.SwerveOdometry;
import org.frcteam2910.common.math.RigidTransform2;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.drivers.Mk2SwerveModuleBuilder;
//import org.frcteam2910.common.robot.drivers.NavX;
import org.frcteam2910.common.util.HolonomicDriveSignal;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.swerverobot.drivers.NavX;

public class DrivetrainSubsystem extends SubsystemBase implements UpdateManager.Updatable {
    // define the trackwidth (short side in our case) and wheelbase (long side in our case) ratio of the robot
    private static final double TRACKWIDTH = 0.49;
    private static final double WHEELBASE = 0.49;

    // define the pid constants for each rotation motor
    private static final PidConstants rotation1 = new PidConstants(.5, 0.0, 0.0001);
    private static final PidConstants rotation2 = new PidConstants(.5, 0.0, 0.0001/*0.2*/);
    private static final PidConstants rotation3 = new PidConstants(.5, 0.0, 0.0001);
    private static final PidConstants rotation4 = new PidConstants(.5, 0.0, 0.0001);

    //
    // initialize each module using Mk2SwerveModuleBuilder
    //
            // check org/frcteam2910/common/robot/drivers/Mk2SwerveModuleBuilder
            // to see the initialization arguments
    private final SwerveModule frontLeftModule =
                new Mk2SwerveModuleBuilder(new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
                .angleEncoder(new AnalogInput(DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_ENCODER), DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_OFFSET)
                    .angleMotor(
                            new CANSparkMax(DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            rotation1, 18.0 / 1.0) 
                    .driveMotor(
                            new CANSparkMax(DRIVETRAIN_FRONT_LEFT_MODULE_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            Mk2SwerveModuleBuilder.MotorType.NEO)
                    .build();
    private final SwerveModule frontRightModule =
            new Mk2SwerveModuleBuilder(new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
                    .angleEncoder(new AnalogInput(DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_ENCODER), DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_OFFSET)
                    .angleMotor(
                            new CANSparkMax(DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            rotation2, 18.0/1.0) 
                    .driveMotor(
                            new CANSparkMax(DRIVETRAIN_FRONT_RIGHT_MODULE_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            Mk2SwerveModuleBuilder.MotorType.NEO)
                    .build();
    private final SwerveModule backLeftModule =
            new Mk2SwerveModuleBuilder(new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
                    .angleMotor(
                            new CANSparkMax(DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            rotation3, 18.0/1.0)
                    .angleEncoder(new AnalogInput(DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_ENCODER), DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_OFFSET)
                    .driveMotor(
                            new CANSparkMax(DRIVETRAIN_BACK_LEFT_MODULE_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            Mk2SwerveModuleBuilder.MotorType.NEO)
                    .build();
    private final SwerveModule backRightModule =
            new Mk2SwerveModuleBuilder(new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0))
                   .angleEncoder(
                           new AnalogInput(DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_ENCODER),
                           DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_OFFSET)
                    .angleMotor(
                            new CANSparkMax(DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            rotation4, 18.0/1.0)
                    .driveMotor(
                            new CANSparkMax(DRIVETRAIN_BACK_RIGHT_MODULE_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
                            Mk2SwerveModuleBuilder.MotorType.NEO)
                    .build();

    private final SwerveModule[] modules = {frontLeftModule, frontRightModule, backLeftModule, backRightModule};

    // set wheel positions
    private final SwerveKinematics kinematics = new SwerveKinematics(
            new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0), // Front Left
            new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0), // Front Right
            new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0), // Back Left
            new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0) // Back Right
    );
    private final SwerveOdometry odometry = new SwerveOdometry(kinematics, RigidTransform2.ZERO);

    private final Object sensorLock = new Object();
    @GuardedBy("sensorLock")
    //public final ADIS16448_IMU lil_navX = new ADIS16448_IMU();

//    public final ADIS16470_IMU adisGyro = new ADIS16470_IMU();

    public final NavX navX = new NavX();
//    private NetworkTableEntry t265Pitch;
        private double pitchZero;
        private NetworkTableEntry t265Pitch;

    private final Object kinematicsLock = new Object();
    @GuardedBy("kinematicsLock")
    private RigidTransform2 pose = RigidTransform2.ZERO;

    private final Object stateLock = new Object();
    @GuardedBy("stateLock")
    private HolonomicDriveSignal driveSignal = null;

    // Logging stuff
    private NetworkTableEntry poseXEntry;
    private NetworkTableEntry poseYEntry;
    private NetworkTableEntry poseAngleEntry;

    private NetworkTableEntry[] moduleAngleEntries = new NetworkTableEntry[modules.length];


// 
    public DrivetrainSubsystem() {
        synchronized (sensorLock) { 
//            navX.setInverted(true);
        }

        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");
        poseXEntry = tab.add("Pose X", 0.0)
                .withPosition(0, 0)
                .withSize(1, 1)
                .getEntry();
        poseYEntry = tab.add("Pose Y", 0.0)
                .withPosition(0, 1)
                .withSize(1, 1)
                .getEntry();
        poseAngleEntry = tab.add("Pose Angle", 0.0)
                .withPosition(0, 2)
                .withSize(1, 1)
                .getEntry();

        // this section adds each swerve module to the driver station's shuffleboard
        ShuffleboardLayout frontLeftModuleContainer = tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                .withPosition(1, 0)
                .withSize(2, 3);
        moduleAngleEntries[0] = frontLeftModuleContainer.add("Angle", 0.0).getEntry();

        ShuffleboardLayout frontRightModuleContainer = tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                .withPosition(3, 0)
                .withSize(2, 3);
        moduleAngleEntries[1] = frontRightModuleContainer.add("Angle", 0.0).getEntry();

        ShuffleboardLayout backLeftModuleContainer = tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                .withPosition(5, 0)
                .withSize(2, 3);
        moduleAngleEntries[2] = backLeftModuleContainer.add("Angle", 0.0).getEntry();

        ShuffleboardLayout backRightModuleContainer = tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                .withPosition(7, 0)
                .withSize(2, 3);
        moduleAngleEntries[3] = backRightModuleContainer.add("Angle", 0.0).getEntry();

        ShuffleboardTab t265Tab = Shuffleboard.getTab("T265");
        t265Pitch = t265Tab.add("Yaw", 0.0).getEntry();
}

    public RigidTransform2 getPose() {
        synchronized (kinematicsLock) {
            return pose;
        }
    }

    public void drive(Vector2 translationalVelocity, double rotationalVelocity, boolean fieldOriented) {
        synchronized (stateLock) {
            driveSignal = new HolonomicDriveSignal(translationalVelocity, rotationalVelocity, fieldOriented);
        }
    }

    public void resetGyroAngle(Rotation2 angle) {
        synchronized (sensorLock) {
            navX.calibrate();
//                adisGyro.calibrate();
        }
    }

    @Override
    public void update(double timestamp, double dt) {
        // updates the odometry and drive signal for the robot at time intervals of length dt
        updateOdometry(dt);

        HolonomicDriveSignal driveSignal;
        synchronized (stateLock) {
            driveSignal = this.driveSignal;
        }

        updateModules(driveSignal, dt);
    }

    private void updateOdometry(double dt) {
        // updates the module velocities at time intervals of length dt
        Vector2[] moduleVelocities = new Vector2[modules.length];
        for (int i = 0; i < modules.length; i++) {
            var module = modules[i];
            module.updateSensors();

            moduleVelocities[i] = Vector2.fromAngle(Rotation2.fromRadians(module.getCurrentAngle())).scale(module.getCurrentVelocity());
        }

        // updates robot angle based on gyro at intervals of length dt
        Rotation2 angle;
        synchronized (sensorLock) {
            angle = Rotation2.fromRadians(navX.getAxis(NavX.Axis.YAW));
        }

        RigidTransform2 pose = odometry.update(angle, dt, moduleVelocities); // set the "pose" to the robot's actual pose
        SmartDashboard.putNumber(String.format("gyro reading"), angle.toDegrees());     // put the gyro reading into the smartdashboard

        synchronized (kinematicsLock) {
            this.pose = pose;
        }
    }

    private void updateModules(HolonomicDriveSignal signal, double dt) {
        ChassisVelocity velocity;
        if (signal == null) {
            velocity = new ChassisVelocity(Vector2.ZERO, 0.0);
        } else if (signal.isFieldOriented()) {
        // if the robot is field oriented, shift translation to account for the rotation
                double rot = signal.getRotation() - getPose().rotation.toRadians();
                
            velocity = new ChassisVelocity(
                    signal.getTranslation().rotateBy(getPose().rotation.inverse()),
                    signal.getRotation()
            );
        } else {
        // if the robot is robot oriented, just get translation and rotation
            velocity = new ChassisVelocity(signal.getTranslation(), signal.getRotation());
        }

        Vector2[] moduleOutputs = kinematics.toModuleVelocities(velocity);
        SwerveKinematics.normalizeModuleVelocities(moduleOutputs, 1.0);

        for (int i = 0; i < modules.length; i++) {
            var module = modules[i];
            module.setTargetVelocity(moduleOutputs[i]);
            module.updateState(dt);
        }
    }

    @Override
    public void periodic() {
        // this function is called once per scheduler run
        var pose = getPose();
        poseXEntry.setDouble(pose.translation.x);
        poseYEntry.setDouble(pose.translation.y);
        poseAngleEntry.setDouble(pose.rotation.toDegrees());

        for (int i = 0; i < modules.length; i++) {
            var module = modules[i];
            moduleAngleEntries[i].setDouble(Math.toDegrees(module.getCurrentAngle()));
        }
    }
}
