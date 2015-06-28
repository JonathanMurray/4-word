package fourword.protocol;

import fourword.messages.*;
import fourword.model.LobbyPlayer;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class ServerPlayerThread implements Runnable {
    private final RemoteSocket socket;
    private Lobby lobby;


    private final List<String> players;
    private final HashMap<String, Lobby> lobbies;
    private final HashMap<String, RemoteSocket> nameSocketMap;

    ServerPlayerThread(RemoteSocket socket, List<String> players, HashMap<String, Lobby> lobbies, HashMap<String, RemoteSocket> nameSocketMap) {
        this.socket = socket;
        this.players = players;
        this.lobbies = lobbies;
        this.nameSocketMap = nameSocketMap;
    }

    @Override
    public void run() {
        try{
            waitForSucessfulLogin();

            boolean stillInMenu = true;
            while(stillInMenu){

                    Msg<ClientMsg> msg = socket.receiveMessage();
                    System.out.println(msg);
                    if(socket.pendingInvite) {
                        Lobby otherLobby;
                        otherLobby = lobbies.get(socket.invitedBy);
                        switch (msg.type()) {

                            case JOIN:
                                otherLobby.lobbyState.get(socket.getName()).hasConnected = true;
                                System.out.println(socket.getName() + "is joining, and hasconnected = true!");;
                                System.out.println("Send lobby state: " + otherLobby.lobbyState);
                                for(LobbyPlayer playerInLobby : otherLobby.lobbyState.values()){
                                    nameSocketMap.get(playerInLobby.name).sendMessage(new MsgLobbyState(otherLobby.getLobbyStateCopy()));
                                }
                                this.lobby = otherLobby;
                                stillInMenu = false;
                                break;
                            case DECLINE:
                                socket.pendingInvite = false;
                                otherLobby.lobbyState.remove(socket.getName());
                                System.out.println("Send lobby state: " + otherLobby.lobbyState);
                                for(LobbyPlayer playerInLobby : otherLobby.lobbyState.values()){
                                    nameSocketMap.get(playerInLobby.name).sendMessage(new MsgLobbyState(otherLobby.getLobbyStateCopy()));
                                }
                                break;
                            default:
                                throw new RuntimeException(msg.toString());
                        }
                    }else{
                        switch (msg.type()){
                            case START_GAME:
                                lobby = new Lobby(socket.getName());
                                lobbies.put(socket.getName(), lobby);
                                stillInMenu = false;
                                break;
                        }
                    }


            }

            handleLobby();





        }catch(IOException e){
            e.printStackTrace();
            System.out.println(socket.getName() + " has disconnected.");
            System.out.println("Players: " + players);
            synchronized (players){
                players.remove(socket.getName());
                nameSocketMap.remove(socket.getName());
            }
        }
    }




    private void waitForSucessfulLogin() throws IOException {

        while(true){
            MsgText loginMsg = (MsgText) socket.receiveMessage();
            synchronized (players){
                String name = loginMsg.text;
                boolean freeName = ! players.contains(name);
                boolean validName = freeName && name.length() > 0;
                if(validName){
                    socket.sendMessage(new Msg(ServerMsg.OK));
                    players.add(name);
                    socket.setName(name);
                    nameSocketMap.put(name, socket);
                    System.out.println("Successful login: " + name);
                    System.out.println("Players: " + players);
                    return;
                }else{
                    socket.sendMessage(new MsgText(ServerMsg.NO, "Invalid name!"));
                }
            }
        }
    }

    private void handleLobby() throws IOException {
        while(true){
            Msg<ClientMsg> msg = socket.receiveMessage();
            switch (msg.type()){
                case INVITE:
                    handleInvite((MsgText) msg);
                    break;
                case KICK:
                    break;
                case START_GAME:
                    boolean enoughPlayers = lobby.lobbyState.size() > 1;
                    if(enoughPlayers){
                        for(LobbyPlayer player : lobby.lobbyState.values()){
                            nameSocketMap.get(player.name).sendMessage(new MsgGameIsStarting(4, 4));
                        }
                        return;
                    }else{
                        socket.sendMessage(new MsgText(ServerMsg.NO, "Not enough players!"));
                    }
            }
        }
    }

    private void handleInvite(MsgText inviteMsg) throws IOException {
        System.out.println("handleInvite(): " + inviteMsg);
        String invitedName = inviteMsg.text;
        String inviterName = socket.getName();
        boolean playerFound;
        synchronized (players) {
            playerFound = players.contains(invitedName);
        }
        boolean selfInvite = inviterName.equals(invitedName);
        RemoteSocket invitedSocket = nameSocketMap.get(invitedName);
        if(selfInvite){
            socket.sendMessage(new MsgText(ServerMsg.NO, "You can't invite yourself!"));
        }else if(!playerFound){
            socket.sendMessage(new MsgText(ServerMsg.NO, "Can't find that player!"));
        }else if(invitedSocket.pendingInvite) {
            socket.sendMessage(new MsgText(ServerMsg.NO, "That player already has a pending invite!"));
        }else{
            System.out.println("0x");
            socket.sendMessage(new Msg(ServerMsg.OK));
            System.out.println("Ax");
            lobby.lobbyState.put(invitedName, new LobbyPlayer(invitedName, true, false));
            System.out.println("Bx");
            invitedSocket.sendMessage(new MsgText(ServerMsg.INVITE, inviterName));
            System.out.println("Sending lobby state: ");
            System.out.println(lobby.lobbyState);
            socket.sendMessage(new MsgLobbyState(lobby.getLobbyStateCopy()));
            System.out.println("Cx");
            invitedSocket.pendingInvite = true;
            System.out.println("Dx");
            invitedSocket.invitedBy = inviterName;

            System.out.println("Ex");
        }
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
