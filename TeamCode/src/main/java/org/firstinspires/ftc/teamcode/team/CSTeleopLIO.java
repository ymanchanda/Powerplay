package org.firstinspires.ftc.teamcode.team;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.team.auto.CSBaseLIO;
import org.firstinspires.ftc.teamcode.team.states.OuttakeStateMachine;
import org.firstinspires.ftc.teamcode.team.states.DroneStateMachine;
import org.firstinspires.ftc.teamcode.team.states.IntakeStateMachine;


/*
 * This {@code class} acts as the driver-controlled program for FTC team 16598 for the CenterStage
 * challenge. By extending {@code CSRobot}, we already have access to all the robot subsystems,
 * so only tele-operated controls need to be defined here.
 *
 * The controls for this robot are:
 *  User 1:
 *      Drive:
 *          Left & Right joysticks     -> Mecanum drive
 *          Left-Bumper               -> Decrease robot speed .7x
 *          Right-Bumper              -> Normal robot speed 1x
 *      Lift:
 *          Dpad-up                    -> High
 *          Dpad-down                  -> Ground / Intake
 *          Dpad-right                 -> Medium
 *          Dpad-left                  -> Low
 *      Drone:
 *          Dpad-Up                    -> Open
 *      Arm:
 *          B-Button (pressed)         -> Left
 *          X-Button (pressed)         -> Right
 *          A-Button (pressed)         -> Middle
 *          Y-Button (pressed)         -> Middle
 *  User 2:
 *      Drive:
 *          Left bumper (pressed)      ->
 *          Right bumper (pressed)     ->
 *      Lift:
 *          Left-trigger               ->
 *          Right-trigger              ->
 *          A-button (pressed)         ->
 *          Y-button (pressed)         ->
 *      Arm:
 *          Dpad-right                 ->
 *          Dpad-down                  ->
 *          Dpad-left                  ->
 *          Dpad-up                    ->
 *
 * @see UltimateGoalRobot
 */

@TeleOp(name = "CS TeleOp LIO", group = "Main")
public class CSTeleopLIO extends CSTeleopRobotLIO {

    private double currentTime = 0; // keep track of current time
    private double speedMultiplier = 0.7;
    //these are based on LiftTest
    private static final double HIGH = 20d;
    private static final double MID = 15d;
    private static final double LOW = 10d;
    private boolean liftdown = true;
    private boolean intakeOn = false;

//    private boolean armMid = true;

    //private boolean coneloaded = false;
    private Pose2d poseEstimate;

    //RevBlinkinLedDriver blinkinLedDriver;
    //RevBlinkinLedDriver.BlinkinPattern pattern;
    //private ElapsedTime timeSinceIntakeButton = new ElapsedTime();

