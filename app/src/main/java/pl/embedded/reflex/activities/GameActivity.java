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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.embedded.reflex.R;
import pl.embedded.reflex.controller.GameController;
import pl.embedded.reflex.enums.Position;
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.util.LocaleHelper;

public class GameActivity extends AppCompatActivity implements SensorEventListener
{
    private static final int TIME = 60_000;
    private GameController gameController;
    private SensorManager sensorManager;
    private CameraManager cameraManager;
    private String cameraId;
    private Sensor rotationSensor;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private long timestamp, timeDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideSystemUI();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            this.cameraId = cameraManager.getCameraIdList()[0];
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        this.gameController = new GameController(new Game());
        this.countDownTimer = buildCountDownTimer(TIME, 1000);
        this.countDownTimer.start();
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
    protected void onDestroy()
    {
        super.onDestroy();
        countDownTimer.cancel();
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

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        ((ImageView) findViewById(R.id.position)).setImageResource(gameController.getPosition().getImage());
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(gameController.getScore()));
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR)
        {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            for (int i = 0; i < 3; i++)
            {
                orientation[i] = (float) Math.toDegrees(orientation[i]);
            }

            if (timestamp != 0)
            {
                timeDelay += (event.timestamp - timestamp) / 1_000_000;
                Position devicePos = getDevicePosition(orientation);
                if (gameController.getPosition() == devicePos)
                {
                    gameController.addScore(10);
                    gameController.randomizePosition();
                    flashTorch();
                    timeDelay = 0;
                }
                else if (devicePos != Position.IDLE && gameController.getPosition() != devicePos && timeDelay > 600)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(150, 255));
                    timeDelay = 150;
                }
            }
            timestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    private void endGame(int score)
    {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private CountDownTimer buildCountDownTimer(long time, long interval)
    {
        return new CountDownTimer(time, interval)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                int seconds = (int) millisUntilFinished / (int) interval;
                ((TextView) findViewById(R.id.timer)).setText(String.valueOf(seconds));
                ((ProgressBar) findViewById(R.id.timer_progress)).setProgress(seconds);
            }

            @Override
            public void onFinish()
            {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 250, 100, 250, 100, 250}, -1));
                endGame(gameController.getScore());
            }
        };
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
        Handler handler = new Handler();
        switchFlashLight(true);
        handler.postDelayed(() -> switchFlashLight(false), 150);
    }

    private void switchFlashLight(boolean status)
    {
        try
        {
            cameraManager.setTorchMode(cameraId, status);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}