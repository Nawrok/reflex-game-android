package pl.embedded.reflex.sensors.listeners;

import pl.embedded.reflex.model.Position;

public interface MotionEventListener
{
    void onMotionChanged(Position position, long eventTimestamp);
}