    @Override
    public void init() {
        drive = new CSBaseLIO(hardwareMap, true);
        //blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        super.init();
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        super.loop();
        drive.update();
        poseEstimate = drive.getPoseEstimate();

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //Gamepad 1

        drive.setWeightedDrivePower(
                new Pose2d(
                        -gamepad1.left_stick_y * speedMultiplier,
                        -gamepad1.left_stick_x * speedMultiplier,
                        -gamepad1.right_stick_x * speedMultiplier
                )
        );

        //this changes the speed the robot moves at
        if (getEnhancedGamepad1().isLeftBumperJustPressed()) {
            speedMultiplier = 0.7;
        }
        if (getEnhancedGamepad1().isRightBumperJustPressed()) {
            speedMultiplier = 1.0;
        }

        //Intake
        //spins the intake to intake a pixel
        if (getEnhancedGamepad1().getLeft_trigger() > 0) {
            drive.robot.getIntakeSubsystem().getStateMachine().updateState(IntakeStateMachine.State.INTAKE);
            intakeOn = true;
        }
        //spins the intake to outtake a pixel
        if (getEnhancedGamepad1().getRight_trigger() > 0) {
            drive.robot.getIntakeSubsystem().getStateMachine().updateState(IntakeStateMachine.State.OUTTAKE);
            intakeOn = true;
        }
        //stops spining the intake
        if (getEnhancedGamepad1().isStart()) {
            drive.robot.getIntakeSubsystem().getStateMachine().updateState(IntakeStateMachine.State.IDLE);
            intakeOn = false;
        }

        //This stops running the intake when the lift is up
        if (intakeOn && !liftdown) {
            drive.robot.getIntakeSubsystem().getStateMachine().updateState(IntakeStateMachine.State.IDLE);
        }

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //Gamepad 2

        //Drone
        telemetry.addData("Drone State: ", drive.robot.getDroneSubsystem().getStateMachine().getState());
        if (getEnhancedGamepad2().isLeftBumperJustPressed()) {
            drive.robot.getDroneSubsystem().getStateMachine().updateState(DroneStateMachine.State.OPEN);
        }

        //Lift
        //brings the lift down to the starting pose
        if (getEnhancedGamepad2().isDpadDownJustPressed()) {
            double lastSetPoint = drive.robot.getLiftSubsystem().getDesiredSetpoint();
            telemetry.addData("Lift State: ", lastSetPoint);
            if (lastSetPoint == LOW) {
                drive.robot.getLiftSubsystem().retract();
                liftdown = true;
            } else if ((lastSetPoint == HIGH || lastSetPoint == MID)) {
                drive.robot.getLiftSubsystem().retract();
                liftdown = true;
            }
        }
        //Brins the lift down to the LOW pose when b is pessed
        if (getEnhancedGamepad2().isDpadRightJustPressed()) {  //&&arm mid
            drive.robot.getLiftSubsystem().extend(LOW);
            liftdown = false;
        }
        //Brins the lift down to the MID pose when b is pessed
        if (getEnhancedGamepad2().isDpadLeftJustPressed()) {  //&&arm mid
            drive.robot.getLiftSubsystem().extend(MID);
            liftdown = false;
        }
        //Brins the lift down to the LOW pose when b is pessed
        if (getEnhancedGamepad2().isDpadUpJustPressed()) {  //&&arm mid
            drive.robot.getLiftSubsystem().extend(HIGH);
            liftdown = false;
        }

        telemetry.addData("Lift State: ", drive.robot.getLiftSubsystem().getStateMachine().getState());
        telemetry.addData("Lift SetPoint: ", drive.robot.getLiftSubsystem().getDesiredSetpoint());
        telemetry.addData("Outtake State: ", drive.robot.getOuttakeSubsystem().getStateMachine().getState());

        //Outtake
        //This only allows moving the Outtake only when the Lift has moved up and is no longer in Ground or Intake Position
        //This also brings the lift down to the ground state when Outtake is Released
        if (!liftdown) {
            if (getEnhancedGamepad2().getLeft_trigger() > 0) {
                drive.robot.getOuttakeSubsystem().getStateMachine().updateState(OuttakeStateMachine.State.RELEASE);
            }
            if (getEnhancedGamepad2().getRight_trigger() > 0) {
                drive.robot.getOuttakeSubsystem().getStateMachine().updateState(OuttakeStateMachine.State.PICKUP);
            }
            if (getEnhancedGamepad2().isRightBumperJustPressed()) {
                drive.robot.getOuttakeSubsystem().getStateMachine().updateState(OuttakeStateMachine.State.FORWARD);
            }
        }


//            if (getEnhancedGamepad2().getRight_trigger() > 0) {
//                drive.robot.getOuttakeSubsystem().getStateMachine().updateState(OuttakeStateMachine.State.RELEASE);
//            }
//        }

//        if(getEnhancedGamepad2().isaJustPressed()){
//            drive.robot.getElevSubsystem().getStateMachine().updateState(ElevStateMachine.State.RETRACT);
//            liftdown = true;
//            stopintake = false;
//            getIntakeMotorSubsystem().getStateMachine().updateState(IntakeStateMachine.State.INTAKE);
//            telemetry.addLine("a pressed lift up: " + drive.robot.getElevSubsystem().getStateMachine().getState());
//        }
//
//        if(getEnhancedGamepad2().isyJustPressed()){
//            if (drive.robot.getCappingArmSubsystem().getStateMachine().getState() == CappingArmStateMachine.State.TOP) {
//                drive.robot.getElevSubsystem().getStateMachine().updateState(ElevStateMachine.State.EXTENDTOP);
//                stopintake = true;
//                liftdown = false;
//            }
//
//            telemetry.addLine("y pressed lift down: " + drive.robot.getElevSubsystem().getStateMachine().getState());
//        }
//        if((atGroundJunction()||!liftdown) && !armMid && drive.robot.getClawSubsystem().getState() == ClawStateMachine.State.OPEN){
//            if(atGroundJunction()){
//                drive.robot.getLiftSubsystem().extend(LOW);
//                liftdown = false;
//            }
//            drive.robot.getArmSubsystem().getStateMachine().updateState(ArmStateMachine.State.INIT);
//            armMid = true;
//            drive.robot.getLiftSubsystem().retract();
//        }
            updateTelemetry(telemetry);
            currentTime = getRuntime();
        }
}

