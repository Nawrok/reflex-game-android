package pl.embedded.reflex.sensors;

import android.content.Context;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

public class SoundManager
{
    private final SoundPool soundPool;
    private final Map<Integer, Integer> sounds;

    private SoundManager()
    {
        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(Integer.MAX_VALUE)
                .build();
        this.sounds = new HashMap<>();
    }

    public static SoundManager getInstance()
    {
        return Holder.INSTANCE;
    }

    public void addSound(Context context, int resId)
    {
        sounds.put(resId, soundPool.load(context, resId, 1));
    }

    public void playSound(int resId)
    {
        Integer soundId = sounds.get(resId);
        if (soundId != null)
        {
            soundPool.play(soundId, 1, 1, 1, 0, 1f);
        }
    }

    private static class Holder
    {
        private static final SoundManager INSTANCE = new SoundManager();
    }
}
