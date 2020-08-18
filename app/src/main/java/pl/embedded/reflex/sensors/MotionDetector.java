package pl.embedded.reflex.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.callbacks.MotionEventListener;

public class MotionDetector implements SensorEventListener
{
    private final float[] rotationMatrix, orientationMatrix;
    private final MotionEventListener listener;

    public MotionDetector(MotionEventListener listener)
    {
        this.rotationMatrix = new float[9];
        this.orientationMatrix = new float[3];
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
        listener.onMotionChanged(getDevicePosition(orientationMatrix), event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private Position getDevicePosition(float[] orientationMatrix)
    {
        double angle_x = orientationMatrix[2];
        double angle_y = orientationMatrix[1];

        Position position = Position.IDLE;
        if (angle_x < -35.0)
        {
            position = Position.LEFT;
        }
        if (angle_x > 35.0)
        {
            position = Position.RIGHT;
        }
        if (angle_y < -30.0)
        {
            position = Position.DOWN;
        }
        if (angle_y > 30.0)
        {
            position = Position.UP;
        }
        return position;
    }
}
