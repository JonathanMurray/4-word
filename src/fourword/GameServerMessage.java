package fourword;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameServerMessage implements Serializable{

    public static enum Type {
        PLACE_LETTER,
        PICK_AND_PLACE_LETTER,
        GAME_FINISHED,
        WAITING_FOR_MORE_PLAYERS,
        GAME_IS_STARTING;
    }

    private final Type type;
    private final char letter;
    private final String pickingPlayerName;
    private final GameResult result;
    private final List<LobbyPlayer> lobbyPlayers;


    private GameServerMessage(Type type, char letter, String pickingPlayerName, GameResult result, List<LobbyPlayer> lobbyPlayers){
        this.type = type;
        this.letter = letter;
        this.pickingPlayerName = pickingPlayerName;
        this.result = result;
        this.lobbyPlayers = lobbyPlayers;
    }
    public static GameServerMessage placeLetter(char letter, String pickingPlayerName){
        return new GameServerMessage(Type.PLACE_LETTER, letter, pickingPlayerName, null, null);
    }

    public static GameServerMessage pickAndPlaceLetter(){
        return new GameServerMessage(Type.PICK_AND_PLACE_LETTER, (char)0, null, null, null);
    }

    public static GameServerMessage gameFinished(GameResult result){
        return new GameServerMessage(Type.GAME_FINISHED, (char)0, null, result, null);
    }

    public static GameServerMessage waitingForMorePlayers(List<LobbyPlayer> lobbyPlayers){
        return new GameServerMessage(Type.WAITING_FOR_MORE_PLAYERS, (char)0, null, null, lobbyPlayers);
    }

    public static GameServerMessage gameIsStarting(){
        return new GameServerMessage(Type.GAME_IS_STARTING, (char)0, null, null, null);
    }

    public Type type(){
        return type;
    }

    public char letter(){
        if(type != Type.PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return letter;
    }

    public String pickingPlayerName(){
        if(type != Type.PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return pickingPlayerName;
    }

    public GameResult result(){
        if(type != Type.GAME_FINISHED){
            throw new UnsupportedOperationException();
        }
        return result;
    }

    public String toString(){
        return "(" + type + ")" + (letter != 0 ? "[" + letter + ":" + pickingPlayerName +  "]" : "");
    }

    public static class LobbyPlayer implements Serializable{
        String name;
        boolean isHuman;
        boolean hasConnected;

        public LobbyPlayer(String name, boolean isHuman, boolean hasConnected) {
            this.name = name;
            this.isHuman = isHuman;
            this.hasConnected = hasConnected;
        }
    }
}
