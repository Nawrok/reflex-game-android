package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
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
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.AudioPlayer;
import pl.embedded.reflex.sensors.MotionDetector;
import pl.embedded.reflex.sensors.Torch;
import pl.embedded.reflex.sensors.listeners.MotionEventListener;
import pl.embedded.reflex.util.timer.Timer;
import pl.embedded.reflex.util.timer.TimerListener;

public class GameActivity extends BaseActivity implements MotionEventListener, TimerListener
{
    private static final int GAMETIME = 60_000;
    private GameController gameController;
    private Timer timer;
    private MotionDetector motionDetector;
    private Torch torch;
    private Vibrator vibrator;
    private AudioPlayer audioPlayer;
    private long timestamp, timeDelay, millisUntilFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        motionDetector = new MotionDetector(this);
        torch = new Torch((CameraManager) getSystemService(Context.CAMERA_SERVICE));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioPlayer = new AudioPlayer();
        audioPlayer.load(this, R.raw.point);
        if (savedInstanceState == null)
        {
            gameController = new GameController(new Game());
            gameController.randomizePosition();
            millisUntilFinished = GAMETIME;
        }
        else
        {
            gameController = new GameController(Parcels.unwrap(savedInstanceState.getParcelable("game")));
            timestamp = savedInstanceState.getLong("timestamp");
            timeDelay = savedInstanceState.getLong("timeDelay");
            millisUntilFinished = savedInstanceState.getLong("millis");
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
        timer = new Timer(millisUntilFinished, 100, this);
        timer.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onMotionChanged(Position position, long eventTimestamp)
    {
        if (timestamp != 0)
        {
            timeDelay += (eventTimestamp - timestamp) / 1_000_000;
            if (position == gameController.getGame().getPosition())
            {
                gameController.addScore(10);
                gameController.randomizePosition();
                updateGameUI();
                torch.flash();
                audioPlayer.play(R.raw.point);
                timeDelay = 0;
            }
            else if (position != Position.IDLE && timeDelay > 650)
            {
                vibrator.vibrate(VibrationEffect.createOneShot(200, 200));
                timeDelay = 100;
            }
        }
        timestamp = eventTimestamp;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        this.millisUntilFinished = millisUntilFinished;
        int time = (int) millisUntilFinished / 100;
        ((TextView) findViewById(R.id.timer)).setText(String.valueOf(time / 10));
        ((ProgressBar) findViewById(R.id.timer_progress)).setProgress(time);
    }

    @Override
    public void onFinish()
    {
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(() -> torch.flash(), i * 350);
        }
        startActivity(new Intent(this, ResultActivity.class).putExtra("score", gameController.getGame().getScore()));
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("game", Parcels.wrap(gameController.getGame()));
        outState.putLong("timestamp", timestamp);
        outState.putLong("timeDelay", timeDelay);
        outState.putLong("millis", millisUntilFinished);
    }

    private void updateGameUI()
    {
        ((ImageView) findViewById(R.id.position)).setImageResource(gameController.getGame().getPosition().getImage());
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(gameController.getGame().getScore()));
    }
}