package fourword.messages;

import fourword.model.GameResult;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgGameFinished extends Msg<ServerMsg> {
    public GameResult result;

    public MsgGameFinished( GameResult result) {
        super(ServerMsg.GAME_FINISHED);
        this.result = result;
    }
}
