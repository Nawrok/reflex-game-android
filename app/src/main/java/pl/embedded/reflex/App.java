package pl.embedded.reflex;

import androidx.appcompat.app.AppCompatDelegate;

import com.zeugmasolutions.localehelper.LocaleAwareApplication;

public class App extends LocaleAwareApplication
{
    public static final String GAME_PREFS = "Game";
    public static final String GAME_PREFS_HIGHSCORE = "Highscore";

    @Override
    public void onCreate()
    {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
