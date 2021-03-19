package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ShootRed extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private BallDelivery.ShootingZone zone = BallDelivery.ShootingZone.Red;
    private static Logger logger = Logger.getLogger(ShootRed.class.getName());
    
    public ShootRed(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootRed");

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, zone),
            //start the shooter
            new ShootBall(ballDelivery),
            //start the eater
            new EatBalls(ballDelivery),
            //spin the carousel
            new SpinCarousel(ballDelivery)
        );
    }


}