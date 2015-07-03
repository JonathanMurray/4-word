package fourword.server;

import fourword.messages.*;
import fourword.model.Lobby;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-23.
 */
public class Server implements ServerGameBehaviour.GameListener {

    public static final int PORT = EnvironmentVars.serverPort();


    private ServerSocket serverSocket;
    private List<String> playerNames = new ArrayList<String>();
    private HashMap<String, PlayerSocket> nameSocketMap = new HashMap<String, PlayerSocket>();
    private HashMap<String, Lobby> hostLobbyMap = new HashMap<String, Lobby>();
    private HashMap<String, GameObject> hostGameMap = new HashMap<>();

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
                System.out.println("Accepted new socket: " + socket);
                printState();
                boolean isLoggedIn = false;
                ServerPlayerThread newPlayerThread = new ServerPlayerThread(this, isLoggedIn, socket);
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

    void createGameHostedBy(String hostName, int numPlayers, int numCols, int numRows){
        hostGameMap.put(hostName, new GameObject(numPlayers, hostName, numCols, numRows));
    }

    boolean joinGameHostedBy(String hostName, String joiningPlayer){
        GameObject game = hostGameMap.get(hostName);
        game.join(nameSocketMap.get(joiningPlayer));
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

    void broadcastOnlineInfo(){
        try {
            ArrayList<String> humanNames = new ArrayList<String>();
            for(PlayerSocket socket : nameSocketMap.values()){
                if(socket.isRemote()){
                    humanNames.add(socket.getName());
                }
            }
            broadcastToHumans(new MsgStringList(ServerMsg.ONLINE_PLAYERS, humanNames));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void broadcastToHumans(Msg<ServerMsg> msg) throws IOException {
        for(PlayerSocket socket : nameSocketMap.values()){
            if(socket.isRemote()){
                socket.sendMessage(msg);
            }
        }
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

    @Override
    public void gameFinished(GameObject game) {
        hostGameMap.remove(game.getHostName());
        for(PlayerSocket socket : game.playerSockets){
            if(socket.isRemote()){
                RemoteSocket remoteSocket = (RemoteSocket) socket;
                final boolean isLoggedIn = true;
                new Thread(
                        new ServerPlayerThread(this, isLoggedIn, remoteSocket)).start();
            }else{
                //Remote all data for the bot. It's job is done.
                playerNames.remove(socket.getName());
                nameSocketMap.remove(socket.getName());
            }
        }
        broadcastOnlineInfo();
        printState();
    }

    @Override
    public void gameCrashed(GameObject game) {
        try{
            for(PlayerSocket socket : game.playerSockets){
                if(socket.hasDisconnected()){
                    removePlayer(socket.getName());
                }else if(socket.isRemote()){
                    socket.sendMessage(new Msg(ServerMsg.GAME_CRASHED));
                }else{ //BOT
                    removePlayer(socket.getName());
                }
            }
            hostGameMap.remove(game.getHostName());

        }catch(IOException e){
            e.printStackTrace();
        }
        printState();
    }

    public void printState(){
        System.out.println();
//        System.out.println(" -----------------------------------------");
//        System.out.println("|             Server state:               |");
        System.out.println(" --------------------------------------------------------");
        System.out.print(getStateString());
//        System.out.println(" -----------------------------------------");
        System.out.println();
    }

    private String getStateString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Players: " + playerNames + "\n");
        if(nameSocketMap.isEmpty()){
            sb.append("Sockets: {}\n");
        }else{
            sb.append("Sockets:\n");
            appendMapToString(sb, nameSocketMap);
        }

        if(hostLobbyMap.isEmpty()){
            sb.append("Lobbies: {}\n");
        }else{
            sb.append("Lobbies:\n");
            appendMapToString(sb, hostLobbyMap);
        }

        if(hostGameMap.isEmpty()){
            sb.append("Games: {}\n");
        }else{
            sb.append("Games:\n");
            appendMapToString(sb, hostGameMap);
        }

        return sb.toString();
    }

    private <K,V> StringBuilder appendMapToString(StringBuilder sb, HashMap<K, V> map){
        for(Map.Entry e : map.entrySet()) {
            sb.append("\t" + e.getKey() + ":  " + e.getValue() + "\n");
        }
        return sb;
    }

    synchronized Lobby getLobbyOfHost(String invitedBy) {
        return hostLobbyMap.get(invitedBy);
    }

    synchronized void removePlayer(String name) throws IOException {
        playerNames.remove(name);
        nameSocketMap.remove(name);
        hostLobbyMap.remove(name);
        broadcastOnlineInfo();
    }

    synchronized public void addLobby(String name, Lobby newLobby) {
        hostLobbyMap.put(name, newLobby);
    }

    synchronized void addPlayer(String name, PlayerSocket socket) throws IOException {
        playerNames.add(name);
        nameSocketMap.put(name, socket);
        broadcastOnlineInfo();
    }

    synchronized void removeLobby(String host) {
        hostLobbyMap.remove(host);
    }

    synchronized boolean validPlayerName(String name) {
        return !playerNames.contains(name) && name.length() > 0;
    }

    synchronized PlayerSocket getSocket(String playerName) {
        return nameSocketMap.get(playerName);
    }

    synchronized boolean containsPlayer(String name) {
        return playerNames.contains(name);
    }

//    synchronized List<String> getPlayerNames(){
//        return playerNames;
//    }
}
