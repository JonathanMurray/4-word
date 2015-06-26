package fourword.messages;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgLobbyWaitingForMore extends ServerMsg{
    public List<LobbyPlayer> lobbyPlayers;

    public MsgLobbyWaitingForMore(List<LobbyPlayer> lobbyPlayers) {
        super(ServerMsgType.LOBBY_WAITING_FOR_MORE_PLAYERS);
        this.lobbyPlayers = lobbyPlayers;
    }

    public static class LobbyPlayer implements Serializable {
        public String name;
        public boolean isHuman;
        public boolean hasConnected;

        public LobbyPlayer(String name, boolean isHuman, boolean hasConnected) {
            this.name = name;
            this.isHuman = isHuman;
            this.hasConnected = hasConnected;
        }
    }
}
