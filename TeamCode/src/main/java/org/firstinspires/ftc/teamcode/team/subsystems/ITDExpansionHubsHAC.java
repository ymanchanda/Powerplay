package org.firstinspires.ftc.teamcode.team.subsystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.lib.drivers.RevMotor;
import org.firstinspires.ftc.teamcode.revextension2.ExpansionHubEx;
import org.firstinspires.ftc.teamcode.revextension2.RevBulkData;
import org.firstinspires.ftc.teamcode.team.ITDAutoRobotHAC;
import org.firstinspires.ftc.teamcode.team.states.IState;

public class ITDExpansionHubsHAC implements ISubsystem {

    private ITDAutoRobotHAC robot;
    private ExpansionHubEx masterHub;
    private ExpansionHubEx slaveHub;
    private RevBulkData masterData;
    private RevBulkData slaveData;

    public ITDExpansionHubsHAC(ITDAutoRobotHAC robot, ExpansionHubEx masterHub, ExpansionHubEx slaveHub) {
        setRobot(robot);
        setMasterHub(masterHub);
        setSlaveHub(slaveHub);
    }

    @Override
    public IState getStateMachine() {
        return null;
    }

    @Override
    public Enum getState() {
        return null;
    }

    @Override
    public void start() {
        getRevBultData();
    }

    @Override
    public void update(double dt) {
        getRevBultData(dt);
    }

    @Override
    public void stop() {

    }

    public void getRevBultData() {
        final RevBulkData masterData;
        try {
            masterData = getMasterHub().getBulkInputData();
            if(masterData != null) {
                setMasterData(masterData);
            }
        } catch(Exception e) {
            //
        }

        final RevBulkData slaveData;
        try {
            slaveData = getSlaveHub().getBulkInputData();
            if(slaveData != null) {
                setSlaveData(slaveData);
            }
        } catch(Exception e) {
            //
        }

        for(final RevMotor revMotor : getRobot().getMotors()) {
            if(revMotor != null) {
                if(revMotor.isOnMasterHub()) {
                    revMotor.setEncoderReading(getMasterData().getMotorCurrentPosition(revMotor.getMotor()));
                } else {
                    revMotor.setEncoderReading(getSlaveData().getMotorCurrentPosition(revMotor.getMotor()));
                }
            }
        }
    }

    public void getRevBultData(double dt) {
        final RevBulkData masterData;
        try {
            masterData = getMasterHub().getBulkInputData();
            if(masterData != null) {
                setMasterData(masterData);
            }
        } catch(Exception e) {
            //
        }

        final RevBulkData slaveData;
        try {
            slaveData = getSlaveHub().getBulkInputData();
            if(slaveData != null) {
                setSlaveData(slaveData);
            }
        } catch(Exception e) {
            //
        }

        try {
            for(final RevMotor revMotor : getRobot().getMotors()) {
                if(revMotor != null) {
                    if(revMotor.isOnMasterHub()) {
                        revMotor.setEncoderReading(getMasterData().getMotorCurrentPosition(revMotor.getMotor()), dt);
                    } else {
                        revMotor.setEncoderReading(getSlaveData().getMotorCurrentPosition(revMotor.getMotor()), dt);
                    }
                }
            }

        } catch(Exception e){
            //
        }

    }

    public ExpansionHubEx getMasterHub() {
        return masterHub;
    }

    public void setMasterHub(ExpansionHubEx masterHub) {
        this.masterHub = masterHub;
    }

    public ExpansionHubEx getSlaveHub() {
        return slaveHub;
    }

    public void setSlaveHub(ExpansionHubEx slaveHub) {
        this.slaveHub = slaveHub;
    }

    public RevBulkData getMasterData() {
        return masterData;
    }

    public void setMasterData(RevBulkData masterData) {
        this.masterData = masterData;
    }

    public RevBulkData getSlaveData() {
        return slaveData;
    }

    public void setSlaveData(RevBulkData slaveData) {
        this.slaveData = slaveData;
    }

    public ITDAutoRobotHAC getRobot() {
        return robot;
    }

    public void setRobot(ITDAutoRobotHAC robot) {
        this.robot = robot;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void writeToTelemetry(Telemetry telemetry) {

    }

}
