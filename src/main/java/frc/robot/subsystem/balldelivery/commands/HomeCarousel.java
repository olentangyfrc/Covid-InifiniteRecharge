package frc.robot.subsystem.balldelivery.commands;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

public class HomeCarousel extends CommandBase {
    private BallDelivery ballDelivery;
    private boolean stop;
    private static Logger logger = Logger.getLogger(HomeCarousel.class.getName());
    
    public HomeCarousel(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
        logger.info("creates HomeCarousel");
    }

    @Override
    public void initialize(){
        logger.info("starts HomeCarousel");
        stop = false;
    }

    @Override
    public void execute(){
        ballDelivery.spinCarousel();
    }

    @Override
    public void end(boolean interrupted){
        stop = true;
    }

    @Override
    public boolean isFinished(){
        return ballDelivery.isCarouselSwitchOn();
    }
}
