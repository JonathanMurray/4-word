package fourword.protocol;

import fourword.messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-23.
 */
public class Server implements ServerGameBehaviour.GameFinishedListener {

    public static final int PORT = 4444;
    public static final String IP_ADDRESS = "192.168.1.2";

    private List<String> playerNames = new ArrayList<String>();
//    private List<LobbyPlayer> lobbyState = new ArrayList<LobbyPlayer>();
//    private List<GameObject> games = new ArrayList<GameObject>();
    private HashMap<String, Lobby> hostLobbyMap = new HashMap<String, Lobby>();

    private int freeBotIndex = 1;

//    private final static int NUM_COLS = 2;
//    private final static int NUM_ROWS = 2;
//    private final static int NUM_HUMANS = 2;
//    private final static int NUM_BOTS = 2;
//    private final int numHumans;
//    private final int numBots;
    private ServerSocket serverSocket;
//    private List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
    private HashMap<String, PlayerSocket> nameSocketMap = new HashMap<String, PlayerSocket>();
//    private List<GridModel> grids = new ArrayList<GridModel>();

//    private List<ServerPlayerThread> playerThreads = new ArrayList<>();

    private HashMap<String, GameObject> hostedGames = new HashMap<>();

    public static void main(String[] args) throws IOException {
//        if (args.length != 1) {
//            System.err.println("Usage: java AI_Server <port number>");
//            System.exit(1);
//        }
//        int portNumber = Integer.parseInt(args[0]);
        int portNumber = PORT;
//        Server server = new Server(NUM_HUMANS, NUM_BOTS);
        Server server = new Server();
        server.run(portNumber);
    }

//    public Server(int numHumans, int numBots){
//        this.numHumans = numHumans;
//        this.numBots = numBots;
//    }

    public void run(int portNumber){
        printTitle();
        try{
            serverSocket = createServerSocket(portNumber);

            while(true){
                final RemoteSocket socket = RemoteSocket.acceptSocket(serverSocket, 0);
                System.out.println("Accepted new playerSocket : " + socket);
                ServerPlayerThread newPlayerThread = new ServerPlayerThread(this, socket, playerNames, hostLobbyMap, nameSocketMap);
//                playerThreads.add(newPlayerThread);
                new Thread(newPlayerThread).start();
            }

//            setupGame();
//            new ServerGameBehaviour(sockets, grids).runGameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            close(serverSocket);
//            for(PlayerSocket socket : sockets){
//                socket.close();
//            }
        }
    }

    void startGameHostedBy(String hostName, int numPlayers, int numCols, int numRows){
        hostedGames.put(hostName, new GameObject(numPlayers, hostName, numCols, numRows));
    }

    boolean joinGameHostedBy(String hostName, PlayerSocket joiningSocket){
        GameObject game = hostedGames.get(hostName);
        game.join(joiningSocket);
        if(game.isReadyToStart()){
            ServerGameBehaviour gameThread = new ServerGameBehaviour(this, game);
            new Thread(gameThread).start();
            return true;
        }
        return false;
    }

    String generateBotName(){
        boolean alreadyTaken = true;
        String name = "";
        while(alreadyTaken){
            name = "BOT_" + freeBotIndex;
            alreadyTaken = playerNames.contains(name);
            freeBotIndex ++;
        }
        return name;
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

//    private void setupGame() throws IOException {
//
//        List<LobbyPlayer> lobbyPlayers = new ArrayList<LobbyPlayer>();
//
//        for(int i = 0; i < numBots; i++){
//            AI ai = new AI();
//            GridModel aiGrid = new GridModel(NUM_COLS, NUM_ROWS);
//            ai.initialize(aiGrid);
//            grids.add(aiGrid);
//            PlayerSocket socket = new BotSocket(ai, i);
//            sockets.add(socket);
//            System.out.println("Accepted BotClient_" + i);
//            lobbyPlayers.add(new LobbyPlayer("Bot_" + i, false, true));
//        }
//
//        for(int i = 0; i < numHumans; i++){
//            lobbyPlayers.add(new LobbyPlayer("Human_" + i, true, false));
//        }
//
//        System.out.println("Will now wait for " + numHumans + " humans to connect.");
//
//        for(int i = 0; i < numHumans; i++){
//            grids.add(new GridModel(NUM_COLS, NUM_ROWS));
//            System.out.println("Waiting for HumanClient_" + i + " ...");
//            PlayerSocket socket = RemoteSocket.acceptSocket(serverSocket, i);
//            sockets.add(socket);
//            System.out.println("Accepted HumanClient_" + i + ": " + socket.getInetAddress());
//            lobbyPlayers.get(numBots + i).hasConnected = true;
//            if(i < numHumans - 1){
//                broadcast(new MsgLobbyState(lobbyPlayers));
//            }
//
//        }
//    }


    /* --------------------------------------
                HELPER-METHODS BELOW
      ---------------------------------------  */

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void broadcast(Msg msg) throws IOException {
//        for(PlayerSocket socket : sockets){
//            sendToPlayer(socket, msg);
//        }
//    }

    private void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToPlayer(PlayerSocket socket, Msg msg) throws IOException {
        socket.sendMessage(msg);
        System.out.println("    Sent message to " + socket.getName() + ": " + msg);
    }

    private void printTitle() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("----------- Multiplayer_Server -----------");
        System.out.println("------------------------------------------");
        System.out.println();
    }

    @Override
    public void gameFinished(GameObject game) {
        for(PlayerSocket socket : game.playerSockets){
            if(socket.isRemote()){
                RemoteSocket remoteSocket = (RemoteSocket) socket;
                new Thread(
                        new ServerPlayerThread(this, remoteSocket, playerNames, hostLobbyMap, nameSocketMap)).start();
            }

        }
    }
}
