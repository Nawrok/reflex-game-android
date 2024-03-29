package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.parceler.Parcels;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.presenter.GamePresenter;
import pl.embedded.reflex.view.GameView;

public class GameActivity extends BaseActivity implements GameView
{
    private GamePresenter gamePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState == null)
        {
            gamePresenter = new GamePresenter(this, new Game());
            gamePresenter.randomizeGamePosition();
            gamePresenter.setTimeUntilFinished(GamePresenter.GAMETIME);
        }
        else
        {
            gamePresenter = new GamePresenter(this, Parcels.unwrap(savedInstanceState.getParcelable("game")));
            gamePresenter.setLastUpdate(savedInstanceState.getLong("lastUpdate"));
            gamePresenter.setCooldown(savedInstanceState.getLong("cooldown"));
            gamePresenter.setTimeUntilFinished(savedInstanceState.getLong("timeUntilFinished"));
        }
        gamePresenter.attach(this);
        gamePresenter.displayGameScorePosition();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gamePresenter.registerMotionDetector(sensorManager);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gamePresenter.unregisterMotionDetector(sensorManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        gamePresenter.startTimer();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        gamePresenter.stopTimer();
    }

    @Override
    public void showGameScorePosition(int score, Position position)
    {
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(score));
        ((ImageView) findViewById(R.id.position)).setImageResource(position.getImage());
    }

    @Override
    public void showGameTimer(String time, int progress)
    {
        ((TextView) findViewById(R.id.timer)).setText(time);
        ((ProgressBar) findViewById(R.id.timer_progress)).setProgress(progress);
    }

    @Override
    public void switchToResultView(int score, int moves)
    {
        gamePresenter.saveGameResult(getSharedPreferences(App.class.getName(), Context.MODE_PRIVATE), score, moves);
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("score", score)
                .putExtra("moves", moves));
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("game", gamePresenter.getGameParcelable());
        outState.putLong("lastUpdate", gamePresenter.getLastUpdate());
        outState.putLong("cooldown", gamePresenter.getCooldown());
        outState.putLong("timeUntilFinished", gamePresenter.getTimeUntilFinished());
    }
}