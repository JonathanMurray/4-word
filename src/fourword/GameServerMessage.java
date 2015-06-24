package fourword;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameServerMessage implements Serializable{

    public static enum Command{
        PLACE_LETTER,
        PICK_AND_PLACE_LETTER,
        GAME_FINISHED;
    }

    private final Command command;
    private final char letter;
    private final String pickingPlayerName;
    private final GameResult result;


    private GameServerMessage(Command command, char letter, String pickingPlayerName, GameResult result){
        this.command = command;
        this.letter = letter;
        this.pickingPlayerName = pickingPlayerName;
        this.result = result;
    }
    public static GameServerMessage placeLetter(char letter, String pickingPlayerName){
        return new GameServerMessage(Command.PLACE_LETTER, letter, pickingPlayerName, null);
    }

    public static GameServerMessage pickAndPlaceLetter(){
        return new GameServerMessage(Command.PICK_AND_PLACE_LETTER, (char)0, null, null);
    }

    public static GameServerMessage gameFinished(GameResult result){
        return new GameServerMessage(Command.GAME_FINISHED, (char)0, null, result);
    }

    public Command command(){
        return command;
    }

    public char letter(){
        if(command != Command.PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return letter;
    }

    public String pickingPlayerName(){
        if(command != Command.PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return pickingPlayerName;
    }

    public GameResult result(){
        if(command != Command.GAME_FINISHED){
            throw new UnsupportedOperationException();
        }
        return result;
    }

    public String toString(){
        return "(" + command + ")" + (letter != 0 ? "[" + letter + ":" + pickingPlayerName +  "]" : "");
    }
}
