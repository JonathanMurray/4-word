package fourword.server;

import fourword.model.*;
import fourword.messages.*;
import fourword.model.Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jonathan on 2015-06-26.
 */
public class ServerGameBehaviour implements Runnable{
    private static final String HOME_DIR = EnvironmentVars.homeDir();

    private static final File[] WORDLIST_FILES = new File[]{
            new File(HOME_DIR, "swedish-word-list-2-letters"),
            new File(HOME_DIR, "swedish-word-list-3-letters"),
            new File(HOME_DIR, "swedish-word-list-4-letters"),
            new File(HOME_DIR, "swedish-word-list-5-letters")};

    private final Dictionary dictionary = Dictionary.fromFiles(WORDLIST_FILES);
    private final ScoreCalculator scoreCalculator = new ScoreCalculator(dictionary);
    private final GameObject game;
    private final int numPlayers;
    private int currentPlayerIndex;
    private final List<PlayerSocket> sockets;
    private final List<GridModel> grids;
    private int numPlacedLetters;
    private final int numCols;
    private final int numRows;
    private final int numCells;
    private final GameListener listener;

    public ServerGameBehaviour(GameListener listener, GameObject game){
        this.sockets = game.playerSockets;
        this.grids = game.grids;
        numCols = grids.get(0).getNumCols();
        numRows = grids.get(0).getNumRows();
        numCells = numCols * numRows;
        System.out.println("new ServerGameBehaviour, cols: " + numCols + ", rows: " + numRows);
        numPlayers = sockets.size();
        this.game = game;
        this.listener = listener;
    }

    @Override
    public void run(){
        try{
//            broadcast(new MsgGameIsStarting(numCols, numRows)); //already sent in other thread
            boolean running = true;
            while(running){
                PlayerSocket currentPlayer = sockets.get(currentPlayerIndex);

                //Sleep before the next turn, for better user experience
                sleep(500);

                broadcast(new MsgText(ServerMsg.GAME_PLAYERS_TURN, currentPlayer.getName()));
                sendToPlayer(currentPlayer, new Msg(ServerMsg.DO_PICK_AND_PLACE_LETTER));
                broadcast(new MsgText(ServerMsg.WAITING_FOR_PLAYER_MOVE, currentPlayer.getName()), currentPlayerIndex);
                MsgPickAndPlaceLetter pickAndPlaceMsg = (MsgPickAndPlaceLetter) receiveFromPlayer(currentPlayer);
                broadcast(new MsgText(ServerMsg.GAME_PLAYER_DONE_THINKING, currentPlayer.getName()));
                final char letterPickedByCurrentPlayer = pickAndPlaceMsg.letter;
                final Cell cellPickedByCurrentPlayer = pickAndPlaceMsg.cell;
                grids.get(currentPlayerIndex).setCharAtCell(letterPickedByCurrentPlayer, cellPickedByCurrentPlayer);
                broadcast(new MsgRequestPlaceLetter(letterPickedByCurrentPlayer, currentPlayer.getName()), currentPlayerIndex);
                handleAllPlaceReplies(letterPickedByCurrentPlayer);
                numPlacedLetters ++;

//                printAllGrids();

                boolean isGameFinished = numPlacedLetters == numCells;
                if(isGameFinished){
                    broadcastResults();
                    running = false;
                }else{
                    currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
                    System.out.println("Current player index: " + currentPlayerIndex);
                }
            }
            listener.gameFinished(game);
            System.out.println("Game is over!");
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
            gameCrashed();
        }

    }

    private void gameCrashed(){
        System.out.println("Some player disconnected. Aborting game.");
        listener.gameCrashed(game);
    }

    private void broadcastResults() throws IOException {
        HashSet<String> lowerWords = new HashSet<>();
        HashMap<String, GridModel> gridMap = new HashMap<String, GridModel>();
        for(int i = 0; i < numPlayers; i++){
            String playerName = sockets.get(i).getName();
            GridModel grid = grids.get(i);
            gridMap.put(playerName, grid);
            for(String row : grid.getRows()){
                List<String> words = scoreCalculator.extractLowerWords(row);
                lowerWords.addAll(words);
            }
            for(String col : grid.getCols()){
                List<String> words = scoreCalculator.extractLowerWords(col);
                lowerWords.addAll(words);
            }
        }
        GameResult result = new GameResult(gridMap, lowerWords);
        broadcast(new MsgGameFinished(result));
    }

    private void handleAllPlaceReplies(final char pickedLetter){
        final AtomicInteger numPlayersHavePlaced = new AtomicInteger(0);
        for(int i = 0; i < numPlayers; i++){
            if(i != currentPlayerIndex){ //One player already placed the letter (pick and place at the same time)
                final int playerIndex = i;
                new Thread(new Runnable() {
                    public void run() {
                        MsgPlaceLetter placeLetterMsg = null;
                        try {
                            placeLetterMsg = (MsgPlaceLetter) receiveFromPlayer(sockets.get(playerIndex));
                            String senderName = sockets.get(playerIndex).getName();
                            broadcast(new MsgText(ServerMsg.GAME_PLAYER_DONE_THINKING, senderName), playerIndex);
                        } catch (IOException|ClassNotFoundException e) {
                            e.printStackTrace();
                            gameCrashed();
                        }
                        synchronized (grids){
                            System.out.println(grids);
                            System.out.println(playerIndex);
                            GridModel grid = grids.get(playerIndex);
                            System.out.println(grid);
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
    }

    private Msg<ClientMsg> receiveFromPlayer(PlayerSocket socket) throws IOException, ClassNotFoundException {
        Msg<ClientMsg> msg = socket.receiveMessage();
        return msg;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public interface GameListener {
        void gameFinished(GameObject game);
        void gameCrashed(GameObject game);
    }



}
