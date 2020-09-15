package pl.embedded.reflex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.akexorcist.localizationactivity.ui.LocalizationApplication;

import java.util.Locale;

import pl.embedded.reflex.sensors.SoundManager;

public class App extends LocalizationApplication
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

    @NonNull
    @Override
    public Locale getDefaultLanguage()
    {
        return Locale.forLanguageTag("pl");
    }
}
