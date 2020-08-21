package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;

public class ResultActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        showScores(getIntent().getIntExtra("score", 0), getIntent().getIntExtra("moves", 0));
    }

    public void startOverGame(View view)
    {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    public void exitToMenu(View view)
    {
        onBackPressed();
    }

    private void showScores(int score, int moves)
    {
        ((TextView) findViewById(R.id.score_result)).setText(String.valueOf(score));
        ((TextView) findViewById(R.id.highscore)).setText(String.valueOf(getSharedPreferences(App.class.getName(), Context.MODE_PRIVATE).getInt(App.GAME_PREFS_HIGHSCORE, score)));
        ((TextView) findViewById(R.id.score_result_moves)).setText(String.valueOf(moves));
        ((TextView) findViewById(R.id.highscore_moves)).setText(String.valueOf(getSharedPreferences(App.class.getName(), Context.MODE_PRIVATE).getInt(App.GAME_PREFS_MOVES, moves)));
    }
}