package fourword.messages;

import fourword.client.Client;

/**
 * Created by jonathan on 2015-07-03.
 */
public class MsgLobbySetDim extends Msg<ClientMsg>{

    public int numCols;
    public int numRows;

    public MsgLobbySetDim(int numCols, int numRows){
        super(ClientMsg.LOBBY_SET_DIMENSIONS);
        this.numCols = numCols;
        this.numRows = numRows;
    }

    public String toString(){
        return "MsgLobbySetDim(" + numCols + ", " + numRows + ")";
    }
}
