package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ShootBlue extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private static Logger logger = Logger.getLogger(ShootBlue.class.getName());
    
    public ShootBlue(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootBlue");

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, "Blue");
            //start the shooter
            new ShootBall(ballDelivery),
            //start the eater
            new EatBalls(ballDelivery),
            //spin the carousel
            new SpinCarousel(ballDelivery)
        );
    }


}