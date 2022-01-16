/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem;

import frc.robot.subsystem.FootballPlayground.FootballPlayground;
import frc.robot.subsystem.FootballPlayground.FootballPlaygroundSBTab;
import frc.robot.subsystem.balldelivery.BallDelivery;
import frc.robot.subsystem.balldelivery.BallDeliverySBTab;
import frc.robot.subsystem.climber.Climber;
import frc.robot.subsystem.climber.ClimberSBTab;
import frc.robot.subsystem.onewheelshooter.OneWheelShooter;
import frc.robot.subsystem.onewheelshooter.OneWheelShooterSBTab;
import frc.robot.subsystem.pixylinecam.PixyLineCam;
import frc.robot.subsystem.pixylinecam.PixyLineCamSBTab;
import frc.robot.subsystem.telemetry.Telemetry;
import frc.robot.subsystem.telemetry.TelemetrySBTab;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Add your docs here.
 */
public class DisplayManager {
    private ClimberSBTab climberDisplay;
    private TelemetrySBTab telemetryDisplay;
    private BallDeliverySBTab ballDeliveryDisplay;
    private PixyLineCamSBTab pixyDisplay;
    private OneWheelShooterSBTab oneWheelShooter;
    private FootballPlaygroundSBTab footballPlayground;

    private static Logger logger = Logger.getLogger(DisplayManager.class.getName());

    private ArrayList<SBInterface> subsystemUpdateList;

    public DisplayManager(){
        subsystemUpdateList = new ArrayList<SBInterface>();
    }

    public void addClimber(Climber c){
        climberDisplay = new ClimberSBTab(c);
        subsystemUpdateList.add(climberDisplay);

    }
    public void addTelemetry(Telemetry te){
        telemetryDisplay = new TelemetrySBTab(te);
        subsystemUpdateList.add(telemetryDisplay);

    }

    public void addBallDelivery(BallDelivery bd) {
        ballDeliveryDisplay = new BallDeliverySBTab(bd);
        subsystemUpdateList.add(ballDeliveryDisplay);
    }

    public void addPixyLineCam(PixyLineCam p) {
        pixyDisplay = new PixyLineCamSBTab(p);
        subsystemUpdateList.add(pixyDisplay);
    }

    public void addShooter(OneWheelShooter s){
        oneWheelShooter = new OneWheelShooterSBTab(s);
        subsystemUpdateList.add(oneWheelShooter);
    }

    public void addFootballPlayground(FootballPlayground fp){
        footballPlayground = new FootballPlaygroundSBTab(fp);
        subsystemUpdateList.add(footballPlayground);
    }


    public void update() {
        for (int j = 0; j < subsystemUpdateList.size(); j ++) {
            subsystemUpdateList.get(j).update();
          }
    }
}
