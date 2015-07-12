package fourword;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.example.android_test.R;

import java.util.HashSet;

/**
 * Created by jonathan on 2015-07-10.
 */
public class SoundManager {
    private SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

    private static SoundManager instance;


    public static SoundManager instance(Context context){
        if(instance == null){
            instance = new SoundManager(context);
        }
        return instance;
    }

    private final Context context;

    private HashSet<Integer> queuedSounds = new HashSet<Integer>();
    private HashSet<Integer> loadedSounds = new HashSet<Integer>();

    public static int ENTER_GAME;
    public static int INVITED;

    public SoundManager(Context context){
        this.context = context;
        ENTER_GAME =  load(R.raw.mickleness__player_turn_start);
        INVITED = load(R.raw.morrisjm__dingaling);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedSounds.add(sampleId);
                if(queuedSounds.contains(sampleId)){
                    play(sampleId);
                    queuedSounds.remove(sampleId);
                }
            }
        });

    }

    private int load(int id){
        return soundPool.load(context, R.raw.mickleness__player_turn_start, 1);
    }

    public void play(int soundId){
        if(loadedSounds.contains(soundId)){
            soundPool.play(soundId, 1, 1, 1, 0, 1f);
        }else{
            queuedSounds.add(soundId);
        }
    }
}
