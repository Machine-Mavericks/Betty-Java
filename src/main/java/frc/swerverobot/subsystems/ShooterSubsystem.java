package frc.swerverobot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.frcteam2910.common.robot.UpdateManager;

//Robot Map
import static frc.swerverobot.RobotMap.*;

//Motors - Motor/Controller Type Not Decided Yet
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.Jaguar;

//Color Sensor
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;

/*      ----Notes----
- Motors
    - Prototype used 2 on the front and one in the back to hold the ball
- How are balls stored?
    - Held away from shooter by a motor behind shooter
- Distance to target/machine vision?
- Autoaim?

- Notes from Prototype Testing
- With Current Motors and angle of ~30 degrees power of 0.6T0.6B works well to get it into the goal from distance
- From Close up adding backspin helps
- Assuming no added force from storage system (The 3rd motor at the end adds speed) 0.5 second wait time before wheels back up to speed
*/



/*      ----Electronics Needed----
- Motors (3)
    - 2 for shooter
    - 1 for storage
- Motor Controllers (3)
    - 1 for each motor in the shooter
- Machine Vision
    - Camera 
    - Rasberry Pie
    - Detect high goal for auto aim
    - Distance Maybe?
        - If we can get reliable distance from Machine Vision relace distance sensor with Machine Vision.
- Distnace Sensor (Shooting Process #2,3) (Ultrasonic maybe?) (Might be replaced with Machine Vision)
    - Detect distance to hub for shooting processs #2 and #3
    - Might need to be aimed upwards because the bottom section is not uniform maybe aim for middle of the lower hoop from edge of the field?
- Color Sensor (2)
    - Detect ball color and count
- PID Controller (Maybe)
    - Keep flywheels at constant speed
    - Might not be needed
*/



/*      ----Driver Interaction----
- Button to Activate Shooting Process #1 (Shoot Balls)
- Button to Activate Shooting Process #2 in either high or low (Aim Assist)
- Button to Activate Shooting Process #3 in either high or low (Auto Aim & Shoot)
- 2 Buttons to change aiming distance manually
*/



/*      ----Processes----
- Shooting Process #1 (No Aim Assist & 2 Balls)
    - Driver Preses Shoot Button or Activated by code (Start Function)
    - Spin bottom & top Motors to variables (This value can be changed by the driver manually or buy the aim program)
    - Wait ~0.5 Seconds
    - Activate Storage Motor to release first Ball (20% power worked in testing)
    - Wait ~0.1 Seconds
    - Stop Storage Motor
    - Use Color Sensor to detect if there is still a ball in the first slot
    - If there is a ball
        - Wait for wheels to regain lost speed ~0.5s
        - Turn Storage Motor to release second Ball
        - Wait for ball to exit shooter ~0.1s
    - Stop both intake motors
    - End
    
- Shooting Process #2 (Aim Assist & 2 Balls)
    - Driver should position robot roughly aming at the hub before running program
    - Driver Preses Aim Button or Activated by code high or low (Maybe a button combination for the low goal) (Start Function)
    - Machiene vision to find the reflective tape on high goal
    - Rotate Robot to face hub
    - Wait Until facing hub
    - Use Distance sensor or Machine Vision to detect distance to hub 
    - If high goal
        - Ajust power of top and bottom motor to get required distance (Using high goal Calibration)
        - If distance is not possible
            - Notify Driver (Maybe a beep or just something on the HUD)
    - else
        - Ajust power of top and bottom motor to get required distance (Using low goal Calibration)
        - If distance is not possible
            - Notify Driver (Maybe a beep or just something on the HUD)
    - End
    
- Shooting Process #3 (Auto Aim/Auto Shoot)
    - Driver should position robot roughly aming at the hub before running program
    - Driver Preses Auto Shoot Button for either high or low (Maybe a button combination for the low goal)
    - Run Shooting Process #2 (Input High Low Goal)
    - Wait until #2 finished
    - Run Shooting Process #1 
    - End
*/


public class ShooterSubsystem extends SubsystemBase{
    float bottomMotorPower = 0;
    float topMotorPower = 0;

    //Shooter Motors
    Jaguar topShooterMotor = new Jaguar(TopMotorPort);// Value from Robot Map
    Jaguar bottomShooterMotor = new Jaguar(BottomMotorPort);  // The Number is the RIO PWM port
    Jaguar storageMotor = new Jaguar(StorageMotorPort);  // The Number is the RIO PWM port
    
