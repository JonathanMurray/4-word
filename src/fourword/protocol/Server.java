package fourword.protocol;

import fourword.messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-23.
 */
public class Server implements ServerGameBehaviour.GameFinishedListener {

    public static final int PORT = 4444;
    public static final String IP_ADDRESS = "192.168.1.2";

    private ServerSocket serverSocket;
    private List<String> playerNames = new ArrayList<String>();
    private HashMap<String, PlayerSocket> nameSocketMap = new HashMap<String, PlayerSocket>();
    private HashMap<String, Lobby> hostLobbyMap = new HashMap<String, Lobby>();
    private HashMap<String, GameObject> hostGameMap = new HashMap<>();
    private int freeBotIndex = 1;

    public static void main(String[] args) throws IOException {
//        if (args.length != 1) {
//            System.err.println("Usage: java AI_Server <port number>");
//            System.exit(1);
//        }
//        int portNumber = Integer.parseInt(args[0]);
        int portNumber = PORT;
        Server server = new Server();
        server.run(portNumber);
    }

    public void run(int portNumber){
        printTitle();
        try{
            serverSocket = createServerSocket(portNumber);
            printState();

            while(true){
                final RemoteSocket socket = RemoteSocket.acceptSocket(serverSocket, 0);
                System.out.println("Accepted new playerSocket : " + socket);
                printState();
                boolean isLoggedIn = false;
                ServerPlayerThread newPlayerThread = new ServerPlayerThread(this, isLoggedIn, socket, playerNames, hostLobbyMap, nameSocketMap);
//                playerThreads.add(newPlayerThread);
                new Thread(newPlayerThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void startGameHostedBy(String hostName, int numPlayers, int numCols, int numRows){
        hostGameMap.put(hostName, new GameObject(numPlayers, hostName, numCols, numRows));
    }

    boolean joinGameHostedBy(String hostName, PlayerSocket joiningSocket){
        GameObject game = hostGameMap.get(hostName);
        game.join(joiningSocket);
        if(game.isReadyToStart()){
            ServerGameBehaviour gameThread = new ServerGameBehaviour(this, game);
            new Thread(gameThread).start();
            return true;
        }
        return false;
    }

    String generateBotName(){
        String name = "";
        final int MAX_BOTS = 10000; //TODO
        for(int i = 1; i < MAX_BOTS; i++){
            name = "BOT_" + i;
            if(!playerNames.contains(name)){
                return name;
            }
        }
        printState();
        throw new RuntimeException();
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
        hostGameMap.remove(game.getHostName());
        for(PlayerSocket socket : game.playerSockets){
            if(socket.isRemote()){
                RemoteSocket remoteSocket = (RemoteSocket) socket;
                final boolean isLoggedIn = true;
                new Thread(
                        new ServerPlayerThread(this, isLoggedIn, remoteSocket, playerNames, hostLobbyMap, nameSocketMap)).start();
            }else{
                //Remote all data for the bot. It's job is done.
                playerNames.remove(socket.getName());
                nameSocketMap.remove(socket.getName());
            }
        }
        printState();
    }

    public void printState(){
        System.out.println();
        System.out.println(" -----------------------------------------");
        System.out.println("|             Server state:               |");
        System.out.println(" -----------------------------------------");
        System.out.println();
        System.out.println(getStateString());
        System.out.println(" -----------------------------------------");
        System.out.println("\n");
    }

    private String getStateString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Player names: " + playerNames + "\n\n");
        sb.append("nameSocketMap:\n");
        appendMapToString(sb, nameSocketMap).append("\n");
        sb.append("hostLobbyMap:\n");
        appendMapToString(sb, hostLobbyMap).append("\n");
        sb.append("hostGameMap:\n");
        appendMapToString(sb, hostGameMap).append("\n");
        return sb.toString();
    }

    private <K,V> StringBuilder appendMapToString(StringBuilder sb, HashMap<K, V> map){
        for(Map.Entry e : map.entrySet()) {
            sb.append(e.getKey() + ":  " + e.getValue() + "\n");
        }
        return sb;
    }
}
