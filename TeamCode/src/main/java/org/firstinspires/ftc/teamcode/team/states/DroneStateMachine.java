package org.firstinspires.ftc.teamcode.team.states;

import org.firstinspires.ftc.teamcode.lib.util.Namable;
import org.firstinspires.ftc.teamcode.lib.util.Time;
import org.firstinspires.ftc.teamcode.lib.util.TimeUnits;

public class DroneStateMachine extends TimedState<DroneStateMachine.State> {
    public DroneStateMachine() {
        super(State.CLOSE);
    }

    @Override
    public String getName() {
        return "Claw State Machine";
    }

    @Override
    protected Time getStateTransitionDuration() {
        return new Time(10d, TimeUnits.MILLISECONDS);
    }

    public enum State implements Namable {
        OPEN("Open",0.4d), CLOSE("Close", 0d);

        private final String name;
        private final double Position;

        State(final String name, final double pPosition) {
            this.name          = name;
            this.Position  = pPosition;
        }

        public double getPosition() {
            return Position;
        }

        @Override

        public String getName() {
            return name;
        }
    }
}