    //      ----Shooting Functions----
    public Boolean shootingProcess1() {
        //No Aim Assist

        //Set Top Motor to topMotorPower
        topShooterMotor.set(topMotorPower);
        //Set Bottom Motor to bottomMotorPower
        bottomShooterMotor.set(bottomMotorPower);

        Thread.sleep(500);//Wait 0.5 Seconds = 500 MS
        
        //Set Storage Motor to 0.2(20%)(Releases first ball)
        storageMotor.set(0.2);

        Thread.sleep(100);//Wait 0.1 Seconds = 100 MS

        //Set Storage Motor to 0
        storageMotor.stopMotor();

        //Detect if there is a second ball
        if (ballCount() < 1) {
            Thread.sleep(500);//Wait 0.5 Seconds = 500 MS

            //Set Storage Motor to 0.2(20%)
            storageMotor.set(0.2);
            
            Thread.sleep(100);//Wait 0.1 Seconds = 100 MS
        }
        //Stop All Motors
        storageMotor.stopMotor();
        topShooterMotor.stopMotor();
        bottomShooterMotor.stopMotor();
    }
    public Boolean shootingProcess2(Boolean highLow) {
        //Aim Assist (No Shoot)
        
        //Machine vision to find reflective tape on high goal
        //Rotate Robot to face hub
        //Wait Until Facing Hub
        if (highLow) {
            //true = High Goal

            //TODO: When Shooter Built Get Values for Distances

            //Set bottomMotorPower & topMotorPower variables to needed to get into high goal
            if (distanceFront() > 3) {
                bottomMotorPower = 0.3f;
                topMotorPower = 0.3f;
                //Return true to signal program completion
                return true;
            }
            else if (distanceFront() > 5) {
                bottomMotorPower = 0.5f;
                topMotorPower = 0.5f;
                //Return true to signal program completion
                return true;
            }
            else if (distanceFront() > 10) {
                //Out of Bounds
                //Set Motors to max just incase driver needs to shoot
                bottomMotorPower = 1f;
                topMotorPower = 1f;
                //Return false to notify driver of error
                return false;
            } 
        }
        else {
            //false = Low Goal

            //TODO: When Shooter Built Get Values for Distances

            //Set bottomMotorPower & topMotorPower variables to needed to get into low goal
            if (distanceFront() > 3) {
                bottomMotorPower = 0.2f;
                topMotorPower = 0.2f;
                //Return true to signal program completion
                return true;
            }
            else if (distanceFront() > 5) {
                bottomMotorPower = 0.3f;
                topMotorPower = 0.3f;
                //Return true to signal program completion
                return true;
            }
            else if (distanceFront() > 10) {
                //Out of Bounds
                //Set Motors to max just incase driver needs to shoot
                bottomMotorPower = 1f;
                topMotorPower = 1f;
                //Return false to notify driver of error
                return false;
            } 
        }
    }
    public Boolean shootingProcess3(Boolean highLow) {
        //Run ShootingProcess2 with highlow boolean to aim the robot
        if (shootingProcess2(highLow) != false) {
            //Run ShootingProcess1 to shoot all the balls in the robot after done aiming
            shootingProcess1();
        }
    }

    //      ----Distance sensor----
    public int distanceFront() {
        //Get distance infront of robot
        int distance = 5;
        return distance;
    }

    //      ----Color Sensor Functions----
    public final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);//TODO: Fix when know sensor port for color sensor
    public final ColorSensorV3 colorSensor2 = new ColorSensorV3(i2cPort);//TODO: Fix when know sensor port for color sensor

    public int ballCount() {
        //Detected Color from Color Sensor 1
        Color detectedColor = colorSensor.getColor();//Detected Color from first color sensor

        //Detected Color from Color Sensor 2
        Color detectedColor2 = colorSensor2.getColor();//Detected Color from first color sensor

        int ballCount = 0;//Starts the count of balls at 0

        //Look for first ball
        if (detectedColor.red > 1/*Red Min Value*/ && detectedColor.red < 5/*Red Max Value*/ || detectedColor.blue > 1/*Blue Min Value*/ && detectedColor.blue < 5/*Blue Max Value*/) {
            //1 Ball Found
            ballCount = 1;

            //Look for second ball
            if (detectedColor2.red > 1/*Red Min Value*/ && detectedColor2.red < 5/*Red Max Value*/ || detectedColor2.blue > 1/*Blue Min Value*/ && detectedColor2.blue < 5/*Blue Max Value*/) {
                //2 Balls foun
                
                
                ballCount = 2;
            } 
        }
        //No else statment because value is initalized at 0
        return ballCount;
    }
    public String ball1color() {
        String ballColor1 = "none";//Starts the first ball color at none
        
        //Detected Color from Color Sensor 1
        Color detectedColor = colorSensor.getColor();//Detected Color from first color sensor

        //Check ball color
        if (detectedColor.red > 1/*Red Min Value*/ && detectedColor.red < 5/*Red Max Value*/) {
            //Red Ball Found
            ballColor1 = "Red";
        }
        else if (detectedColor.blue > 1/*Blue Min Value*/ && detectedColor.blue < 5/*Blue Max Value*/) {
            //Red Ball Found
            ballColor1 = "Blue";
        }
        
        return ballColor1;
    }
    public String ball2color() {
        String ballColor2 = "none";//Starts the second ball color at none

        //Detected Color from Color Sensor 2
        Color detectedColor2 = colorSensor.getColor();//Detected Color from first color sensor

        //Check ball color
        if (detectedColor2.red > 1/*Red Min Value*/ && detectedColor2.red < 5/*Red Max Value*/) {
            //Red Ball Found
            ballColor2 = "Red";
        }
        else if (detectedColor2.blue > 1/*Blue Min Value*/ && detectedColor2.blue < 5/*Blue Max Value*/) {
            //Red Ball Found
            ballColor2 = "Blue";
        }
        
        return ballColor2;
    }
}
