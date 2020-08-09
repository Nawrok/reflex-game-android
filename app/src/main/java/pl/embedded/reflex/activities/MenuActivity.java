package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.embedded.reflex.R;
import pl.embedded.reflex.enums.Language;
import pl.embedded.reflex.util.LocaleHelper;

public class MenuActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        hideSystemUI();
        loadAuthors();
        //Toast.makeText(getBaseContext(), R.string.lang_change, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            hideSystemUI();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    public void onClickChangeLang(View view)
    {
        switch (view.getId())
        {
            case (R.id.english_lang_button):
            {
                changeLang(Language.ENGLISH);
                break;
            }
            case (R.id.polish_lang_button):
            {
                changeLang(Language.POLISH);
                break;
            }
            default:
            {
                break;
            }
        }
    }

    public void onClickPlay(View view)
    {
        startActivity(new Intent(this, GameActivity.class));
    }

    private void changeLang(Language language)
    {
        LocaleHelper.setLocale(getBaseContext(), language.toString());
        reload(getIntent());
    }

    private void reload(Intent intent)
    {
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void hideSystemUI()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void loadAuthors()
    {
        TextView authors = findViewById(R.id.authors_list);
        StringBuilder sb = new StringBuilder();
        for (String author : getResources().getStringArray(R.array.authors_list))
        {
            sb.append(author).append(System.lineSeparator());
        }
        authors.setText(sb.toString().trim());
    }
}