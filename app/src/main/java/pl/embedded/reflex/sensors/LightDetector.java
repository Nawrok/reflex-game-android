package pl.embedded.reflex.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.embedded.reflex.sensors.listeners.LightEventListener;

public class LightDetector implements SensorEventListener
{
    private final LightEventListener listener;
    private boolean dark;

    public LightDetector(LightEventListener listener, boolean dark)
    {
        this.listener = listener;
        this.dark = dark;
    }

    public void register(SensorManager manager)
    {
        Sensor light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(SensorManager manager)
    {
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double illuminance = event.values[0];
        boolean prevDark = dark;
        if (illuminance < 20.0)
        {
            dark = true;
        }
        if (illuminance > 35.0)
        {
            dark = false;
        }
        if (prevDark != dark)
        {
            listener.onLightChanged(dark);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
