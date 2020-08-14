package pl.embedded.reflex.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import pl.embedded.reflex.callbacks.LightSensorEventListener;

public class LightSensorActivity extends BaseActivity
{
    protected SensorManager sensorManager;
    private Sensor lightSensor;
    private LightSensorEventListener lightSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorEventListener = new LightSensorEventListener(false);
        if (savedInstanceState != null)
        {
            lightSensorEventListener.setDark(savedInstanceState.getBoolean("isDark"));
        }
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
        outState.putBoolean("isDark", lightSensorEventListener.isDark());
    }
}