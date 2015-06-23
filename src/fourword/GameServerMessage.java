package fourword;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameServerMessage implements Serializable{

    public static enum Command{
        PLACE_LETTER,
        PICK_AND_PLACE_LETTER;
    }

    private Command command;
    private char letter;

    private GameServerMessage(Command command, char letter){
        this.command = command;
        this.letter = letter;
    }
    public static GameServerMessage placeLetter(char letter){
        return new GameServerMessage(Command.PLACE_LETTER, letter);
    }

    public static GameServerMessage pickAndPlaceLetter(){
        return new GameServerMessage(Command.PICK_AND_PLACE_LETTER, (char)0);
    }

    public Command command(){
        return command;
    }

    public char letter(){
        if(command == Command.PICK_AND_PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return letter;
    }

    public String toString(){
        return "(" + command + ")";
    }
}
