package pl.embedded.reflex.sensors;

import android.content.Context;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

public class AudioPlayer
{
    private final SoundPool soundPool;
    private final Map<Integer, Integer> sounds;

    public AudioPlayer()
    {
        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(Integer.MAX_VALUE)
                .build();
        this.sounds = new HashMap<>();
    }

    public void load(Context context, int resId)
    {
        sounds.put(resId, soundPool.load(context, resId, 1));
    }

    public void play(int resId)
    {
        Integer soundId = sounds.get(resId);
        if (soundId != null)
        {
            soundPool.play(soundId, 1, 1, 1, 0, 1f);
        }
    }
}
