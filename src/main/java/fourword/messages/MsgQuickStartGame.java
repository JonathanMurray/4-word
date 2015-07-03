package fourword.messages;

/**
 * Created by jonathan on 2015-07-02.
 */
public class MsgQuickStartGame extends Msg<ClientMsg>{

    final public int numCols;
    final public int numRows;
    final public int numBots;

    public MsgQuickStartGame(int numCols, int numRows, int numBots) {
        super(ClientMsg.QUICK_START_GAME);
        this.numCols = numCols;
        this.numRows = numRows;
        this.numBots = numBots;
    }
}
