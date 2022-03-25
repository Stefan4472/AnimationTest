package com.plainsimple.spaceships.engine.external;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.plainsimple.spaceships.engine.audio.SoundID;

import java.util.HashMap;

// TODO: support releasing() / activity lifecycle
// TODO: does this belong in GameEngine?
public class SoundPlayer {
    private Context appContext;
    private SoundPool soundPool;
    // Map SoundID to "resID" loaded by the SoundPool
    private HashMap<SoundID, Integer> resIds;

    public SoundPlayer(Context appContext) {
        this.appContext = appContext;
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        builder.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        soundPool = builder.build();
        resIds = new HashMap<>();
    }

    public void playSound(SoundID sound) {
        Integer resId = resIds.get(sound);
        if (resId == null) {
            resId = loadSound(sound);
        }
//        soundPool.play(resId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // Release all associated memory TODO
//    public void release() {
//
//    }

    // TODO: Pretty sure this will never return null, but *could* it?
    private int loadSound(SoundID sound) {
        int resId = soundPool.load(appContext, sound.getId(), 1);
        resIds.put(sound, resId);
        return resId;
    }

}
