package fourword.client;

import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.model.GridModel;
import fourword_shared.messages.MsgListener;
//import fourword.server.*;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-24.
 */
public class OfflineClient{

//       exends Client implements MsgListener, ServerGameBehaviour.GameListener {
//
//    private final ServerGameBehaviour behaviour;
//    private final LocalSocket localSocket;
//
//    public OfflineClient(int numAIs, int numCols, int numRows){
//
//        List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
//        List<GridModel> grids = new ArrayList<GridModel>();
//
//        GridModel playerGrid = new GridModel(numCols, numRows);
//        localSocket = new LocalSocket("Player", this);
//        sockets.add(localSocket);
//        grids.add(playerGrid);
//
//        GameObject game = new GameObject(numAIs + 1, localSocket.getName(), numCols, numRows);
//
//        for(int i = 0; i < numAIs; i++){
//            AI ai = new AI();
//            GridModel aiGrid = new GridModel(numCols, numRows);
//            ai.initialize(aiGrid);
//            grids.add(aiGrid);
//            PlayerSocket socket = new BotSocket(ai, "Bot_" + i);
//            sockets.add(socket);
//        }
//        behaviour = new ServerGameBehaviour(this, game);
//    }
//
//    @Override
//    public void sendMessage(final Msg<ClientMsg> msg) {
//        localSocket.handleMessage(msg);
//    }
//
//    @Override
//    public void start(){
//        new Thread(behaviour).start();
//    }
//
//    @Override
//    public boolean handleMessage(Msg msg) {
//        Debug.d("OfflineClient.handleMessage(" + msg + ")");
//        delegateToListener(msg);
//        return true;
//    }
//
//    @Override
//    public void gameFinished(GameObject game) {
//        //TODO
//    }
//
//    @Override
//    public void gameCrashed(GameObject game) {
//        //TODO
//    }
}
