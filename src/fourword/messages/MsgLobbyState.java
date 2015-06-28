package fourword.messages;

import fourword.model.LobbyPlayer;
import fourword.protocol.Lobby;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgLobbyState extends Msg {
    public final Lobby lobby;

    public MsgLobbyState(Lobby lobby) {
        super(ServerMsg.LOBBY_STATE);
        this.lobby = lobby;
    }

    public String toString(){
        return "MsgLobbyState(" + lobby + ")";
    }

}
