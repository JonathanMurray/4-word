package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgWaitingForPlayerMove extends ServerMsg {

    public String playerName;

    public MsgWaitingForPlayerMove(String playerName) {
        super(ServerMsgType.WAITING_FOR_PLAYER_MOVE);
        this.playerName = playerName;
    }
}
