package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.parceler.Parcels;

import pl.embedded.reflex.R;
import pl.embedded.reflex.controller.GameController;
import pl.embedded.reflex.enums.Position;
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.sensors.MotionDetector;
import pl.embedded.reflex.sensors.Torch;
import pl.embedded.reflex.sensors.listeners.MotionEventListener;

public class GameActivity extends BaseActivity implements MotionEventListener
{
    private static final int TIME = 60_000;
    private GameController gameController;
    private MotionDetector motionDetector;
    private Torch torch;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private long timestamp, timeDelay, millisUntilFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        motionDetector = new MotionDetector(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        torch = new Torch((CameraManager) getSystemService(Context.CAMERA_SERVICE));
        if (savedInstanceState == null)
        {
            gameController = new GameController(new Game());
            gameController.randomizePosition();
            millisUntilFinished = TIME;
        }
        else
        {
            gameController = new GameController(Parcels.unwrap(savedInstanceState.getParcelable("game")));
            millisUntilFinished = savedInstanceState.getLong("millis");
            timestamp = savedInstanceState.getLong("timestamp");
            timeDelay = savedInstanceState.getLong("timeDelay");
        }
        updateGameUI();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        motionDetector.register(sensorManager);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        motionDetector.unregister(sensorManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        countDownTimer = createTimer(millisUntilFinished);
        countDownTimer.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        countDownTimer.cancel();
    }

    @Override
    public void onMotionChanged(Position position, long timestamp)
    {
        if (this.timestamp != 0)
        {
            timeDelay += (timestamp - this.timestamp) / 1_000_000;
            if (position == gameController.getGame().getPosition())
            {
                gameController.addScore(10);
                gameController.randomizePosition();
                updateGameUI();
                torch.flash();
                MediaPlayer.create(this, R.raw.point).start();
                timeDelay = 0;
            }
            else if (position != Position.IDLE && timeDelay > 650)
            {
                vibrator.vibrate(VibrationEffect.createOneShot(200, 200));
                timeDelay = 100;
            }
        }
        this.timestamp = timestamp;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("game", Parcels.wrap(gameController.getGame()));
        outState.putLong("millis", millisUntilFinished);
        outState.putLong("timestamp", timestamp);
        outState.putLong("timeDelay", timeDelay);
    }

    private void endGame(int score)
    {
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(() -> torch.flash(), i * 350);
        }
        startActivity(new Intent(this, ResultActivity.class).putExtra("score", score));
        finish();
    }

    private CountDownTimer createTimer(long millisUntilFinished)
    {
        return new CountDownTimer(millisUntilFinished, 50)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                GameActivity.this.millisUntilFinished = millisUntilFinished;
                int seconds = (int) millisUntilFinished / 1000;
                updateTimerUI(seconds);
            }

            @Override
            public void onFinish()
            {
                endGame(gameController.getGame().getScore());
            }
        };
    }

    private void updateGameUI()
    {
        ((ImageView) findViewById(R.id.position)).setImageResource(gameController.getGame().getPosition().getImage());
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(gameController.getGame().getScore()));
    }

    private void updateTimerUI(int seconds)
    {
        ((TextView) findViewById(R.id.timer)).setText(String.valueOf(seconds));
        ((ProgressBar) findViewById(R.id.timer_progress)).setProgress(seconds);
    }
}