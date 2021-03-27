package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.balldelivery.BallDelivery;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ShootZone extends SequentialCommandGroup {
    private BallDelivery.ShootingZone zone;
    private BallDelivery ballDelivery;
    private double hoodAdjustTime;
    private static Logger logger = Logger.getLogger(ShootZone.class.getName());
    
    public ShootZone(BallDelivery bd, BallDelivery.ShootingZone zone){
        this.zone = zone;
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates ShootBlue");

        switch(zone) {
            case Red: hoodAdjustTime = 0;
            break;
            case Blue: hoodAdjustTime = 0;
            break;
            case Yellow: hoodAdjustTime = 1000;
            break;
            case Green: hoodAdjustTime = 500;
            break;
            default: hoodAdjustTime = 0;
            break;
        }

        addCommands(
            //sets shooting zone
            new SetShootingZone(ballDelivery, zone),
            new WaitCommand(hoodAdjustTime),
            //start the shooter
            new DeliverBall(ballDelivery)
        );
    }


}