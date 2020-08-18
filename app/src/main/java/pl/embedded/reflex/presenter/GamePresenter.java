package pl.embedded.reflex.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.security.SecureRandom;
import java.util.Arrays;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.AudioPlayer;
import pl.embedded.reflex.sensors.MotionDetector;
import pl.embedded.reflex.sensors.Torch;
import pl.embedded.reflex.sensors.callbacks.MotionEventListener;
import pl.embedded.reflex.util.timer.Timer;
import pl.embedded.reflex.util.timer.TimerListener;
import pl.embedded.reflex.view.GameView;

public class GamePresenter extends BasePresenter<GameView> implements MotionEventListener, TimerListener
{
    public static final int GAMETIME = 60_000;
    private static final SecureRandom random = new SecureRandom();
    private final Game game;
    private final MotionDetector motionDetector;
    private final Torch torch;
    private final Vibrator vibrator;
    private final AudioPlayer audioPlayer;
    private Timer timer;
    private long timestamp, timeCooldown, timeUntilFinished;

    public GamePresenter(Context context, Game game)
    {
        this.game = game;
        this.motionDetector = new MotionDetector(this);
        this.torch = new Torch((CameraManager) context.getSystemService(Context.CAMERA_SERVICE));
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.audioPlayer = new AudioPlayer();
        this.audioPlayer.load(context, R.raw.point);
    }

    public void displayGameScorePosition()
    {
        view.showGameScorePosition(game.getScore(), game.getPosition());
    }

    public void addGameScore(int score)
    {
        if (score >= 0)
        {
            game.setScore(game.getScore() + score);
        }
    }

    public void randomizeGamePosition()
    {
        Position lastPosition = game.getPosition();
        Position position;
        do
        {
            int rand = random.nextInt(Position.values().length - 1) + 1;
            position = Arrays.asList(Position.values()).get(rand);
        } while (position.equals(lastPosition));
        game.setPosition(position);
    }

    public int saveHighscore(SharedPreferences preferences, int score)
    {
        int highscore = preferences.getInt(App.GAME_PREFS_HIGHSCORE, 0);
        if (highscore < score)
        {
            preferences.edit().putInt(App.GAME_PREFS_HIGHSCORE, score).apply();
            highscore = score;
        }
        return highscore;
    }

    @Override
    public void onMotionChanged(Position position, long eventTimestamp)
    {
        if (timestamp != 0)
        {
            timeCooldown += (eventTimestamp - timestamp) / 1_000_000;
            if (position == game.getPosition())
            {
                addGameScore(10);
                randomizeGamePosition();
                displayGameScorePosition();
                torch.flash();
                audioPlayer.play(R.raw.point);
                timeCooldown = 0;
            }
            else if (position != Position.IDLE && timeCooldown > 500)
            {
                vibrator.vibrate(VibrationEffect.createOneShot(200, 200));
                timeCooldown = 0;
            }
        }
        timestamp = eventTimestamp;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        timeUntilFinished = millisUntilFinished;
        int time = (int) millisUntilFinished / 100;
        view.showGameTimer(String.valueOf(time / 10), time);
    }

    @Override
    public void onFinish()
    {
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(torch::flash, i * 350);
        }
        view.switchToResultView(game.getScore());
    }

    public void registerMotionDetector(SensorManager sensorManager)
    {
        motionDetector.register(sensorManager);
    }

    public void unregisterMotionDetector(SensorManager sensorManager)
    {
        motionDetector.unregister(sensorManager);
    }

    public void startTimer()
    {
        timer = new Timer(timeUntilFinished, 100, this);
        timer.start();
    }

    public void stopTimer()
    {
        timer.cancel();
    }

    public Game getGame()
    {
        return new Game(game);
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getTimeCooldown()
    {
        return timeCooldown;
    }

    public void setTimeCooldown(long timeCooldown)
    {
        this.timeCooldown = timeCooldown;
    }

    public long getTimeUntilFinished()
    {
        return timeUntilFinished;
    }

    public void setTimeUntilFinished(long timeUntilFinished)
    {
        this.timeUntilFinished = timeUntilFinished;
    }
}
