package pl.embedded.reflex.sensors.motion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.callbacks.RotationEventListener;

public class RotationDetector implements SensorEventListener
{
    private final static float[] rotationMatrix = new float[9];
    private final static float[] orientationMatrix = new float[3];
    private final RotationEventListener listener;

    public RotationDetector(RotationEventListener listener)
    {
        this.listener = listener;
    }

    public void register(SensorManager manager)
    {
        Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        manager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregister(SensorManager manager)
    {
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientationMatrix);
        for (int i = 0; i < orientationMatrix.length; i++)
        {
            orientationMatrix[i] = (float) Math.toDegrees(orientationMatrix[i]);
        }
        listener.onRotationChanged(getDevicePosition(), event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private Position getDevicePosition()
    {
        double yAxis = orientationMatrix[1];
        double xAxis = orientationMatrix[2];
        Position position = Position.IDLE;
        if (xAxis < -35.0)
        {
            position = Position.LEFT;
        }
        if (xAxis > 35.0)
        {
            position = Position.RIGHT;
        }
        if (yAxis < -30.0)
        {
            position = Position.DOWN;
        }
        if (yAxis > 30.0)
        {
            position = Position.UP;
        }
        return position;
    }
}
