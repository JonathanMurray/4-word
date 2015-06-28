package fourword.protocol;

import fourword.model.LobbyPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class Lobby {
    String hostingPlayer;
    HashMap<String, LobbyPlayer> lobbyState = new HashMap<>();

    public Lobby(String hostingPlayer){
        this.hostingPlayer = hostingPlayer;
        lobbyState.put(hostingPlayer, new LobbyPlayer(hostingPlayer, true, true));
    }

    public HashMap<String, LobbyPlayer> getLobbyStateCopy(){
        HashMap<String, LobbyPlayer> newMap = new HashMap<>();
        for(String k : lobbyState.keySet()){
            newMap.put(k, lobbyState.get(k).getCopy());
        }
        return newMap;
    }

    public String toString(){
        return "Lobby(" + hostingPlayer + ", " + lobbyState + ")";
    }
}
