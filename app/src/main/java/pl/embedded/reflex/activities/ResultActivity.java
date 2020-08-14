package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;

public class ResultActivity extends LightSensorActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("score", 0);
        ((TextView) findViewById(R.id.highscore)).setText(String.valueOf(saveHighscore(score)));
        ((TextView) findViewById(R.id.score_result)).setText(String.valueOf(score));
    }

    public void onClickTryAgain(View view)
    {
        startActivity(new Intent(this, GameActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void onClickExitToMenu(View view)
    {
        onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private int saveHighscore(int score)
    {
        SharedPreferences preferences = getSharedPreferences(App.GAME_PREFS, Context.MODE_PRIVATE);
        int highscore = preferences.getInt(App.GAME_PREFS_HIGHSCORE, 0);
        if (highscore < score)
        {
            preferences.edit().putInt(App.GAME_PREFS_HIGHSCORE, score).apply();
            highscore = score;
        }
        return highscore;
    }
}