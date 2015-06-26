package fourword.messages;


import fourword.model.Cell;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public class ClientMsg implements Serializable {

    public static enum Action {
        PLACE_LETTER,
        PICK_AND_PLACE_LETTER;
    }

    private Action action;
    private Cell cell;
    private char letter;

    private ClientMsg(Action action, Cell cell, char letter){
        this.action = action;
        this.cell = cell;
        this.letter = letter;
    }
    public static ClientMsg placeLetter(Cell cell){
        return new ClientMsg(Action.PLACE_LETTER, cell, (char)0);
    }

    public static ClientMsg pickAndPlaceLetter(char letter, Cell cell){
        return new ClientMsg(Action.PICK_AND_PLACE_LETTER, cell, letter);
    }

    public Action action(){
        return action;
    }

    public Cell cell(){
        return cell;
    }

    public char letter(){
        if(action == Action.PLACE_LETTER){
            throw new UnsupportedOperationException();
        }
        return letter;
    }

    public String toString(){
        if(action == Action.PICK_AND_PLACE_LETTER){
            return "(" + action + ", " + letter + ", " + cell + ")";
        }
        return "(" + action + ", "  + cell + ")";
    }
}
