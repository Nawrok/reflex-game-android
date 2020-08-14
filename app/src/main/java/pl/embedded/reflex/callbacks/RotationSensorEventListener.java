package pl.embedded.reflex.callbacks;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.VibrationEffect;

import pl.embedded.reflex.activities.GameActivity;
import pl.embedded.reflex.enums.Position;

public class RotationSensorEventListener implements SensorEventListener
{
    private GameActivity gameActivity;

    public RotationSensorEventListener(GameActivity gameActivity)
    {
        this.gameActivity = gameActivity;
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

            long timestamp = gameActivity.getTimestamp();
            if (timestamp != 0)
            {
                gameActivity.setTimeDelay(gameActivity.getTimeDelay() + ((event.timestamp - timestamp) / 1_000_000));
                Position devicePos = gameActivity.getDevicePosition(orientation);
                if (gameActivity.getGameController().getGame().getPosition() == devicePos)
                {
                    gameActivity.getGameController().addScore(10);
                    gameActivity.getGameController().randomizePosition();
                    gameActivity.updateUI();
                    gameActivity.flashTorch();
                    gameActivity.setTimeDelay(0);
                }
                else if (devicePos != Position.IDLE && gameActivity.getGameController().getGame().getPosition() != devicePos && gameActivity.getTimeDelay() > 650)
                {
                    gameActivity.getVibrator().vibrate(VibrationEffect.createOneShot(200, 200));
                    gameActivity.setTimeDelay(100);
                }
            }
            gameActivity.setTimestamp(event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
