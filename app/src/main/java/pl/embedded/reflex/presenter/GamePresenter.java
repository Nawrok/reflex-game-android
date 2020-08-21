package pl.embedded.reflex.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;

import org.parceler.Parcels;

import java.security.SecureRandom;
import java.util.Arrays;

import pl.embedded.reflex.App;
import pl.embedded.reflex.R;
import pl.embedded.reflex.model.Game;
import pl.embedded.reflex.model.Position;
import pl.embedded.reflex.sensors.AudioPlayer;
import pl.embedded.reflex.sensors.Torch;
import pl.embedded.reflex.sensors.callbacks.MotionEventListener;
import pl.embedded.reflex.sensors.motion.MotionDetector;
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
    private long lastUpdate, cooldown, timeUntilFinished;

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
        game.setScore(game.getScore() + score);
    }

    public void addGameMoves(int moves)
    {
        game.setMoves(game.getMoves() + moves);
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

    public void saveGameResult(SharedPreferences preferences, int score, int moves)
    {
        int highscore = preferences.getInt(App.GAME_PREFS_HIGHSCORE, 0);
        if (highscore < score)
        {
            preferences
                    .edit()
                    .putInt(App.GAME_PREFS_HIGHSCORE, score)
                    .putInt(App.GAME_PREFS_MOVES, moves)
                    .apply();
        }
    }

    @Override
    public void onMotionChanged(Position position, double speed, long timestamp)
    {
        if (lastUpdate != 0)
        {
            cooldown += (timestamp - lastUpdate) / 1_000_000;
            if (position == game.getPosition())
            {
                addGameScore(Math.toIntExact(Math.round(10.0 * speed)));
                addGameMoves(1);
                randomizeGamePosition();
                displayGameScorePosition();
                torch.flash();
                audioPlayer.play(R.raw.point);
                cooldown = 0;
            }
            if (position != Position.IDLE && cooldown >= 500)
            {
                vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                cooldown = 0;
            }
        }
        lastUpdate = timestamp;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        timeUntilFinished = millisUntilFinished;
        view.showGameTimer(String.valueOf(Math.round(millisUntilFinished / 1000.0)), Math.toIntExact(millisUntilFinished));
    }

    @Override
    public void onFinish()
    {
        Handler handler = new Handler();
        for (int i = 0; i < 3; i++)
        {
            handler.postDelayed(torch::flash, i * 350);
        }
        view.switchToResultView(game.getScore(), game.getMoves());
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
        timer = new Timer(timeUntilFinished, 10, this);
        timer.start();
    }

    public void stopTimer()
    {
        timer.cancel();
    }

    public Parcelable getGameParcelable()
    {
        return Parcels.wrap(game);
    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public long getCooldown()
    {
        return cooldown;
    }

    public void setCooldown(long cooldown)
    {
        this.cooldown = cooldown;
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
