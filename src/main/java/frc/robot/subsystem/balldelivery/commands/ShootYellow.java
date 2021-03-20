package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class ShootYellow extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private BallDelivery.ShootingZone zone = BallDelivery.ShootingZone.Yellow;
    private static Logger logger = Logger.getLogger(ShootYellow.class.getName());
    
    public ShootYellow(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootYellow");

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, zone),
            //start the shooter
            new DeliverBall(ballDelivery)
        );
    }


}