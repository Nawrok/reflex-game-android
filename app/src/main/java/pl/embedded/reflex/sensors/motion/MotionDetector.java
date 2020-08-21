package pl.embedded.reflex.sensors.motion;

import android.hardware.SensorManager;

import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.callbacks.MotionEventListener;
import pl.embedded.reflex.sensors.callbacks.RotationEventListener;
import pl.embedded.reflex.sensors.callbacks.RotationSpeedEventListener;

public class MotionDetector implements RotationEventListener, RotationSpeedEventListener
{
    private final RotationDetector rotationDetector;
    private final RotationSpeedDetector rotationSpeedDetector;
    private final MotionEventListener listener;
    private Position position;
    private long timestamp;

    public MotionDetector(MotionEventListener listener)
    {
        this.rotationDetector = new RotationDetector(this);
        this.rotationSpeedDetector = new RotationSpeedDetector(this);
        this.listener = listener;
    }

    public void register(SensorManager manager)
    {
        rotationDetector.register(manager);
        rotationSpeedDetector.register(manager);
    }

    public void unregister(SensorManager manager)
    {
        rotationDetector.unregister(manager);
        rotationSpeedDetector.unregister(manager);
    }

    @Override
    public void onRotationChanged(Position position, long timestamp)
    {
        this.position = position;
        this.timestamp = timestamp;
    }

    @Override
    public void onSpeedRotationChanged(double[] speeds)
    {
        if (position == Position.UP || position == Position.DOWN)
        {
            listener.onMotionChanged(position, speeds[0], timestamp);
        }
        if (position == Position.LEFT || position == Position.RIGHT)
        {
            listener.onMotionChanged(position, speeds[1], timestamp);
        }
    }
}
