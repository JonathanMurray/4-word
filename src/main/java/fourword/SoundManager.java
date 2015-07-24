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


    public static void create(Context context){
        if(instance == null){
            instance = new SoundManager(context);
        }
    }

    public static SoundManager instance(){
        if(instance == null){
            throw new RuntimeException("SoundManager not set up");
        }
        return instance;
    }

    private final Context context;

    private HashSet<Integer> queuedSounds = new HashSet<Integer>();
    private HashSet<Integer> loadedSounds = new HashSet<Integer>();

    public static int ENTER_GAME;
    public static int YOU_ARE_INVITED;
    public static int YOUR_TURN;
    public static int PLAYER_DONE_THINKING;
    public static int ADDED_TO_LOBBY;
    public static int JOINED_LOBBY;

    public SoundManager(Context context){
        this.context = context;
        ENTER_GAME =  load(R.raw.mickleness__player_turn_start);
        YOU_ARE_INVITED = load(R.raw.oceanictrancer__happy_effect_3);
        YOUR_TURN = load(R.raw.qubodup__notification_signal);
        PLAYER_DONE_THINKING = load(R.raw.porphyr__waterdrop);
        ADDED_TO_LOBBY = load(R.raw.mickleness__chat_message_6_trade_not_mine);
        JOINED_LOBBY = load(R.raw.kollege__mailinbox2);
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
        return soundPool.load(context, id, 1);
    }

    public void play(int soundId){
        if(loadedSounds.contains(soundId)){
            System.out.println("Playing sound " + soundId);
            soundPool.play(soundId, 1, 1, 1, 0, 1f);
        }else{
            System.out.println("Queueing sound " + soundId);
            queuedSounds.add(soundId);
        }
    }
}
