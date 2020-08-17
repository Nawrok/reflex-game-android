package pl.embedded.reflex.util.timer;

public interface TimerListener
{
    void onTick(long millisUntilFinished);

    void onFinish();
}
