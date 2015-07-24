package fourword;

import fourword_shared.model.PlayerInfo;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.Iterator;
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
    private List<PlayerInfo> otherOnlinePlayers = new ArrayList();
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

    public List<PlayerInfo> getOtherOnlinePlayers(){
        return otherOnlinePlayers;
    }

    public String[] getOtherOnlinePlayerStrings(){
        List<String> strings = new ArrayList<String>();
        for(PlayerInfo info : getOtherOnlinePlayers()){
            strings.add(info.name + "   " + info.infoString());
        }
        return strings.toArray(new String[0]);
    }

    public String[] getOtherOnlinePlayerNames(){
        List<String> strings = new ArrayList<String>();
        for(PlayerInfo info : getOtherOnlinePlayers()){
            strings.add(info.name);
        }
        return strings.toArray(new String[0]);
    }

    private void removeInfoWithName(List<PlayerInfo> list, String name){
        Iterator<PlayerInfo> infos = list.iterator();
        while(infos.hasNext()){
            if(infos.next().name.equals(name)){
                infos.remove();
                break;
            }
        }
    }


    public void notifyOnlinePlayers(List<PlayerInfo> onlinePlayers){
        otherOnlinePlayers.clear();
        otherOnlinePlayers.addAll(onlinePlayers);
        removeInfoWithName(otherOnlinePlayers, playerName);

        Debug.e("Persistent updating other online players: " + otherOnlinePlayers);
        if(listener != null){
            Debug.e("Notifying listener");
            listener.notifyOthersOnline(otherOnlinePlayers);
        }else{
            Debug.e("NO LISTENER!");
        }
    }

    public void notifyPlayerInfo(PlayerInfo info) {
        boolean updatedExisting = false;
        if(info.name.equals(playerName)){
            return; //Don't care about the player of this client
        }
        for(int i = 0; i < otherOnlinePlayers.size(); i++){
            PlayerInfo existing = otherOnlinePlayers.get(i);
            if(existing.name.equals(info.name)){
                otherOnlinePlayers.set(i, info);
                updatedExisting = true;
                break;
            }
        }
        if(!updatedExisting){
            otherOnlinePlayers.add(info);
        }
        if(listener != null){
            Debug.e("Notifying listener");
            listener.notifyOthersOnline(otherOnlinePlayers);
        }else{
            Debug.e("NO LISTENER!");
        }
    }

    public void notifyLoggedOut(String name) {
        removeWithName(name);
        if(listener != null){
            Debug.e("Notifying listener");
            listener.notifyOthersOnline(otherOnlinePlayers);
        }else{
            Debug.e("NO LISTENER!");
        }
    }

    private void removeWithName(String name){
        Iterator<PlayerInfo> it = otherOnlinePlayers.iterator();
        while(it.hasNext()){
            PlayerInfo existing = it.next();
            if(existing.name.equals(name)){
                it.remove();
                break;
            }
        }
    }

    interface OnlineListener{
        void notifyOthersOnline(List<PlayerInfo> othersOnline);
    }

}
