package frc.robot.subsystem.balldelivery.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystem.balldelivery.BallDelivery;

import java.util.logging.Logger;

/**
 * do we need this
 */

public class RotateCarouselOnce extends CommandBase {
    private BallDelivery ballDelivery;
    private boolean stop;

    public RotateCarouselOnce(BallDelivery bd){
        ballDelivery = bd;
        addRequirements(bd);
    }

    public void initialize()
    {
        stop = false;
    }

    @Override
    public void execute()
    {

    }

    public void end()
    {
        stop = true;
    }

    public boolean isFinished()
    {
        return stop;
    }


}
