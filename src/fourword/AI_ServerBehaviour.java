package fourword;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-24.
 */
public class AI_ServerBehaviour {

//    //private AI ai;
//    private List<AI> AIs;
//    private int currentAI_index;
//    private Client.Listener listener;
//    private final int numCols;
//    private final int numRows;
//    private final int numCells;
//    private int numFilledCells;
//    private HashMap<String, GridModel> grids;
//    private GridModel playerGrid;
//    private char pickedLetterByAI;
//    private final static String PLAYER_NAME = "PLAYER";
//
//    public AI_ServerBehaviour(List<AI> AIs, int numCols, int numRows){
//        this.AIs = AIs;
//        this.numCols = numCols;
//        this.numRows = numRows;
//        numCells = numRows * numCols;
//        numFilledCells = 0;
//        grids = new HashMap<String, GridModel>();
//        for(int i = 0; i < AIs.size(); i++){
//            AI ai = AIs.get(i);
//            GridModel aiGrid = new GridModel(numCols, numRows);
//            ai.initialize(aiGrid);
//            String name = "AI_" + i;
//            grids.put(name, aiGrid);
//        }
//        playerGrid = new GridModel(numCols, numRows);
//        grids.put(PLAYER_NAME, playerGrid);
//        currentAI_index = 0;
//    }
//
//
//    public ServerMsg act(ClientMsg msgFromClient){
//        switch(msgFromClient.action()){
//
//            case PICK_AND_PLACE_LETTER:
//                playerGrid.setCharAtCell(msgFromClient.letter(), msgFromClient.cell());
//                for(AI ai : AIs){
//                    ai.placeLetter(msgFromClient.letter());
//                }
//                numFilledCells ++;
//                if(numFilledCells == numCells){
//                    return ServerMsg.gameFinished(new GameResult(grids));
//                }else{
//                    return aiPickAndPlaceAndOthersPlace(currentAI_index);
//                }
//
//            case PLACE_LETTER:
//                playerGrid.setCharAtCell(pickedLetterByAI, msgFromClient.cell());
//                numFilledCells ++;
//                if(numFilledCells == numCells){
//                    return ServerMsg.gameFinished(new GameResult(grids));
//                }else{
//                    currentAI_index = (currentAI_index + 1) % AIs.size();
//                    if(currentAI_index > 0){
//                        return aiPickAndPlaceAndOthersPlace(currentAI_index);
//                    }else{
//                        return ServerMsg.pickAndPlaceLetter();
//                    }
//                }
//        }
//
//        throw new RuntimeException("Unhandled action for msg: " + msgFromClient);
//    }
//
//    private ServerMsg aiPickAndPlaceAndOthersPlace(int currentAI_index){
//        AI currentAI = AIs.get(currentAI_index);
//        pickedLetterByAI = currentAI.pickAndPlaceLetter();
//        for(int i = 0; i < AIs.size(); i++){
//            if(i != currentAI_index){
//                AIs.get(i).placeLetter(pickedLetterByAI);
//            }
//        }
//        String name = "AI_" + currentAI_index;
//        return ServerMsg.placeLetter(pickedLetterByAI, name);
//    }
//
//    public String toString(){
//        StringBuilder sb = new StringBuilder();
//        for(Map.Entry<String, GridModel> e : grids.entrySet()){
//            sb.append(e.getKey() + ":\n");
//            sb.append(e.getValue() + "\n\n");
//        }
//        return sb.toString();
//    }

}
