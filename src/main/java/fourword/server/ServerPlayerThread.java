package fourword.server;

import fourword_shared.messages.*;
import fourword_shared.model.LobbyPlayer;
import fourword_shared.model.Lobby;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class ServerPlayerThread implements Runnable {

    private final PlayerSocket thisSocket;

//    private final static int NUM_COLS = 3;
//    private final static int NUM_ROWS = 3;

//    private Object playersLock = new Object();
    private boolean isLoggedIn;

    private final Server server;

    ServerPlayerThread(Server server, boolean isLoggedIn, RemoteSocket socket) {
        this.server = server;
        this.isLoggedIn = isLoggedIn;
        this.thisSocket = socket;
    }

    @Override
    public void run() {
        try{
            boolean done = false;
            while(!done){
                Msg<ClientMsg> msg = thisSocket.receiveMessage();
                done = handleClientMessage(msg);
            }
        }catch(EOFException e){
            playerDisconnected();
        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
            playerDisconnected();
        }
    }

    private void playerDisconnected(){
        System.out.println(thisSocket.getName() + " has disconnected.");
        try {
            server.removePlayer(thisSocket.getName());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private boolean handleClientMessage(Msg<ClientMsg> msg) throws IOException {
        switch (msg.type()){
            case LOGIN:
                if(isLoggedIn){
                    throw new RuntimeException();
                }
                String name = ((MsgText)msg).text;
                if(server.validPlayerName(name)) {
                    thisSocket.sendMessage(new Msg(ServerMsg.OK));
                    thisSocket.setName(name);
                    server.addPlayer(name, thisSocket);
                    System.out.println(name + " has logged in.");
                    server.printState();
                    isLoggedIn = true;
                }else{
                    thisSocket.sendMessage(new MsgText(ServerMsg.NO, "Invalid name!"));
                }
                break;

            case ACCEPT_INVITE:
                Lobby otherLobby = server.getLobbyOfHost(thisSocket.getInvitedBy());
                otherLobby.setConnected(thisSocket.getName());
                broadcastLobbyState(otherLobby);
                thisSocket.joinLobby(otherLobby);
                server.printState();
                break;
            case DECLINE_INVITE:
                otherLobby = server.getLobbyOfHost(thisSocket.getInvitedBy());
                thisSocket.removeInvite();
                otherLobby.removePlayer(thisSocket.getName());
                broadcastLobbyState(otherLobby);
                server.printState();
                break;

            case LOGOUT:
                server.removePlayer(thisSocket.getName());
                break;
            case CREATE_LOBBY:
                Lobby newLobby = new Lobby(thisSocket.getName());
                thisSocket.joinLobby(newLobby);
                server.addLobby(thisSocket.getName(), newLobby);
                server.printState();
                break;
            case INVITE_TO_LOBBY:
                handleInvite((MsgText) msg);
                break;
            case ADD_BOT_TO_LOBBY:
                String botName = server.generateBotName();

                //The bot-socket has no dedicated thread like the human-sockets
                BotSocket botSocket = new BotSocket(new AI(), botName);
                server.addPlayer(botName, botSocket);
                botSocket.joinLobby(thisSocket.getLobby());
                thisSocket.getLobby().addPlayer(LobbyPlayer.bot(botName));
                broadcastLobbyState(thisSocket.getLobby());
                server.printState();
                break;
            case KICK_FROM_LOBBY:
                handleKick((MsgText) msg);
                break;
            case LEAVE_LOBBY:
                leaveLobby(thisSocket);
                break;
            case START_GAME_FROM_LOBBY:
                Lobby lobby = thisSocket.getLobby();
                boolean enoughPlayers = lobby.size() > 1;
                if(enoughPlayers){
                    broadcastInLobby(lobby, new MsgGameIsStarting(lobby.numCols, lobby.numRows, lobby.sortedNames().toArray(new String[0])));
                    server.createGameHostedBy(thisSocket.getName(), lobby.size(), lobby.numCols, lobby.numRows);
                    for(LobbyPlayer bot : lobby.getAllBots()){
                        server.joinGameHostedBy(thisSocket.getName(), bot.name);
                    }
                    server.printState();
                }else{
                    thisSocket.sendMessage(new MsgText(ServerMsg.NO, "Not enough playerNames!"));
                }

                break;

            case LOBBY_SET_DIMENSIONS:
                thisSocket.getLobby().numRows = ((MsgLobbySetDim)msg).numRows;
                thisSocket.getLobby().numCols = ((MsgLobbySetDim)msg).numCols;
                broadcastLobbyState(thisSocket.getLobby());
                break;

            case CONFIRM_GAME_STARTING:
                String host = ((MsgText)msg).text;
                boolean newGameStarted = server.joinGameHostedBy(host, thisSocket.getName());
                if(newGameStarted){
                    server.removeLobby(host);
                }
                thisSocket.leaveLobby();
                server.printState();
                return true; //Return from this runnable. It's job is done!

            case QUICK_START_GAME:
                MsgQuickStartGame quickStart = (MsgQuickStartGame) msg;
                server.createGameHostedBy(thisSocket.getName(), 1 + quickStart.numBots, quickStart.numCols, quickStart.numRows);
                List<String> names = new ArrayList<String>();
                for(int i = 0; i < quickStart.numBots; i++){
                    botName = server.generateBotName();
                    botSocket = new BotSocket(new AI(), botName);
                    names.add(botName);
                    server.addPlayer(botName, botSocket);
                    server.joinGameHostedBy(thisSocket.getName(), botName);
                }
                names.add(thisSocket.getName());
                thisSocket.sendMessage(new MsgGameIsStarting(quickStart.numCols, quickStart.numRows, names.toArray(new String[0])));
                server.printState();
                return false;
        }
        return false; //Thread is not done yet
    }

    private void handleKick(MsgText kickMsg) throws IOException {
        String kickedPlayer = kickMsg.text;
        PlayerSocket kickedSocket = server.getSocket(kickedPlayer);
        kickedSocket.sendMessage(new Msg(ServerMsg.YOU_WERE_KICKED));
        leaveLobby(kickedSocket);
    }

    private void leaveLobby(PlayerSocket socket) throws IOException {
        Lobby lobby = socket.getLobby();
        boolean isHost = socket.isHostOfLobby();
        socket.leaveLobby();
        lobby.removePlayer(socket.getName());

        if(isHost){
            ArrayList<LobbyPlayer> otherHumansInLobby = lobby.getAllHumans();
            if(otherHumansInLobby.isEmpty()){
                for(LobbyPlayer bot : lobby.getAllBots()){
                    server.removePlayer(bot.name);
                }
                server.removeLobby(socket.getName());
            }else{
                String newHost = otherHumansInLobby.get(0).name;
                lobby.setNewHost(newHost);
            }
        }
        broadcastLobbyState(lobby);
        server.printState();
    }



    //Not sent to bots
    public void broadcastLobbyState(Lobby lobby) throws IOException {
        broadcastInLobby(lobby, new MsgLobbyState(lobby));
    }

    //Not sent to bots
    public void broadcastInLobby(Lobby lobby, Msg<ServerMsg> msg) throws IOException {
        for(String playerInLobby : lobby.sortedNames()){
            if(lobby.isConnected(playerInLobby) && lobby.isHuman(playerInLobby)){
                server.getSocket(playerInLobby).sendMessage(msg);
            }
        }
    }

    private void handleInvite(MsgText inviteMsg) throws IOException {
        String invitedName = inviteMsg.text;
        String inviterName = thisSocket.getName();
        boolean playerFound, selfInvite;
        PlayerSocket invitedSocket;

        invitedSocket = server.getSocket(invitedName);
        playerFound = server.containsPlayer(invitedName) && invitedSocket.isRemote();
        selfInvite = inviterName.equals(invitedName);
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
            server.printState();
        }
    }

}
