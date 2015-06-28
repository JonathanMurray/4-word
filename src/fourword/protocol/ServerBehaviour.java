package fourword.protocol;

import fourword.model.Cell;
import fourword.model.GameResult;
import fourword.model.GridModel;
import fourword.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jonathan on 2015-06-26.
 */
public class ServerBehaviour {
    private final int numPlayers;
    private int currentPlayerIndex;
    private final List<PlayerSocket> sockets;
    private final List<GridModel> grids;
    private int numPlacedLetters;
    private final int numCols;
    private final int numRows;
    private final int numCells;

    public ServerBehaviour(List<PlayerSocket> sockets, List<GridModel> grids){
        this.sockets = sockets;
        this.grids = grids;
        numCols = grids.get(0).getNumCols();
        numRows = grids.get(0).getNumRows();
        numCells = numCols * numRows;
        numPlayers = sockets.size();
    }

    public void runGameLoop(){
        try{
            broadcast(new MsgGameIsStarting(numCols, numRows));
            boolean running = true;
            while(running){
                PlayerSocket currentPlayer = sockets.get(currentPlayerIndex);
                sendToPlayer(currentPlayer, new Msg(ServerMsg.PICK_AND_PLACE_LETTER));
                broadcast(new MsgText(ServerMsg.WAITING_FOR_PLAYER_MOVE, currentPlayer.getName()), currentPlayerIndex);
                MsgPickAndPlaceLetter pickAndPlaceMsg = (MsgPickAndPlaceLetter) receiveFromPlayer(currentPlayer);
                final char letterPickedByCurrentPlayer = pickAndPlaceMsg.letter;
                final Cell cellPickedByCurrentPlayer = pickAndPlaceMsg.cell;
                grids.get(currentPlayerIndex).setCharAtCell(letterPickedByCurrentPlayer, cellPickedByCurrentPlayer);

                broadcast(new MsgRequestPlaceLetter(letterPickedByCurrentPlayer, currentPlayer.getName()), currentPlayerIndex);
                handleAllPlaceReplies(letterPickedByCurrentPlayer);
                numPlacedLetters ++;

                printAllGrids();

                boolean isGameFinished = numPlacedLetters == numCells;
                if(isGameFinished){
                    broadcastResults();
                    running = false;
                }else{
                    currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
                }
            }
            System.out.println("Game is over!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcastResults() throws IOException {
        HashMap<String, GridModel> gridMap = new HashMap<String, GridModel>();
        for(int i = 0; i < numPlayers; i++){
            gridMap.put("PLAYER_" + i, grids.get(i));
        }
        GameResult result = new GameResult(gridMap);
        broadcast(new MsgGameFinished(result));
    }

    private void handleAllPlaceReplies(final char pickedLetter){
        final AtomicInteger numPlayersHavePlaced = new AtomicInteger(0);
        System.out.println("Waiting for PLACE-replies ...");
        for(int i = 0; i < numPlayers; i++){
            if(i != currentPlayerIndex){ //One player already placed the letter (pick and place at the same time)
                final int playerIndex = i;
                new Thread(new Runnable() {
                    public void run() {
                        MsgPlaceLetter placeLetterMsg = null;
                        try {
                            placeLetterMsg = (MsgPlaceLetter) receiveFromPlayer(sockets.get(playerIndex));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        synchronized (grids){
                            GridModel grid = grids.get(playerIndex);
                            grid.setCharAtCell(pickedLetter, placeLetterMsg.cell);
                            numPlayersHavePlaced.incrementAndGet();
                        }
                    }
                }).start();
            }
        }

        boolean waitingForReplies = true;
        while(waitingForReplies){
            sleep(100);
            synchronized (grids){
                waitingForReplies = numPlayersHavePlaced.get() < numPlayers - 1;
            }
        }
    }






    /* --------------------------------------
                HELPER-METHODS BELOW
      ---------------------------------------  */

    private void printAllGrids(){
        System.out.println("Printing all grids:\n");
        for(GridModel grid : grids){
            System.out.println(grid + "\n");
        }
    }

    private void broadcast(Msg<ServerMsg> msg) throws IOException {
        for(PlayerSocket socket : sockets){
            sendToPlayer(socket, msg);
        }
    }

    private void broadcast(Msg<ServerMsg> msg, int exceptPlayerWithIndex) throws IOException {
        for(int i = 0; i < sockets.size(); i++){
            if(i != exceptPlayerWithIndex){
                sendToPlayer(sockets.get(i), msg);
            }
        }
    }

    private void sendToPlayer(PlayerSocket socket, Msg<ServerMsg> msg) throws IOException {
        socket.sendMessage(msg);
        System.out.println("    Sent message to " + socket.getName() + ": " + msg);
    }

    private Msg<ClientMsg> receiveFromPlayer(PlayerSocket socket) throws IOException {
        System.out.println("Waiting for message from " + socket.getName() + " ... ");
        Msg<ClientMsg> msg = socket.receiveMessage();
        System.out.println("    Received message from " + socket.getName() + ": " + msg);
        return msg;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }






}
