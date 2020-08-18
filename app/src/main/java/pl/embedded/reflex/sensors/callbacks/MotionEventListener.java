package pl.embedded.reflex.sensors.callbacks;

import pl.embedded.reflex.model.Position;

public interface MotionEventListener
{
    void onMotionChanged(Position position, long eventTimestamp);
}
