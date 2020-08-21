package pl.embedded.reflex.sensors.motion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.embedded.reflex.sensors.callbacks.RotationSpeedEventListener;

public class RotationSpeedDetector implements SensorEventListener
{
    private final static double MAX_ROTATION_SPEED = 10.0;
    private final static double[] speeds = new double[2];
    private final RotationSpeedEventListener listener;

    public RotationSpeedDetector(RotationSpeedEventListener listener)
    {
        this.listener = listener;
    }

    public void register(SensorManager manager)
    {
        Sensor gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        manager.registerListener(this, gyroscope, 10_000);
    }

    public void unregister(SensorManager manager)
    {
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        for (int i = 0; i < 2; i++)
        {
            speeds[i] = 0.05 * Math.min(Math.abs(event.values[i]), MAX_ROTATION_SPEED) + 1;
        }
        listener.onSpeedRotationChanged(speeds.clone());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
