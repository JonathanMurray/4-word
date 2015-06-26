package fourword.protocol;

import fourword.GridModel;
import fourword.messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-23.
 */
public class Server {

    private final static int NUM_COLS = 2;
    private final static int NUM_ROWS = 2;
    private final static int NUM_HUMANS = 2;
    private final static int NUM_BOTS = 2;
    private final int numHumans;
    private final int numBots;
    private ServerSocket serverSocket;
    private List<PlayerSocket> sockets = new ArrayList<PlayerSocket>();
    private List<GridModel> grids = new ArrayList<GridModel>();

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java AI_Server <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        Server server = new Server(NUM_HUMANS, NUM_BOTS);
        server.run(portNumber);
    }

    public Server(int numHumans, int numBots){
        this.numHumans = numHumans;
        this.numBots = numBots;
    }

    public void run(int portNumber){
        printTitle();
        try{
            serverSocket = createServerSocket(portNumber);
            setupGame();
            new ServerBehaviour(sockets, grids).runProtocolLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
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

        List<MsgLobbyWaitingForMore.LobbyPlayer> lobbyPlayers = new ArrayList<MsgLobbyWaitingForMore.LobbyPlayer>();

        for(int i = 0; i < numBots; i++){
            AI ai = new AI();
            GridModel aiGrid = new GridModel(NUM_COLS, NUM_ROWS);
            ai.initialize(aiGrid);
            grids.add(aiGrid);
            PlayerSocket socket = new BotSocket(ai, i);
            sockets.add(socket);
            System.out.println("Accepted BotClient_" + i);
            lobbyPlayers.add(new MsgLobbyWaitingForMore.LobbyPlayer("Bot_" + i, false, true));
        }

        for(int i = 0; i < numHumans; i++){
            lobbyPlayers.add(new MsgLobbyWaitingForMore.LobbyPlayer("Human_" + i, true, false));
        }

        System.out.println("Will now wait for " + numHumans + " humans to connect.");

        for(int i = 0; i < numHumans; i++){
            grids.add(new GridModel(NUM_COLS, NUM_ROWS));
            System.out.println("Waiting for HumanClient_" + i + " ...");
            PlayerSocket socket = RemoteSocket.acceptSocket(serverSocket, i);
            sockets.add(socket);
            System.out.println("Accepted HumanClient_" + i + ": " + socket.getInetAddress());
            lobbyPlayers.get(numBots + i).hasConnected = true;
            if(i < numHumans - 1){
                broadcast(new MsgLobbyWaitingForMore(lobbyPlayers));
            }

        }
    }


    /* --------------------------------------
                HELPER-METHODS BELOW
      ---------------------------------------  */

    private void broadcast(ServerMsg msg) throws IOException {
        for(PlayerSocket socket : sockets){
            sendToPlayer(socket, msg);
        }
    }

    private void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToPlayer(PlayerSocket socket, ServerMsg msg) throws IOException {
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
}
