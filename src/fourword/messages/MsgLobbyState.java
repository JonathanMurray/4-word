package fourword.messages;

import fourword.model.LobbyPlayer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgLobbyState extends Msg {
    public HashMap<String, LobbyPlayer> lobbyPlayers;

    public MsgLobbyState(HashMap<String, LobbyPlayer> lobbyPlayers) {
        super(ServerMsg.LOBBY_STATE);
        this.lobbyPlayers = lobbyPlayers;
    }

    public String toString(){
        return "MsgLobbyState(" + lobbyPlayers + ")";
    }

}
