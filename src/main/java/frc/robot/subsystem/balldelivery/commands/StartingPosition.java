package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class StartingPosition extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private static Logger logger = Logger.getLogger(DeliverBall.class.getName());

    public StartingPosition(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates StartingPosition");

        addCommands(
            new HomeHood(ballDelivery)
            //new HomeCarousel(ballDelivery)
        );
    }
}
