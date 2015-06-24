package fourword;

import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-24.
 */
public class OfflineClient implements Client {

    private AI ai;
    private Listener listener;
    private final int numCols;
    private final int numRows;
    private final int numCells;
    private int numFilledCells;
    private HashMap<String, GridModel> grids;
    private GridModel playerGrid;
    private char pickedLetterByAI;
    private final static String PLAYER_NAME = "PLAYER";

    public OfflineClient(AI ai, int numCols, int numRows){
        this.ai = ai;
        this.numCols = numCols;
        this.numRows = numRows;
        numCells = numRows * numCols;
        numFilledCells = 0;
        GridModel aiGrid = new GridModel(numCols, numRows);
        ai.initialize(aiGrid);
        grids = new HashMap<String, GridModel>();
        grids.put(ai.getPlayerName(), aiGrid);
        playerGrid = new GridModel(numCols, numRows);
        grids.put(PLAYER_NAME, playerGrid);
    }

    @Override
    public void sendMessage(final GameClientMessage msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Debug.d("   to mockup-server: " + msg);
                GameServerMessage reply = actAsServerAndProduceReply(msg);
                Debug.d("   from mockup-server: " + reply);
                Debug.d("   Server grid: ");
                Debug.d(ai.toString());
                listener.handleServerMessage(reply);
            }
        }).start();
    }

    private GameServerMessage actAsServerAndProduceReply(GameClientMessage msg){
        switch(msg.action()){
            case PICK_AND_PLACE_LETTER:
                playerGrid.setCharAtCell(msg.letter(), msg.cell());
                ai.placeLetter(msg.letter());
                numFilledCells ++;
                if(numFilledCells == numCells){
                    return GameServerMessage.gameFinished(new GameResult(grids));
                }else{
                    pickedLetterByAI = ai.pickAndPlaceLetter();
                    return GameServerMessage.placeLetter(pickedLetterByAI, ai.getPlayerName());
                }
            case PLACE_LETTER:
                playerGrid.setCharAtCell(pickedLetterByAI, msg.cell());
                numFilledCells ++;
                if(numFilledCells == numCells){
                    return GameServerMessage.gameFinished(new GameResult(grids));
                }else{
                    return GameServerMessage.pickAndPlaceLetter();
                }
            default:
                throw new RuntimeException("Unhandled action for msg: " + msg);
        }
    }

    public void start(){
        Debug.e("Starting offline client ...");
        GameServerMessage msg = GameServerMessage.pickAndPlaceLetter();
        Debug.d("   from mockup-server: " + msg);
        listener.handleServerMessage(msg);
    }

    @Override
    public void setMessageListener(Listener listener) {
        this.listener = listener;
    }
}
