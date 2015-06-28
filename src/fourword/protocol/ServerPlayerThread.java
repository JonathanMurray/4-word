package fourword.protocol;

import fourword.messages.*;
import fourword.model.LobbyPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class ServerPlayerThread implements Runnable {

    private final PlayerSocket thisSocket;

    private final static int NUM_COLS = 2;
    private final static int NUM_ROWS = 1;

    private Object playersLock = new Object();

    private final Server server;
    private final List<String> playerNames;
    private final HashMap<String, Lobby> hostLobbyMap;
    private final HashMap<String, PlayerSocket> nameSocketMap;

    ServerPlayerThread(Server server, RemoteSocket socket, List<String> playerNames, HashMap<String, Lobby> hostLobbyMap, HashMap<String, PlayerSocket> nameSocketMap) {
        this.server = server;
        this.thisSocket = socket;
        this.playerNames = playerNames;
        this.hostLobbyMap = hostLobbyMap;
        this.nameSocketMap = nameSocketMap;
    }

    @Override
    public void run() {
        try{
            waitForSucessfulLogin();

            while(true){

                Msg<ClientMsg> msg = thisSocket.receiveMessage();
                System.out.println(msg);
                if(thisSocket.isInvited()) {
                    Lobby otherLobby;
                    otherLobby = hostLobbyMap.get(thisSocket.getInvitedBy());
                    switch (msg.type()) {

                        case JOIN:
                            otherLobby.setConnected(thisSocket.getName());
                            broadcastLobbyState(otherLobby);
                            thisSocket.joinLobby(otherLobby);
                            break;
                        case DECLINE:
                            thisSocket.removeInvite();
                            otherLobby.removePlayer(thisSocket.getName());
                            broadcastLobbyState(otherLobby);
                            break;
                        default:
                            throw new RuntimeException(msg.toString());
                    }
                }else{
                    switch (msg.type()){
                        case CREATE_GAME:
                            thisSocket.joinLobby(new Lobby(thisSocket.getName()));
                            hostLobbyMap.put(thisSocket.getName(), thisSocket.getLobby());
                            break;
                        case INVITE:
                            handleInvite((MsgText) msg);
                            break;
                        case ADD_BOT:
                            String botName = server.generateBotName();
                            synchronized (playersLock){
                                playerNames.add(botName);
                                //The bot-socket has no dedicated thread like the human-sockets
                                BotSocket botSocket = new BotSocket(new AI(), botName);
                                nameSocketMap.put(botName, botSocket);
                                botSocket.joinLobby(thisSocket.getLobby());
                                thisSocket.getLobby().addPlayer(LobbyPlayer.bot(botName));
                                broadcastLobbyState(thisSocket.getLobby());
                            }
                            break;
                        case KICK:
                            handleKick((MsgText) msg);
                            break;
                        case START_GAME:
                            Lobby lobby = thisSocket.getLobby();
                            boolean enoughPlayers = lobby.size() > 1;
                            if(enoughPlayers){
                                broadcastInLobby(lobby, new MsgGameIsStarting(NUM_COLS, NUM_ROWS));
//                                for(String player : thisSocket.getLobby().sortedNames()){
//                                    nameSocketMap.get(player).sendMessage(new MsgGameIsStarting(NUM_COLS, NUM_ROWS));
//                                }

                                server.startGameHostedBy(thisSocket.getName(), lobby.size(), NUM_COLS, NUM_ROWS);
                                for(LobbyPlayer bot : lobby.getAllBots()){
                                    server.joinGameHostedBy(thisSocket.getName(), nameSocketMap.get(bot.name));
                                }
                            }else{
                                thisSocket.sendMessage(new MsgText(ServerMsg.NO, "Not enough playerNames!"));
                            }
                            break;
                        case CONFIRM_GAME_STARTING:
                            boolean newGameStarted = server.joinGameHostedBy(thisSocket.getLobby().getHost(), thisSocket);
                            if(newGameStarted){
                                hostLobbyMap.remove(thisSocket.getLobby().getHost());
                            }
                            thisSocket.leaveLobby();
                            return; //Return from this runnable. It's job is done!
                    }
                }

            }

        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
            System.out.println(thisSocket.getName() + " has disconnected.");
            synchronized (playersLock){
                playerNames.remove(thisSocket.getName());
                nameSocketMap.remove(thisSocket.getName());
                hostLobbyMap.remove(thisSocket.getName());
                System.out.println("Players: " + playerNames);
            }

        }
    }



    private void waitForSucessfulLogin() throws IOException, ClassNotFoundException {

        while(true){
            MsgText loginMsg = (MsgText) thisSocket.receiveMessage();

            String name = loginMsg.text;
            boolean freeName;
            synchronized (playersLock) {
                freeName = !playerNames.contains(name);
            }
            boolean validName = freeName && name.length() > 0;
            if(validName) {
                thisSocket.sendMessage(new Msg(ServerMsg.OK));
                synchronized (playersLock) {
                    playerNames.add(name);
                    thisSocket.setName(name);
                    nameSocketMap.put(name, thisSocket);
                    System.out.println("Successful login: " + name);
                    System.out.println("Players: " + playerNames);
                }
                return;
            }else{
                thisSocket.sendMessage(new MsgText(ServerMsg.NO, "Invalid name!"));
            }
        }
    }

    private void handleKick(MsgText kickMsg) throws IOException {
        System.out.println("handleKick(): " + kickMsg);
        String kickedPlayer = kickMsg.text;
        PlayerSocket kickedSocket = nameSocketMap.get(kickedPlayer);
        kickedSocket.sendMessage(new Msg(ServerMsg.YOU_WERE_KICKED));
        kickedSocket.leaveLobby();
        thisSocket.getLobby().removePlayer(kickedPlayer);
        broadcastLobbyState(thisSocket.getLobby());
    }


    //Not sent to bots
    public void broadcastLobbyState(Lobby lobby) throws IOException {
        broadcastInLobby(lobby, new MsgLobbyState(lobby.getCopy()));
    }

    //Not sent to bots
    public void broadcastInLobby(Lobby lobby, Msg<ServerMsg> msg) throws IOException {
        System.out.println("Broadcast in lobby: " + msg);
        for(String playerInLobby : lobby.sortedNames()){
            if(lobby.isConnected(playerInLobby) && lobby.isHuman(playerInLobby)){
                nameSocketMap.get(playerInLobby).sendMessage(msg);
            }
        }
    }

    private void handleInvite(MsgText inviteMsg) throws IOException {
        System.out.println("handleInvite(): " + inviteMsg);
        String invitedName = inviteMsg.text;
        String inviterName = thisSocket.getName();
        System.out.println("inviterName: " + invitedName);
        boolean playerFound, selfInvite, invitedIsRemoteHuman;
        PlayerSocket invitedSocket;

        synchronized (playersLock) {
            invitedSocket = nameSocketMap.get(invitedName);
            playerFound = playerNames.contains(invitedName) && invitedSocket.isRemote();
            selfInvite = inviterName.equals(invitedName);
        }
        if(selfInvite){
            thisSocket.sendMessage(new MsgText(ServerMsg.NO, "You can't invite yourself!"));
        }else if(!playerFound){
            thisSocket.sendMessage(new MsgText(ServerMsg.NO, "Can't find that player!"));
        }else if(invitedSocket.isInvited()) {
            thisSocket.sendMessage(new MsgText(ServerMsg.NO, "That player already has a pending invite!"));
        }else if(invitedSocket.isInLobby()) {
            thisSocket.sendMessage(new MsgText(ServerMsg.NO, "That player is already in a lobby!"));
        }else{
            thisSocket.sendMessage(new Msg(ServerMsg.OK));
            thisSocket.getLobby().addPlayer(LobbyPlayer.pendingHuman(invitedName));
            invitedSocket.sendMessage(new MsgText(ServerMsg.YOU_ARE_INVITED, inviterName));
            broadcastLobbyState(thisSocket.getLobby());
            invitedSocket.setInvitedBy(inviterName);
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
