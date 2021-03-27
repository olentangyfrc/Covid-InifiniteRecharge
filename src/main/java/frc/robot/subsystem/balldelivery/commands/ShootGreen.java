package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.DelayCommand;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ShootGreen extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private BallDelivery.ShootingZone zone = BallDelivery.ShootingZone.Green;
    private static Logger logger = Logger.getLogger(ShootGreen.class.getName());
    
    public ShootGreen(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootGreen");

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, zone),
            new DelayCommand(0.5),
            //start the shooter
            new DeliverBall(ballDelivery)
        );
    }


}