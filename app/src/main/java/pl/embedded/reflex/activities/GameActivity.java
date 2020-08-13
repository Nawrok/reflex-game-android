package pl.embedded.reflex.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.embedded.reflex.R;
import pl.embedded.reflex.controller.GameController;
import pl.embedded.reflex.enums.Position;
import pl.embedded.reflex.model.Game;

public class GameActivity extends LightSensorActivity implements SensorEventListener
{
    private static final int TIME = 60_000;
    private GameController gameController;
    private CameraManager cameraManager;
    private String cameraId;
    private Sensor rotationSensor;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private long timestamp, timeDelay, millisUntilFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            cameraId = cameraManager.getCameraIdList()[0];
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
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
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
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
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR)
        {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            for (int i = 0; i < orientation.length; i++)
            {
                orientation[i] = (float) Math.toDegrees(orientation[i]);
            }

            if (timestamp != 0)
            {
                timeDelay += (event.timestamp - timestamp) / 1_000_000;
                Position devicePos = getDevicePosition(orientation);
                if (gameController.getGame().getPosition() == devicePos)
                {
                    gameController.addScore(10);
                    gameController.randomizePosition();
                    updateUI();
                    flashTorch();
                    timeDelay = 0;
                }
                else if (devicePos != Position.IDLE && gameController.getGame().getPosition() != devicePos && timeDelay > 650)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, 200));
                    timeDelay = 100;
                }
            }
            timestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void endGame(int score)
    {
        finish();
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(this::flashTorch, i * 350);
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void updateUI()
    {
        ((ImageView) findViewById(R.id.position)).setImageResource(gameController.getGame().getPosition().getImage());
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(gameController.getGame().getScore()));
    }

    private Position getDevicePosition(float[] orientation)
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

    private void flashTorch()
    {
        enableTorch(true);
        new Handler().postDelayed(() -> enableTorch(false), 150);
    }

    private void enableTorch(boolean enabled)
    {
        try
        {
            cameraManager.setTorchMode(cameraId, enabled);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}