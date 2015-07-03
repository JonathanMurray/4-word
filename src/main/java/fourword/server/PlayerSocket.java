package fourword.server;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.messages.ServerMsg;
import fourword.model.GridModel;
import fourword.model.Lobby;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by jonathan on 2015-06-26.
 */
public abstract class PlayerSocket{

    private String name;
    private String invitedBy;
    private Lobby currentLobby;
    private boolean hasDisconnected;

    public abstract void sendMessage(Msg<ServerMsg> msg) throws IOException;
    public abstract Msg<ClientMsg> receiveMessage() throws IOException, ClassNotFoundException;
    public abstract void close();
    public abstract InetAddress getInetAddress();

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

    public boolean isHostOfLobby(){
        return currentLobby.getHost().equals(getName());
    }

    public void leaveLobby()  {
        currentLobby = null;
    }

    public abstract void initializeWithGrid(GridModel grid);

    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean isRemote();

    public boolean hasDisconnected(){
        return hasDisconnected;
    }

    protected void setDisconnected(){
        hasDisconnected = true;
    }

    public String toString(){
        return name + (isInvited() ? "(inv. by " + invitedBy + ")" : "");
    }
}