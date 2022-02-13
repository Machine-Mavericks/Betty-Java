package frc.swerverobot;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.swerverobot.commands.DriveCommand;
import frc.swerverobot.subsystems.DrivetrainSubsystem;


public class RobotContainer {
    private final Controller controller = new XboxController(0);

    private final DrivetrainSubsystem drivetrain = new DrivetrainSubsystem();

    private final UpdateManager updateManager = new UpdateManager(
            drivetrain
    );


    public RobotContainer() {

        // set the drivetrain's default command to the driver's controller values
        CommandScheduler.getInstance().setDefaultCommand(drivetrain, new DriveCommand(
                drivetrain,
                () -> -controller.getLeftXAxis().get(true)*0.75,
                () -> controller.getLeftYAxis().get(true)*0.75,
                () -> controller.getRightXAxis().get(true)*0.5,
                () -> controller.getRightBumperButton().get()
        ));


        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // reset gyro angle
        controller.getBackButton().whenPressed(
                () -> drivetrain.resetGyroAngle(Rotation2.ZERO)
        );
    }
}
