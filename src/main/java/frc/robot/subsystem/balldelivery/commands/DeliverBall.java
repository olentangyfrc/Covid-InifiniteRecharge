package frc.robot.subsystem.balldelivery.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class DeliverBall extends SequentialCommandGroup {

    
    public DeliverBall(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates DeliverBall");
    }
}