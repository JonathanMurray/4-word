package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.messages.ServerMsg;
import fourword.model.GridModel;

import java.io.IOException;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-26.
 */
public abstract class PlayerSocket{

    private String name;

    public abstract void sendMessage(Msg<ServerMsg> msg) throws IOException;
    public abstract Msg<ClientMsg> receiveMessage() throws IOException, ClassNotFoundException;
    public abstract void close();
    public abstract InetAddress getInetAddress();

    private String invitedBy;
    private Lobby currentLobby;

    public PlayerSocket(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getInvitedBy(){
        return invitedBy;
    }

    public void setInvitedBy(String inviterName) {
        this.invitedBy = inviterName;
    }

    public void removeInvite() {
        invitedBy = null;
    }

    public boolean isInvited() {
        return invitedBy != null;
    }

    public void joinLobby(Lobby lobby) {
        this.currentLobby = lobby;
        this.invitedBy = null;
    }

    public boolean isInLobby() {
        return currentLobby != null;
    }

    public Lobby getLobby() {
        return currentLobby;
    }

    public void leaveLobby()  {
        currentLobby = null;
    }

    public abstract void initializeWithGrid(GridModel grid);

    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean isRemote();
}