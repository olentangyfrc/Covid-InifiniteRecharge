package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;

/**
 * stops the shooting process
 */

public class StopDelivery extends SequentialCommandGroup {
    private BallDelivery ballDelivery;
    private static Logger logger = Logger.getLogger(StopDelivery.class.getName());
    
    public StopDelivery(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates StopDelivery");

        addCommands(
            //stop the eater
            new StopEating(ballDelivery),
            //stop the shooter
            new StopShooting(ballDelivery)
        );
    }


}