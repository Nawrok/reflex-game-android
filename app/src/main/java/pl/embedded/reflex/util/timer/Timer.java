package pl.embedded.reflex.util.timer;

import android.os.CountDownTimer;

public class Timer extends CountDownTimer
{
    private final TimerListener listener;

    public Timer(long millisInFuture, long countDownInterval, TimerListener listener)
    {
        super(millisInFuture, countDownInterval);
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        listener.onTick(millisUntilFinished);
    }

    @Override
    public void onFinish()
    {
        listener.onFinish();
    }
}
