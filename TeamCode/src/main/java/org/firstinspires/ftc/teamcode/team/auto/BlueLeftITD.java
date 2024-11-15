package org.firstinspires.ftc.teamcode.team.auto;




import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;




import org.firstinspires.ftc.teamcode.lib.util.TimeProfiler;
import org.firstinspires.ftc.teamcode.lib.util.TimeUnits;
import org.firstinspires.ftc.teamcode.team.CSVP;
import org.firstinspires.ftc.teamcode.team.PoseStorage;
import org.firstinspires.ftc.teamcode.team.odometry.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.team.states.DroneStateMachine;
import org.firstinspires.ftc.teamcode.team.states.LiftStateMachine;
import org.firstinspires.ftc.teamcode.team.states.OuttakeStateMachine;





import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;




import org.firstinspires.ftc.teamcode.lib.util.TimeProfiler;
import org.firstinspires.ftc.teamcode.lib.util.TimeUnits;
import org.firstinspires.ftc.teamcode.team.CSVP;
import org.firstinspires.ftc.teamcode.team.PoseStorage;
import org.firstinspires.ftc.teamcode.team.odometry.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.team.states.OuttakeStateMachine;
import org.firstinspires.ftc.teamcode.team.states.DroneStateMachine;
import org.firstinspires.ftc.teamcode.team.states.LiftStateMachine;




@Autonomous(name = "Blue Left", group = "Pixel")
public class BlueLeftITD extends LinearOpMode {




    CSBaseLIO drive;
    private static double dt;
    private static TimeProfiler updateRuntime;




    static final Vector2d path1 = new Vector2d(-24 - (15.125/2),0); // blue left, not confirmed, maybe change y to a different location for space
    static final Vector2d path2 = new Vector2d(-24 - (15.125/2), 0); // blue right, not confirmed, maybe change y to a different location for space
    static final Vector2d path3 = new Vector2d(24 + (15.125/2),0); // red right, not confirmed, maybe change y to a different location for space
    static final Vector2d path4 = new Vector2d(24 + (15.125/2), 0); // red left, not confirmed, maybe change y to a different location for space
    static final Vector2d Location1 = new Vector2d(50, 12);
    static final Vector2d Location2 = new Vector2d(34, 12);
    static final Vector2d Location3 = new Vector2d(12,12);




    //ElapsedTime carouselTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    ElapsedTime waitTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);




    enum State {
        WAIT0,
        CLAWCLOSE,
        INITSTRAFE,
        LIFTUP,
        FORWARD,
        PRELOAD,
        MOVEARM,
        CLAWOPEN,
        MOVEARMBACK,
        LIFTDOWN,
        IDLE,
        PARK,
        GRAB
    }


    org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.IDLE;
    State currentState = State.IDLE;




    private static final double width = 16.375;
    private static final double length = 15.125;



    Pose2d startPoseRL = new Pose2d( 72 - (15.125/2), - 24 + (16.375/2)); // 72, -24 not confirmed
    Pose2d startPoseRR = new Pose2d(72 - (15.125/2), 24 - (16.375/2)); //72, 24 not confirmed
    Pose2d startPoseBR = new Pose2d(- 72 + (15.125/2), - 24 + (16.375/2)); //-72, -24 not confirmed
    Pose2d startPoseBL = new Pose2d(- 72 + (15.125/2), 24 - (16.375/2)); //-72, 24 not confirmed
    //lift test needs to be done (values are estimated/inaccurate)
    private static final double HIGHBAR = 24d; //36 inches, 91.4 cm
    private static final double LOWBAR = 23.5d; //20 inches, 50.8 cm
    private static final double LOWBASKET = 0d; //25.75 inches, 65.4 cm
    private static final double HIGHBASKET = 0d; //43 inches, 109.2 cm




    boolean tf = false;




    int counter = 0;




