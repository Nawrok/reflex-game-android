package pl.embedded.reflex.sensors.listeners;

import pl.embedded.reflex.enums.Position;

public interface MotionEventListener
{
    void onMotionChanged(Position position, long timestamp);
}
