package pl.embedded.reflex.activities;

import android.app.Service;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;
import com.zeugmasolutions.localehelper.LocaleHelper;

import java.util.Locale;

import pl.embedded.reflex.R;
import pl.embedded.reflex.sensors.LightDetector;
import pl.embedded.reflex.sensors.callbacks.LightEventListener;

public abstract class BaseActivity extends LocaleAwareCompatActivity implements LightEventListener
{
    protected SensorManager sensorManager;
    private LightDetector lightDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightDetector = new LightDetector(this, (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES));
        if (getIntent().getBooleanExtra("localeChanged", false))
        {
            Toast.makeText(this, R.string.lang_change, Toast.LENGTH_SHORT).show();
            getIntent().removeExtra("localeChanged");
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
        if (isFinishing())
        {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onLightChanged(boolean dark)
    {
        AppCompatDelegate.setDefaultNightMode((dark) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void updateLocale(@NonNull Locale locale)
    {
        if (!locale.equals(LocaleHelper.INSTANCE.getLocale(this)))
        {
            super.updateLocale(locale);
            getIntent().putExtra("localeChanged", true);
        }
    }
}