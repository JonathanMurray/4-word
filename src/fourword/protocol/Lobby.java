package fourword.protocol;

import fourword.messages.Msg;
import fourword.messages.MsgLobbyState;
import fourword.messages.ServerMsg;
import fourword.model.LobbyPlayer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class Lobby implements Serializable{
    private String hostingPlayer;
    private HashMap<String, LobbyPlayer> players = new HashMap<>();
    private ArrayList<String> sortedNames = new ArrayList<>();

    public Lobby(String hostingPlayer){
        this.hostingPlayer = hostingPlayer;
        players.put(hostingPlayer, LobbyPlayer.connectedHuman(hostingPlayer));
        sortedNames.add(hostingPlayer);
    }

    public void addPlayer(LobbyPlayer player){
        players.put(player.name, player);
        sortedNames.add(player.name);
    }

    public void removePlayer(String name){
        if(players.containsKey(name)){
            players.remove(name);
            sortedNames.remove(name);
        }else{
            throw new IllegalArgumentException(sortedNames + " doesn't contain '" + name + "'");
        }
    }

    public ArrayList<LobbyPlayer> getAllBots(){
        ArrayList<LobbyPlayer> bots = new ArrayList<>();
        for(LobbyPlayer player : players.values()){
            if(!player.isHuman){
                bots.add(player);
            }
        }
        return bots;
    }

    public ArrayList<LobbyPlayer> getAllHumans(){
        ArrayList<LobbyPlayer> humans = new ArrayList<>();
        for(LobbyPlayer player : players.values()){
            if(player.isHuman){
                humans.add(player);
            }
        }
        return humans;
    }

    public void setNewHost(String host){
        if(!sortedNames.contains(host)){
            throw new IllegalArgumentException("host " + host + " is not in lobby: " + sortedNames);
        }
        hostingPlayer = host;
    }

    public String getHost(){
        return hostingPlayer;
    }

    public int size(){
        return sortedNames.size();
    }

    public void setConnected(String playerName){
        players.get(playerName).hasConnected = true;
    }

    public boolean isConnected(String playerName){
        return players.get(playerName).hasConnected;
    }

    public boolean isHuman(String playerName){
        return players.get(playerName).isHuman;
    }

    public LobbyPlayer getPlayer(String name){
        return players.get(name);
    }

    public String getNameAtIndex(int index){
        return sortedNames.get(index);
    }

    public List<String> sortedNames(){
        return sortedNames;
    }

    public Lobby getCopy(){
        HashMap<String, LobbyPlayer> playersCopy = new HashMap<>();
        for(String k : players.keySet()){
            playersCopy.put(k, players.get(k).getCopy());
        }
        ArrayList<String> sortedNamesCopy = new ArrayList<>(sortedNames);
        Lobby copy = new Lobby(hostingPlayer);
        copy.sortedNames = sortedNamesCopy;
        copy.players = playersCopy;
        return copy;
    }

    public String toString(){
        return "Lobby(" + hostingPlayer + ", " + players + ", " + sortedNames + ")";
    }
}
