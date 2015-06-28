package fourword.model;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-27.
 */
public class LobbyPlayer implements Serializable, Cloneable {
    final public String name;
    final public boolean isHuman;
    public boolean hasConnected;

    private LobbyPlayer(String name, boolean isHuman, boolean hasConnected) {
        this.name = name;
        this.isHuman = isHuman;
        this.hasConnected = hasConnected;
    }

    public static LobbyPlayer connectedHuman(String name){
        return new LobbyPlayer(name, true, true);
    }

    public static LobbyPlayer pendingHuman(String name){
        return new LobbyPlayer(name, true, false);
    }

    public static LobbyPlayer bot(String name){
        return new LobbyPlayer(name, false, true);
    }

    public String toString(){
        return "P{" + name + ", hum: " + isHuman + ", conn: " + hasConnected + "}";
    }

    public LobbyPlayer getCopy(){
        try {
            return (LobbyPlayer) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
