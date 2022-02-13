package frc.swerverobot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.swerverobot.subsystems.DrivetrainSubsystem;
import org.frcteam2910.common.math.Vector2;

import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;

import java.util.function.DoubleSupplier;

public class DriveCommand extends CommandBase {
    private final DrivetrainSubsystem drivetrain;
    private final DoubleSupplier forward;
    private final DoubleSupplier strafe;
    private final DoubleSupplier rotation;

    // create a pid controller for rlearobot rotation
    private PidController rotationController = new PidController(new PidConstants(0.5, 0.0, 0.02));

    public DriveCommand(DrivetrainSubsystem drivetrain,
                        DoubleSupplier forward,
                        DoubleSupplier strafe,
                        DoubleSupplier rotation) {
        this.drivetrain = drivetrain;
        this.forward = forward;
        this.strafe = strafe;
        this.rotation = rotation;

        rotationController.setInputRange(0.0, 2*Math.PI);
        rotationController.setContinuous(true);

        addRequirements(drivetrain);
    }


    @Override
    public void initialize() {
        rotationController.reset();
        rotationController.setSetpoint(rotation.getAsDouble());
    }

    @Override
    public void execute() {

        // if the driver isn't rotating the robot, use pid to keep robot orientation constant (rotation = 0)
        if (rotation.getAsDouble() == 0) {
            double rotationOutput = rotationController.calculate(drivetrain.getPose().rotation.toRadians(), 0.02);

        // drive command, change values here to change robot speed/field oriented
            drivetrain.drive(
                    new Vector2(
                            forward.getAsDouble(),
                            strafe.getAsDouble()
                    ),
                    rotationOutput,
                    false
            );
        }

        // if the driver is rotating the robot, just get the rotation value and plug it into the .drive()
        else{
            drivetrain.drive(
                    new Vector2(
                            forward.getAsDouble(),
                            strafe.getAsDouble()
                    ),
                    rotation.getAsDouble(),
                    true
            );
        }

    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.drive(Vector2.ZERO, 0.0, false);
    }
}
