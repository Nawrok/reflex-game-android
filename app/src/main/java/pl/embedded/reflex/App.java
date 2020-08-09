package pl.embedded.reflex;

import android.app.Application;
import android.content.Context;

import pl.embedded.reflex.util.LocaleHelper;

public class App extends Application
{
    public static final String GAME_PREFS = "Game";

    @Override
    public void onCreate()
    {
        super.onCreate();
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
