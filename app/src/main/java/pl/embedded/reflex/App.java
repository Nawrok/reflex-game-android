package pl.embedded.reflex;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import pl.embedded.reflex.sensors.SoundManager;

public class App extends Application
{
    public static final String GAME_PREFS_HIGHSCORE = "Highscore";
    public static final String GAME_PREFS_MOVES = "Moves";

    @Override
    public void onCreate()
    {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SoundManager.getInstance().addSound(this, R.raw.point);
    }
}
