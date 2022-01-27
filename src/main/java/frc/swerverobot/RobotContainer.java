package frc.swerverobot;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.swerverobot.commands.DriveCommand;
import frc.swerverobot.commands.BasicDriveCommand;
import frc.swerverobot.commands.DriveWithSetRotationCommand;
import frc.swerverobot.commands.SquareCommand;
import frc.swerverobot.subsystems.ClimberSubsystem;
import frc.swerverobot.subsystems.DrivetrainSubsystem;
import frc.swerverobot.subsystems.IntakeSubsystem;
import frc.swerverobot.subsystems.ShooterSubsystem;

import java.util.function.DoubleSupplier;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;


public class RobotContainer {
    private final Controller controller = new XboxController(0);

    private final DrivetrainSubsystem drivetrain = new DrivetrainSubsystem();
    private final IntakeSubsystem intake = new IntakeSubsystem();
    private final ShooterSubsystem shooter = new ShooterSubsystem();
    private final ClimberSubsystem climber = new ClimberSubsystem();


    private final Vector2 vector0 = new Vector2(1, 0);
    private final Vector2 vector1 = new Vector2(0, 1);

    private final UpdateManager updateManager = new UpdateManager(
            drivetrain
//            shooter
    );


    public RobotContainer() {

/*        if (controller.getRightXAxis().get(true) == 0) {
        CommandScheduler.getInstance().setDefaultCommand(drivetrain, new DriveWithSetRotationCommand(
                drivetrain,
                () -> controller.getLeftXAxis().get(true),
                () -> -controller.getLeftYAxis().get(true),
                0.0
        ));
        }
        else {*/
        CommandScheduler.getInstance().setDefaultCommand(drivetrain, new DriveCommand(
                drivetrain,
                () -> controller.getLeftXAxis().get(true),
                () -> -controller.getLeftYAxis().get(true),
                () -> controller.getRightXAxis().get(true)
        ));
//        }


        /*        CommandScheduler.getInstance().setDefaultCommand(drivetrain, new DriveCommand(
            drivetrain, 
            () -> 0.1, 
            () -> 0, 
            () -> 0.1));
*/
        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        controller.getBackButton().whenPressed(
                () -> drivetrain.resetGyroAngle(Rotation2.ZERO)
        );
        controller.getAButton().whenPressed(
                () -> drivetrain.drive(vector0, 0, false)
        );
        controller.getBButton().whenPressed(
                new SquareCommand(drivetrain, 0.4, 1)
        );
    }
}
