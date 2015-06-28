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
public class OfflineClient extends Client implements MsgListener {

    private final ServerBehaviour behaviour;
    private final LocalSocket localSocket;

    public OfflineClient(int numAIs, int numCols, int numRows){

        List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
        List<GridModel> grids = new ArrayList<GridModel>();

        for(int i = 0; i < numAIs; i++){
            AI ai = new AI();
            GridModel aiGrid = new GridModel(numCols, numRows);
            ai.initialize(aiGrid);
            grids.add(aiGrid);
            PlayerSocket socket = new BotSocket(ai, i);
            sockets.add(socket);
        }

        GridModel playerGrid = new GridModel(numCols, numRows);
        localSocket = new LocalSocket("LOCAL_PLAYER", this);
        sockets.add(localSocket);
        grids.add(playerGrid);

        behaviour = new ServerBehaviour(sockets, grids);
    }

    @Override
    public void sendMessage(final Msg<ClientMsg> msg) {
        localSocket.handleMessage(msg);
    }

    @Override
    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Debug.e("Starting offline client ...");
                behaviour.runGameLoop();
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Msg msg) {
        Debug.d("OfflineClient.handleMessage(" + msg + ")");
        delegateToListener(msg);
        return true;
    }
}
