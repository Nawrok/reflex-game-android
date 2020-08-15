package pl.embedded.reflex.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;

import java.util.Locale;

import pl.embedded.reflex.R;

public class BaseActivity extends LocaleAwareCompatActivity
{
    private static Locale LOCALE = Locale.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!LOCALE.equals(Locale.getDefault()))
        {
            Toast.makeText(this, R.string.lang_change, Toast.LENGTH_SHORT).show();
            LOCALE = Locale.getDefault();
        }
    }

    @Override
    public void updateLocale(@NonNull Locale locale)
    {
        if (!locale.equals(Locale.getDefault()))
        {
            super.updateLocale(locale);
        }
    }
}