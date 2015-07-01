package fourword;

import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-28.
 */
public class Persistent {
    private static Persistent INSTANCE = new Persistent();
    public static Persistent instance(){
        return INSTANCE;
    }

    private String playerName;
    private List<String> otherOnlinePlayers = new ArrayList<>();
    private OnlineListener listener;

    public void setOnlineListener(OnlineListener listener){
        this.listener = listener;
    }

    public void removeOnlineListener(){
        listener = null;
    }

    public void init(String playerName){
        this.playerName = playerName;
    }

    public String playerName(){
        return playerName;
    }

    public List<String> getOtherOnlinePlayers(){
        return otherOnlinePlayers;
    }



    public void notifyOnlinePlayers(List<String> onlinePlayers){
        otherOnlinePlayers.clear();
        otherOnlinePlayers.addAll(onlinePlayers);
        otherOnlinePlayers.remove(playerName);
        Debug.e("Persistent updating online players");
        if(listener != null){
            Debug.e("Notifying listener");
            listener.notifyOthersOnline(otherOnlinePlayers);
        }else{
            Debug.e("NO LISTENER!");
        }
    }

    interface OnlineListener{
        void notifyOthersOnline(List<String> othersOnline);
    }

}
