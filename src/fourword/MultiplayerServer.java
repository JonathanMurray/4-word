package fourword;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jonathan on 2015-06-23.
 */
public class MultiplayerServer {

    private final static int NUM_COLS = 2;
    private final static int NUM_ROWS = 2;
    private final static int NUM_CELLS = NUM_COLS * NUM_ROWS;
    private final static int NUM_HUMANS = 1;
    private final static int NUM_BOTS = 2;
    private final int numHumans;
    private final int numBots;
    private final int numPlayers;
    private int currentPlayerIndex;
    private ServerSocket serverSocket;
    private List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
    private List<GridModel> grids = new ArrayList<GridModel>();
    private int numPlacedLetters;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java AI_Server <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        MultiplayerServer server = new MultiplayerServer(NUM_HUMANS, NUM_BOTS);
        server.run(portNumber);
    }

    public MultiplayerServer(int numHumans, int numBots){
        this.numHumans = numHumans;
        this.numBots = numBots;
        this.numPlayers = numHumans + numBots;
    }

    public void run(int portNumber){
        printTitle();
        try{
            serverSocket = createServerSocket(portNumber);
            setupGame();
            runProtocolLoop();
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            close(serverSocket);
            for(PlayerSocket socket : sockets){
                socket.close();
            }
        }
    }

    private ServerSocket createServerSocket(int portNumber) throws IOException {
        System.out.println("Creating serverSocket on port " + portNumber);
        serverSocket = new ServerSocket(portNumber);
        System.out.println("Listening on port " + portNumber);
        System.out.println("InetAddress: " + serverSocket.getInetAddress());
        System.out.println("LocalPort: " + serverSocket.getLocalPort());
        System.out.println("LocalSocketAddress: " + serverSocket.getLocalSocketAddress());
        return serverSocket;
    }

    private void setupGame() throws IOException {

        List<GameServerMessage.LobbyPlayer> lobbyPlayers = new ArrayList<GameServerMessage.LobbyPlayer>();

        for(int i = 0; i < numBots; i++){
            MessageAI ai = new MessageAI();
            GridModel aiGrid = new GridModel(NUM_COLS, NUM_ROWS);
            ai.initialize(aiGrid);
            grids.add(aiGrid);
            PlayerSocket socket = new BotSocket(ai);
            sockets.add(socket);
            System.out.println("Accepted BotClient_" + i);
            lobbyPlayers.add(new GameServerMessage.LobbyPlayer("Bot_" + i, false, true));
        }

        for(int i = 0; i < numHumans; i++){
            lobbyPlayers.add(new GameServerMessage.LobbyPlayer("Human_" + i, true, false));
        }

        System.out.println("Will now wait for " + numHumans + " humans to connect.");

        for(int i = 0; i < numHumans; i++){
            grids.add(new GridModel(NUM_COLS, NUM_ROWS));
            System.out.println("Waiting for HumanClient_" + i + " ...");
            PlayerSocket socket = HumanSocket.acceptSocket(serverSocket);
            sockets.add(socket);
            lobbyPlayers.get(numBots + i).hasConnected = true;
            if(i < numHumans - 1){
                broadcast(GameServerMessage.waitingForMorePlayers(lobbyPlayers));
            }
            System.out.println("Accepted HumanClient_" + i + ": " + socket.getInetAddress());
        }

        broadcast(GameServerMessage.gameIsStarting());
    }

    private void runProtocolLoop() throws IOException, ClassNotFoundException {
        boolean running = true;
        while(running){
            sendToPlayer(currentPlayerIndex, GameServerMessage.pickAndPlaceLetter());
            GameClientMessage pickAndPlaceMsg = receiveFromPlayer(currentPlayerIndex);
            final char letterPickedByCurrentPlayer = pickAndPlaceMsg.letter();
            final Cell cellPickedByCurrentPlayer = pickAndPlaceMsg.cell();
            grids.get(currentPlayerIndex).setCharAtCell(letterPickedByCurrentPlayer, cellPickedByCurrentPlayer);

            String pickingPlayerName = "PLAYER_" + currentPlayerIndex;

            broadcast(GameServerMessage.placeLetter(letterPickedByCurrentPlayer, pickingPlayerName), currentPlayerIndex);
            handleAllPlaceReplies(letterPickedByCurrentPlayer);
            numPlacedLetters ++;

            printAllGrids();

            boolean isGameFinished = numPlacedLetters == NUM_CELLS;
            if(isGameFinished){
                broadcastResults();
                running = false;
            }else{
                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
            }
        }
        System.out.println("Game is over!");
    }

    private void broadcastResults() throws IOException {
        HashMap<String, GridModel> gridMap = new HashMap<String, GridModel>();
        for(int i = 0; i < numPlayers; i++){
            gridMap.put("PLAYER_" + i, grids.get(i));
        }
        GameResult result = new GameResult(gridMap);
        broadcast(GameServerMessage.gameFinished(result));
    }

    private void handleAllPlaceReplies(final char pickedLetter){
        final AtomicInteger numPlayersHavePlaced = new AtomicInteger(0);
        System.out.println("Waiting for PLACE-replies ...");
        for(int i = 0; i < numPlayers; i++){
            if(i != currentPlayerIndex){ //One player already placed the letter (pick and place at the same time)
                final int playerIndex = i;
                new Thread(new Runnable() {
                    public void run() {
                        GameClientMessage placeLetterMsg = receiveFromPlayer(playerIndex);
                        synchronized (grids){
                            GridModel grid = grids.get(playerIndex);
                            grid.setCharAtCell(pickedLetter, placeLetterMsg.cell());
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

    private void broadcast(GameServerMessage msg) throws IOException {
        for(int i = 0; i < numPlayers; i++){
            sendToPlayer(i, msg);
        }
    }

    private void broadcast(GameServerMessage msg, int exceptPlayerWithIndex) throws IOException {
        for(int i = 0; i < numPlayers; i++){
            if(i != exceptPlayerWithIndex){
                sendToPlayer(i, msg);
            }
        }
    }

    private void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToPlayer(int playerIndex, GameServerMessage msg) throws IOException {
        sockets.get(playerIndex).sendMessage(msg);
        System.out.println("    Sent message to PLAYER_" + playerIndex + ": " + msg);
    }

    private GameClientMessage receiveFromPlayer(int playerIndex){
        System.out.println("Waiting for message from PLAYER_" + playerIndex + " ... ");
        GameClientMessage msg = sockets.get(playerIndex).receiveMessage();
        System.out.println("    Received message from PLAYER_" + playerIndex + ": " + msg);
        return msg;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printTitle() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("----------- Multiplayer_Server -----------");
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private interface PlayerSocket{
        void sendMessage(GameServerMessage msg);
        GameClientMessage receiveMessage();
        void close();
        InetAddress getInetAddress();
    }

    private class BotSocket implements PlayerSocket{

        private MessageAI ai;
        private GameClientMessage replyFromAI;

        BotSocket(MessageAI ai){
            this.ai = ai;
        }

        @Override
        public void sendMessage(GameServerMessage msg) {
            replyFromAI = ai.handleServerMessageAndProduceReply(msg);
        }

        @Override
        public GameClientMessage receiveMessage() {
            if(replyFromAI == null){
                throw new RuntimeException();
            }
            sleep(500);
            GameClientMessage msg = replyFromAI;
            replyFromAI = null;
            return msg;
        }

        @Override
        public void close(){
            //Do nothing
        }

        @Override
        public InetAddress getInetAddress() {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class HumanSocket implements PlayerSocket{

        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Socket clientSocket;

        static HumanSocket acceptSocket(ServerSocket serverSocket){
            try {
                HumanSocket socket = new HumanSocket();
                socket.clientSocket = serverSocket.accept();
                socket.out = new ObjectOutputStream(socket.clientSocket.getOutputStream());
                socket.in = new ObjectInputStream(socket.clientSocket.getInputStream());

                return socket;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void sendMessage(GameServerMessage msg) {
            try {
                out.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public GameClientMessage receiveMessage() {
            try {
                return (GameClientMessage) in.readObject();
            } catch (ClassNotFoundException|IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public InetAddress getInetAddress() {
            return clientSocket.getInetAddress();
        }
    }
}
