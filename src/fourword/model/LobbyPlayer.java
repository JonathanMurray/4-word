package fourword.model;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-27.
 */
public class LobbyPlayer implements Serializable, Cloneable {
    public String name;
    public boolean isHuman;
    public boolean hasConnected;

    public LobbyPlayer(String name, boolean isHuman, boolean hasConnected) {
        this.name = name;
        this.isHuman = isHuman;
        this.hasConnected = hasConnected;
    }

    public String toString(){
        return "LobbyPlayer{" + name + ", isHuman: " + isHuman + ", hasCOnnected: " + hasConnected + "}";
    }

    public LobbyPlayer getCopy(){
        try {
            return (LobbyPlayer) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
