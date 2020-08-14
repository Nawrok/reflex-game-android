package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.embedded.reflex.R;
import pl.embedded.reflex.callbacks.RotationSensorEventListener;
import pl.embedded.reflex.controller.GameController;
import pl.embedded.reflex.enums.Position;
import pl.embedded.reflex.model.Game;

public class GameActivity extends LightSensorActivity
{
    private static final int TIME = 60_000;
    private GameController gameController;
    private CameraManager cameraManager;
    private Sensor rotationSensor;
    private RotationSensorEventListener rotationSensorEventListener;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private long timestamp, timeDelay, millisUntilFinished;

    public final GameController getGameController()
    {
        return gameController;
    }

    public final Vibrator getVibrator()
    {
        return vibrator;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getTimeDelay()
    {
        return timeDelay;
    }

    public void setTimeDelay(long timeDelay)
    {
        this.timeDelay = timeDelay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        rotationSensorEventListener = new RotationSensorEventListener(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (savedInstanceState == null)
        {
            gameController = new GameController(new Game());
            gameController.randomizePosition();
            millisUntilFinished = TIME;
        }
        else
        {
            gameController = new GameController(savedInstanceState.getParcelable("game"));
            millisUntilFinished = savedInstanceState.getLong("millis");
            timestamp = savedInstanceState.getLong("timestamp");
            timeDelay = savedInstanceState.getLong("timeDelay");
        }
        updateUI();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(rotationSensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(rotationSensorEventListener);
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
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("game", gameController.getGame());
        outState.putLong("millis", millisUntilFinished);
        outState.putLong("timestamp", timestamp);
        outState.putLong("timeDelay", timeDelay);
    }

    public Position getDevicePosition(float[] orientation)
    {
        double angle_x = orientation[2];
        double angle_y = orientation[1];

        Position position = Position.IDLE;
        if (angle_x < -35.0)
        {
            position = Position.LEFT;
        }
        if (angle_x > 35.0)
        {
            position = Position.RIGHT;
        }
        if (angle_y < -30.0)
        {
            position = Position.DOWN;
        }
        if (angle_y > 30.0)
        {
            position = Position.UP;
        }
        return position;
    }

    public void flashTorch()
    {
        enableTorch(true);
        new Handler().postDelayed(() -> enableTorch(false), 150);
    }

    public void updateUI()
    {
        ImageView arrow = findViewById(R.id.position);
        arrow.setImageResource(gameController.getGame().getPosition().getImage());
        arrow.setContentDescription(gameController.getGame().getPosition().name());
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(gameController.getGame().getScore()));
    }

    private void endGame(int score)
    {
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(this::flashTorch, i * 350);
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private CountDownTimer createTimer(long millisUntilFinished)
    {
        return new CountDownTimer(millisUntilFinished, 100)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                GameActivity.this.millisUntilFinished = millisUntilFinished;
                int seconds = (int) millisUntilFinished / 1000;
                ((TextView) findViewById(R.id.timer)).setText(String.valueOf(seconds));
                ((ProgressBar) findViewById(R.id.timer_progress)).setProgress(seconds);
            }

            @Override
            public void onFinish()
            {
                endGame(gameController.getGame().getScore());
            }
        };
    }

    private void enableTorch(boolean enabled)
    {
        try
        {
            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], enabled);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}