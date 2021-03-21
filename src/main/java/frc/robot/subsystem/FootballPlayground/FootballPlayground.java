package frc.robot.subsystem.FootballPlayground;

import java.util.logging.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystem.PortMan;

public class FootballPlayground extends SubsystemBase{
    private static Logger logger = Logger.getLogger(FootballPlayground.class.getName());

    private DigitalInput beamBrakerSender;
    private DigitalInput beamBrakerReceiver;
    private boolean lastReading = true;
    private int count = 1;
    private WPI_TalonSRX motor;

    public void init(final PortMan portMan) throws Exception {
        //beamBrakerSender = new DigitalInput(portMan.acquirePort(PortMan.digital0_label, "Beam Braker Sender"));
        beamBrakerReceiver = new DigitalInput(portMan.acquirePort(PortMan.digital1_label, "Beam Braker Receiver"));
        motor = new WPI_TalonSRX(portMan.acquirePort(PortMan.can_16_label, "Motor"));
        motor.setSelectedSensorPosition(0, 0, 0);
        motor.setSensorPhase(true);
        motor.config_kP(0, 0.2, 0);
        motor.config_kI(0, 0.0, 0);
        motor.config_kD(0, 0, 0);
        motor.config_kF(0, 0, 0);

        //spin one rotation
        //motor.set(ControlMode.Position, 4096);
        //motor.set(ControlMode.Velocity, 500);
        //rotateOnceWithBeamBraker();
    }

    @Override
    public void periodic(){
        //logger.info("" + motor.getSelectedSensorPosition());
        //runUntilBroken();
        //rotateOnceWithBeamBraker();
        //xperiodic();
        logBeamBraker();
        if(count < 4){
            rotateOnceWithBeamBraker();
        }
        else{
            motor.set(ControlMode.PercentOutput, 0);
        }
    }
    public void xperiodic() {

        boolean reading;
        reading = beamBrakerReceiver.get();
        if (lastReading != reading) {
            logger.info(" Receiver [" + reading + "] lastReading [" + lastReading +"]");
            lastReading = reading;
            if(lastReading == true){
                count++;
                logger.info("" + count);
            }
            if(count % 4 == 0){
                logger.info("stop");
            }
        }
    }

    public void rotateOnce()
    {
        motor.set(ControlMode.Position, 4096);
    }

    public void rotateOnceWithBeamBraker()
    {
        boolean reading;
        motor.set(ControlMode.Velocity, 500);
        reading = beamBrakerReceiver.get();
        if(lastReading != reading){
            logger.info(" Receiver [" + reading + "] lastReading [" + lastReading +"]");
            lastReading = reading;
            if(lastReading == true)
            {
                count++;
                logger.info("" + count);
            }
            if(count >= 4){
                logger.info("stop");
                motor.set(ControlMode.PercentOutput, 0);
            }
        }
    }
    
    public void logBeamBraker()
    {
        boolean reading;
        reading = beamBrakerReceiver.get();
        if(lastReading != reading){
            logger.info(" Receiver [" + reading + "] lastReading [" + lastReading +"]");
            lastReading = reading;
            if(lastReading == true)
            {
                count++;
                logger.info("" + count);
            }
            if(count >= 4){
                logger.info("stop");
            }
        }

    }

    public double getCurrentMotorPosition()
    {
        return motor.getSelectedSensorPosition();
    }

    public double getCurrentMotorVelocity()
    {
        return motor.getSelectedSensorVelocity();
    }

    public boolean getIsBeamBroken()
    {
        return !(beamBrakerReceiver.get());
    }

    public void runUntilBroken()
    {
        if(beamBrakerReceiver.get() == true){
            motor.set(ControlMode.Velocity, 500);
        }
        else{
            motor.set(ControlMode.PercentOutput, 0);
        }
    }
}