    public void runOpMode() throws InterruptedException {
        setUpdateRuntime(new TimeProfiler(false));




        drive = new CSBaseLIO(hardwareMap);
        drive.setPoseEstimate(startPose);
        drive.robot.getLiftSubsystem().getStateMachine().updateState(LiftStateMachine.State.IDLE);
        drive.robot.getOuttakeSubsystem().getStateMachine().updateState(OuttakeStateMachine.State.FORWARD);
        drive.robot.getCappingArmSubsystem().getStateMachine().updateState(ArmStateMachine.State.REST);




        TrajectorySequence P0 = drive.trajectorySequenceBuilder(startPose)
                .lineTo(Traj0)
                .build();




        TrajectorySequence P1 = drive.trajectorySequenceBuilder(traj0.end())
                .lineTo(Traj1)
                .turn(Math.toRadians(93.5))
                .build();




        TrajectorySequence P2 = drive.trajectorySequenceBuilder(traj1.end())
                .lineTo(Traj2)
                .build();




        TrajectorySequence P3 = drive.trajectorySequenceBuilder(traj2.end())
                .lineTo(Traj3)
                .build();




        TrajectorySequence P4 = drive.trajectorySequenceBuilder(traj3.end())
                .lineTo(Traj2)
                .build();




        TrajectorySequence location1 = drive.trajectorySequenceBuilder(traj4.end())
                .lineTo(Location1)
                .build();




        TrajectorySequence location2 = drive.trajectorySequenceBuilder(traj4.end())
                .lineTo(Location2)
                .build();




        TrajectorySequence location3 = drive.trajectorySequenceBuilder(traj4.end())
                .lineTo(Location3)
                .build();




        drive.getExpansionHubs().update(getDt());




        drive.robot.getLiftSubsystem().update(getDt());
        drive.robot.getOuttakeSubsystem().update(getDt());
        drive.robot.getDroneSubsystem().update(getDt());




        double t1 = waitTimer.milliseconds();




        CSVP = new CSVP();
        CSVP.initTfod(hardwareMap);




        double t2 = waitTimer.milliseconds();




        telemetry.addData("Initialize Time Seconds", (t2 - t1));
        telemetry.update();




        int detectCounter = 0;
        //double confidence = 0;
        //String label = "NONE";
        int oldRecog = 0;
        int recog;




        telemetry.update();
        waitForStart();




        if (isStopRequested()) return;












        currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.WAIT0;




        while (opModeIsActive() && !isStopRequested()) {




            setDt(getUpdateRuntime().getDeltaTime(TimeUnits.SECONDS, true));




            switch (currentState) {




                case WAIT0:
                    telemetry.addLine("in the wait0 state");
                    break;




                case CLAWCLOSE:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.INITSTRAFE;
                    break;




                case INITSTRAFE:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.LIFTUP;
                    break;




                case LIFTUP:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.FORWARD;
                    break;




                case FORWARD:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.PRELOAD;
                    break;




                case PRELOAD:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.MOVEARM;
                    break;




                case MOVEARM:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.CLAWOPEN;
                    break;




                case CLAWOPEN:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.MOVEARMBACK;
                    break;




                case MOVEARMBACK:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.LIFTDOWN;
                    break;




                case LIFTDOWN:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.TOSTACK;
                    break;




                case TOSTACK:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.GRAB;
                    break;




                case GRAB:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.TODROP;
                    break;




                case TODROP:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.PARK;
                    break;




                case PARK:
                    currentState = org.firstinspires.ftc.teamcode.team.auto.BlueLeftFTC.State.IDLE;
                    break;




                case IDLE:
                    PoseStorage.currentPose = drive.getPoseEstimate();
                    break;
            }








            drive.update();




            //The following code ensure state machine updates i.e. parallel execution with drivetrain
            drive.getExpansionHubs().update(getDt());
            drive.robot.getLiftSubsystem().update(getDt());
            drive.robot.getOuttakeSubsystem().update(getDt());
            drive.robot.getDroneSubsystem().update(getDt());
            telemetry.update();
        }




        drive.setMotorPowers(0.0,0.0,0.0,0.0);
    }
    public static TimeProfiler getUpdateRuntime() {
        return updateRuntime;
    }




    public static void setUpdateRuntime(TimeProfiler updaRuntime) {
        updateRuntime = updaRuntime;
    }




    public static double getDt() {
        return dt;
    }




    public static void setDt(double pdt) {
        dt = pdt;
    }
}
}







