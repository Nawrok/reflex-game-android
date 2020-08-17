package pl.embedded.reflex.activities;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;

import java.util.Locale;

import pl.embedded.reflex.R;
import pl.embedded.reflex.sensors.LightDetector;
import pl.embedded.reflex.sensors.listeners.LightEventListener;

public class BaseActivity extends LocaleAwareCompatActivity implements LightEventListener
{
    private static Locale LOCALE = Locale.getDefault();
    protected SensorManager sensorManager;
    private LightDetector lightDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!LOCALE.equals(Locale.getDefault()))
        {
            Toast.makeText(this, R.string.lang_change, Toast.LENGTH_SHORT).show();
            LOCALE = Locale.getDefault();
        }
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        if (savedInstanceState == null)
        {
            lightDetector = new LightDetector(this, false);
        }
        else
        {
            lightDetector = new LightDetector(this, savedInstanceState.getBoolean("dark"));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        lightDetector.register(sensorManager);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        lightDetector.unregister(sensorManager);
    }

    @Override
    public void onLightChanged(boolean dark)
    {
        AppCompatDelegate.setDefaultNightMode((dark) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void updateLocale(@NonNull Locale locale)
    {
        if (!locale.equals(Locale.getDefault()))
        {
            super.updateLocale(locale);
        }
    }

    @Override
    public void startActivity(Intent intent)
    {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dark", AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    }
}