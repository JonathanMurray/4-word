package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgGameIsStarting extends Msg<ServerMsg> {

    public int numCols;
    public int numRows;
    public String[] sortedPlayerNames;

    public MsgGameIsStarting(int numCols, int numRows, String[] sortedPlayerNames) {
        super(ServerMsg.GAME_IS_STARTING);
        this.numCols = numCols;
        this.numRows = numRows;
        this.sortedPlayerNames = sortedPlayerNames;
    }
}
