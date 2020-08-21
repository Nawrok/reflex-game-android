package pl.embedded.reflex.sensors.callbacks;

import pl.embedded.reflex.model.Position;

public interface RotationEventListener
{
    void onRotationChanged(Position position, long timestamp);
}
