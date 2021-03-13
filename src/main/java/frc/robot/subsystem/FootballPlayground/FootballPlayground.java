package frc.robot.subsystem.FootballPlayground;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystem.PortMan;

public class FootballPlayground extends SubsystemBase{
    private static Logger logger = Logger.getLogger(FootballPlayground.class.getName());

    private DigitalInput beamBrakerSender;
    private DigitalInput beamBrakerReceiver;

    public void init(final PortMan portMan) throws Exception {
        beamBrakerSender = new DigitalInput(portMan.acquirePort(PortMan.digital0_label, "Beam Braker Sender"));
        beamBrakerReceiver = new DigitalInput(portMan.acquirePort(PortMan.digital1_label, "Beam Braker Receiver"));
    }
}
