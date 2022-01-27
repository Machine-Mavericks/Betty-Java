package frc.swerverobot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.swerverobot.subsystems.DrivetrainSubsystem;
import org.frcteam2910.common.math.Vector2;

public class SquareCommand extends SequentialCommandGroup {
    public SquareCommand(DrivetrainSubsystem drivetrain, double speed, double dt) {
        addCommands(
            new BasicDriveCommand(drivetrain, new Vector2(-speed, 0), 0.0, true).withTimeout(dt),
            new BasicDriveCommand(drivetrain, new Vector2(0, -speed), 0.0, true).withTimeout(dt),
            new BasicDriveCommand(drivetrain, new Vector2(speed, 0), 0.0, true).withTimeout(dt),
            new BasicDriveCommand(drivetrain, new Vector2(0, speed), 0.0, true).withTimeout(dt)
        );
    }
}
