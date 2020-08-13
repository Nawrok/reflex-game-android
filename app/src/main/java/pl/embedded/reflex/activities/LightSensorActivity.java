package pl.embedded.reflex.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;

public class LightSensorActivity extends LocaleAwareCompatActivity
{
    protected SensorManager sensorManager;
    private boolean isDark;
    private Sensor lightSensor;
    private SensorEventListener lightSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (savedInstanceState != null)
        {
            isDark = savedInstanceState.getBoolean("isDark");
        }
        lightSensorEventListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT)
                {
                    float lux = event.values[0];
                    boolean prevDark = isDark;
                    if (lux < 20f)
                    {
                        isDark = true;
                    }
                    if (lux > 30f)
                    {
                        isDark = false;
                    }
                    if (prevDark != isDark)
                    {
                        AppCompatDelegate.setDefaultNightMode((isDark) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        };
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(lightSensorEventListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isDark", isDark);
    }
}