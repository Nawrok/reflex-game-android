package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;
import pl.embedded.reflex.util.LocaleHelper;

public class ResultActivity extends AppCompatActivity
{
    private static final String HIGHSCORE = "Game.Highscore";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        hideSystemUI();

        int score = getIntent().getIntExtra("score", 0);
        ((TextView) findViewById(R.id.highscore)).setText(String.valueOf(saveHighscore(score)));
        ((TextView) findViewById(R.id.score_result)).setText(String.valueOf(score));
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

    public void onClickTryAgain(View view)
    {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    public void onClickExitToMenu(View view)
    {
        onBackPressed();
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

    private int saveHighscore(int score)
    {
        SharedPreferences preferences = getSharedPreferences(App.GAME_PREFS, Context.MODE_PRIVATE);
        int highscore = preferences.getInt(HIGHSCORE, 0);
        if (highscore < score)
        {
            preferences.edit().putInt(HIGHSCORE, score).apply();
            highscore = score;
        }
        return highscore;
    }
}