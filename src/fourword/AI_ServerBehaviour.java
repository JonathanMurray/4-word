package fourword;

import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-24.
 */
public class AI_ServerBehaviour {

    private AI ai;
    private Client.Listener listener;
    private final int numCols;
    private final int numRows;
    private final int numCells;
    private int numFilledCells;
    private HashMap<String, GridModel> grids;
    private GridModel playerGrid;
    private char pickedLetterByAI;
    private final static String PLAYER_NAME = "PLAYER";

    public AI_ServerBehaviour(AI ai, int numCols, int numRows){
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


    public GameServerMessage act(GameClientMessage msgFromClient){
        switch(msgFromClient.action()){
            case PICK_AND_PLACE_LETTER:
                playerGrid.setCharAtCell(msgFromClient.letter(), msgFromClient.cell());
                ai.placeLetter(msgFromClient.letter());
                numFilledCells ++;
                if(numFilledCells == numCells){
                    return GameServerMessage.gameFinished(new GameResult(grids));
                }else{
                    pickedLetterByAI = ai.pickAndPlaceLetter();
                    return GameServerMessage.placeLetter(pickedLetterByAI, ai.getPlayerName());
                }
            case PLACE_LETTER:
                playerGrid.setCharAtCell(pickedLetterByAI, msgFromClient.cell());
                numFilledCells ++;
                if(numFilledCells == numCells){
                    return GameServerMessage.gameFinished(new GameResult(grids));
                }else{
                    return GameServerMessage.pickAndPlaceLetter();
                }
            default:
                throw new RuntimeException("Unhandled action for msg: " + msgFromClient);
        }
    }

    public AI ai(){
        return ai;
    }
}
