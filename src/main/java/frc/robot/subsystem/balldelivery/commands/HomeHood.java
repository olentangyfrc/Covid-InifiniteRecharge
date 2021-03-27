package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class HomeHood extends CommandBase {
    private BallDelivery ballDelivery;
    private boolean stop;
    private static Logger logger = Logger.getLogger(HomeHood.class.getName());

    public HomeHood(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates HomeHood");
    }

    @Override
    public void initialize(){
        logger.info("starts HomeHood");
        stop = false;
    }

    public void execute(){
        ballDelivery.homeHood();
    }

    @Override
    public void end(boolean interrupted) {
      stop = true;
    }

    @Override
    public boolean isFinished(){
        return ballDelivery.isHoodLimitSwitchHit();
    }
}
