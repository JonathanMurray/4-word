package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.model.GridModel;
import fourword.messages.MsgListener;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-24.
 */
public class OfflineClient extends Client implements MsgListener, ServerGameBehaviour.GameFinishedListener {

    private final ServerGameBehaviour behaviour;
    private final LocalSocket localSocket;

    public OfflineClient(int numAIs, int numCols, int numRows){

        List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
        List<GridModel> grids = new ArrayList<GridModel>();

        GridModel playerGrid = new GridModel(numCols, numRows);
        localSocket = new LocalSocket("Player", this);
        sockets.add(localSocket);
        grids.add(playerGrid);

        GameObject game = new GameObject(numAIs + 1, localSocket.getName(), numCols, numRows);

        for(int i = 0; i < numAIs; i++){
            AI ai = new AI();
            GridModel aiGrid = new GridModel(numCols, numRows);
            ai.initialize(aiGrid);
            grids.add(aiGrid);
            PlayerSocket socket = new BotSocket(ai, "Bot_" + i);
            sockets.add(socket);
        }
        behaviour = new ServerGameBehaviour(this, game);
    }

    @Override
    public void sendMessage(final Msg<ClientMsg> msg) {
        localSocket.handleMessage(msg);
    }

    @Override
    public void start(){
        new Thread(behaviour).start();
    }

    @Override
    public boolean handleMessage(Msg msg) {
        Debug.d("OfflineClient.handleMessage(" + msg + ")");
        delegateToListener(msg);
        return true;
    }

    @Override
    public void gameFinished(GameObject game) {
        //TODO
    }
}
