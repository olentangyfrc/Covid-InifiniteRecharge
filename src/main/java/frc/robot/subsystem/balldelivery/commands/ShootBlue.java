package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.robot.subsystem.InterstellarAccuracyAuton.commands.DelayCommand;

public class ShootBlue extends SequentialCommandGroup {
    private BallDelivery.ShootingZone zone = BallDelivery.ShootingZone.Blue;
    private BallDelivery ballDelivery;
    private static Logger logger = Logger.getLogger(ShootBlue.class.getName());
    
    public ShootBlue(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootBlue");

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, zone),
            //start the shooter
            new DeliverBall(ballDelivery)
        );
    }


}