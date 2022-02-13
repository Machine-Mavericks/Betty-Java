package frc.swerverobot;

//Shooter Subsystem - Color Sensor
import edu.wpi.first.wpilibj.I2C;

public class RobotMap {
// define all ports and constants

//Shooter Subsytem
    public static final int TopMotorPort = 1; // The Number is the RIO PWM port
    public static final int BottomMotorPort = 2; // The Number is the RIO PWM port
    public static final int StorageMotorPort = 3; // The Number is the RIO PWM port
    public static final I2C.Port i2cPort = I2C.Port.kOnboard;


// EVEN numbers are angle motors, ODD numbers are drive motors
    public static final int DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_ENCODER = 2;
    public static final int DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_MOTOR = 5;
    public static final double DRIVETRAIN_FRONT_RIGHT_MODULE_ANGLE_OFFSET = -Math.toRadians(265);
    public static final int DRIVETRAIN_FRONT_RIGHT_MODULE_DRIVE_MOTOR = 6;

    public static final int DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_ENCODER = 0;
    public static final int DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_MOTOR = 1;
    public static final double DRIVETRAIN_BACK_RIGHT_MODULE_ANGLE_OFFSET = -Math.toRadians(182.7);
    public static final int DRIVETRAIN_BACK_RIGHT_MODULE_DRIVE_MOTOR = 2;

    public static final int DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_ENCODER = 3;
    public static final int DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_MOTOR = 7;
    public static final double DRIVETRAIN_FRONT_LEFT_MODULE_ANGLE_OFFSET = -Math.toRadians(176.5);
    public static final int DRIVETRAIN_FRONT_LEFT_MODULE_DRIVE_MOTOR = 8;

    public static final int DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_ENCODER = 1;
    public static final int DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_MOTOR = 3;
    public static final double DRIVETRAIN_BACK_LEFT_MODULE_ANGLE_OFFSET = -Math.toRadians(179.5);
    public static final int DRIVETRAIN_BACK_LEFT_MODULE_DRIVE_MOTOR = 4;
}
