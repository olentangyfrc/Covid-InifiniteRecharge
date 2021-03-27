package frc.common.auton;

import frc.common.control.ITrajectoryConstraint;
import frc.common.control.Path;
import frc.common.control.PathLineSegment;
import frc.common.control.Trajectory;
import frc.common.math.Rotation2;
import frc.common.math.Vector2;

public class AutonomousTrajectories {

    private static final int SUBDIVIDE_ITERATIONS = 8;


    //AutonNav Trajectories
    private final Trajectory boxyBounceTrajectory;
    private final Trajectory slalomTrajectory;
    private final Trajectory barrelRacingTrajectory;

    //Insterstellar accuracy Trajectories
    private final Trajectory greenZonetoReIntroductionZone;
    private final Trajectory reIntroductionZoneToYellowZone;
    private final Trajectory yellowZonetoReIntroductionZone;
    private final Trajectory reIntroductionZoneToBlueZone;
    private final Trajectory blueZonetoReIntroductionZone;
    private final Trajectory reIntroductionZoneToRedZone;/*
    private final Trajectory redZonetoReIntroductionZone;
    */
    
/*
        y+: Left
        y-: Right
        x+: Forwards
        x-: Backwards
        Unit: inches
*/
    public AutonomousTrajectories(ITrajectoryConstraint... constraints) {
        // <editor-fold desc="Hab to Cargo Ship Side Near">
        Path reIntroductionZoneToRedZonePath = new Path(Rotation2.ZERO);
        reIntroductionZoneToRedZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(55, 0)
                ),
                Rotation2.fromDegrees(11)
        );
        reIntroductionZoneToRedZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        reIntroductionZoneToRedZone = new Trajectory(reIntroductionZoneToRedZonePath, constraints);

        Path blueZonetoReIntroductionZonePath = new Path(Rotation2.fromDegrees(11));
        blueZonetoReIntroductionZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(-98, 0)
                ),
                Rotation2.ZERO
        );
        blueZonetoReIntroductionZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        blueZonetoReIntroductionZone = new Trajectory(blueZonetoReIntroductionZonePath, constraints);
        Path reIntroductionZoneToBlueZonePath = new Path(Rotation2.ZERO);
        reIntroductionZoneToBlueZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(98, 0)
                ),
                Rotation2.fromDegrees(11)
        );
        reIntroductionZoneToBlueZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        reIntroductionZoneToBlueZone = new Trajectory(reIntroductionZoneToBlueZonePath, constraints);
        Path yellowZonetoReIntroductionZonePath = new Path(Rotation2.fromDegrees(15));
        yellowZonetoReIntroductionZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(-156, 0)
                ),
                Rotation2.ZERO
        );
        yellowZonetoReIntroductionZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        yellowZonetoReIntroductionZone = new Trajectory(yellowZonetoReIntroductionZonePath, constraints);
        Path reIntroductionZoneToYellowZonePath = new Path(Rotation2.ZERO);
        reIntroductionZoneToYellowZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(164, -4)
                ),
                Rotation2.fromDegrees(15)
        );
        reIntroductionZoneToYellowZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        reIntroductionZoneToYellowZone = new Trajectory(reIntroductionZoneToYellowZonePath, constraints);


        Path greenZoneToReIntroductionZonePath = new Path(Rotation2.fromDegrees(25.7));
        greenZoneToReIntroductionZonePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(-215, 0)
                ),
                Rotation2.ZERO
        );
        greenZoneToReIntroductionZonePath.subdivide(SUBDIVIDE_ITERATIONS);
        greenZonetoReIntroductionZone = new Trajectory(greenZoneToReIntroductionZonePath, constraints);

        Path barrelRacingPath = new Path(Rotation2.ZERO);
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(110, 0)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(110, 0),
                        new Vector2(110, -70)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(110, -70),
                        new Vector2(50, -70)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(50, -70),
                        new Vector2(50, 0)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(50, 0),
                        new Vector2(210, 0)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(210, 0),
                        new Vector2(210, 50)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(210, 50),
                        new Vector2(115, 50)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(125, 50),
                        new Vector2(125, 0)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(125, 0),
                        new Vector2(205, -75)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(205, -75),
                        new Vector2(265, -75)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(265, -75),
                        new Vector2(265, -10)
                ),
                Rotation2.ZERO
        );
        barrelRacingPath.addSegment(
                new PathLineSegment(
                        new Vector2(265, -10),
                        new Vector2(0, -10)
                ),
                Rotation2.ZERO
        );

        barrelRacingPath.subdivide(SUBDIVIDE_ITERATIONS);
        barrelRacingTrajectory = new Trajectory(barrelRacingPath, constraints);

        Path slalomPath = new Path(Rotation2.ZERO);

        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(30, 0)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(30, 0),
                        new Vector2(30, 46)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(30, 46),
                        new Vector2(225, 46)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(225, 46),
                        new Vector2(225, -24)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(225, -24),
                        new Vector2(265, -24)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(265, -24),
                        new Vector2(265, 66)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(265, 66),
                        new Vector2(200, 66)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(200, 66),
                        new Vector2(200, -24)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(200, -24),
                        new Vector2(40, -24)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(40, -24),
                        new Vector2(40, 56)
                ),
                Rotation2.ZERO
        );
        slalomPath.addSegment(
                new PathLineSegment(
                        new Vector2(40, 56),
                        new Vector2(-10, 56)
                ),
                Rotation2.ZERO
        );

        slalomPath.subdivide(SUBDIVIDE_ITERATIONS);
        slalomTrajectory = new Trajectory(slalomPath, constraints);


        Path boxyBouncePath = new Path(Rotation2.fromDegrees(0));
        
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(0, 0),
                        new Vector2(33, 0)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(33, 0),
                        new Vector2(33, 50)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(33, 50),
                        new Vector2(25, 10)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(25, 10),
                        new Vector2(80, -100)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(80, -100),
                        new Vector2(114, -100)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(114, -100),
                        new Vector2(114, 55)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(114, 55),
                        new Vector2(114, -100)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(114, -100),
                        new Vector2(204, -100)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(204, -100),
                        new Vector2(204, 45)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(204, 45),
                        new Vector2(204, -50)
                ),
                Rotation2.fromDegrees(0)
        );
        boxyBouncePath.addSegment(
                new PathLineSegment(
                        new Vector2(204, -50),
                        new Vector2(254, -50)
                ),
                Rotation2.fromDegrees(0)
        );

        boxyBouncePath.subdivide(SUBDIVIDE_ITERATIONS);
        boxyBounceTrajectory = new Trajectory(boxyBouncePath, constraints);
    }
    public Trajectory getBoxyBounceTrajectory() {
        return boxyBounceTrajectory;
    }
    public Trajectory getSlalomTrajectory() {
        return slalomTrajectory;
    }
    public Trajectory getBarrelRacingTrajectory() {
        return barrelRacingTrajectory;
    }
    public Trajectory getGreenZoneToReIntroductionZone() {
        return greenZonetoReIntroductionZone;
    }
    public Trajectory getReIntroductionZoneToYellowZone() {
        return reIntroductionZoneToYellowZone;
    }
    public Trajectory getYellowZoneToReIntroductionZone() {
        return yellowZonetoReIntroductionZone;
    }
    public Trajectory getReIntroductionZoneToBlueZone() {
        return reIntroductionZoneToBlueZone;
    }
    public Trajectory getBlueZonetoReIntroductionZone() {
        return blueZonetoReIntroductionZone;
    }
    public Trajectory getReIntroductionZoneToRedZone() {
        return reIntroductionZoneToRedZone;
    }
}