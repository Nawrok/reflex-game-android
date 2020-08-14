package pl.embedded.reflex.callbacks;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import androidx.appcompat.app.AppCompatDelegate;

public class LightSensorEventListener implements SensorEventListener
{
    private boolean dark;

    public LightSensorEventListener(boolean dark)
    {
        this.dark = dark;
    }

    public boolean isDark()
    {
        return dark;
    }

    public void setDark(boolean dark)
    {
        this.dark = dark;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            double lux = event.values[0];
            boolean prevDark = dark;
            if (lux < 20.0)
            {
                dark = true;
            }
            if (lux > 30.0)
            {
                dark = false;
            }
            if (prevDark != dark)
            {
                AppCompatDelegate.setDefaultNightMode((dark) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
