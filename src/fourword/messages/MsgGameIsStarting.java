package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgGameIsStarting extends Msg<ServerMsg> {

    public int numCols;
    public int numRows;

    public MsgGameIsStarting(int numCols, int numRows) {
        super(ServerMsg.GAME_IS_STARTING);
        this.numCols = numCols;
        this.numRows = numRows;
    }
}
