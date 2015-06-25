package fourword;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
    private final static int NUM_PLAYERS = 2;
    private final int numPlayers;
    private int currentPlayerIndex;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets = new ArrayList<Socket>();
    private List<ObjectOutputStream> outputStreams = new ArrayList<ObjectOutputStream>();
    private List<ObjectInputStream> inputStreams = new ArrayList<ObjectInputStream>();
    private List<GridModel> grids = new ArrayList<GridModel>();
    private int numPlacedLetters;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java AI_Server <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        MultiplayerServer server = new MultiplayerServer(NUM_PLAYERS);
        server.run(portNumber);
    }

    public MultiplayerServer(int numPlayers){
        this.numPlayers = numPlayers;
    }

    public void run(int portNumber){
        printTitle();
        try{
            serverSocket = createServerSocket(portNumber);
            acceptClients();
            sendToPlayer(currentPlayerIndex, GameServerMessage.pickAndPlaceLetter());
            runProtocolLoop();
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            close(serverSocket);
            for(Socket socket : clientSockets){
                close(socket);
            }
            for(ObjectInputStream s : inputStreams){
                close(s);
            }
            for(ObjectOutputStream s : outputStreams){
                close(s);
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

    private void acceptClients() throws IOException {
        for(int i = 0; i < numPlayers; i++){
            grids.add(new GridModel(NUM_COLS, NUM_ROWS));
            System.out.println("Waiting for client_" + i + " ...");
            Socket clientSocket = serverSocket.accept();
            clientSockets.add(clientSocket);
            outputStreams.add(new ObjectOutputStream(clientSocket.getOutputStream()));
            inputStreams.add(new ObjectInputStream(clientSocket.getInputStream()));
            System.out.println("Accepted client_" + i + ": " + clientSocket.getInetAddress());
        }
    }

    private void runProtocolLoop() throws IOException, ClassNotFoundException {
        boolean running = true;
        while(running){
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
                sendToPlayer(currentPlayerIndex, GameServerMessage.pickAndPlaceLetter());
            }
        }
        System.out.println("Game is over!");
    }

    private void printAllGrids(){
        System.out.println("Printing all grids:\n");
        for(GridModel grid : grids){
            System.out.println(grid + "\n");
        }
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
        sendMessage(outputStreams.get(playerIndex), msg);
        System.out.println("    Sent message to PLAYER_" + playerIndex + ": " + msg);
    }

    private GameClientMessage receiveFromPlayer(int playerIndex){
        System.out.println("Waiting for message from PLAYER_" + playerIndex + " ... ");
        GameClientMessage msg = receiveMessage(inputStreams.get(playerIndex));
        System.out.println("    Received message from PLAYER_" + playerIndex + ": " + msg);
        return msg;
    }

    private GameClientMessage receiveMessage(ObjectInputStream in){
        try {
            return (GameClientMessage) in.readObject();
        } catch (ClassNotFoundException|IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ObjectOutputStream out, GameServerMessage msg) throws IOException {
        out.writeObject(msg);
    }

    private void printTitle() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("----------- Multiplayer_Server -----------");
        System.out.println("------------------------------------------");
        System.out.println();
    }
}
