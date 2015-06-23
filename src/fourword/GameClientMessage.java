package fourword;


import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameClientMessage implements Serializable {

    public static enum Action {
        PLACE_LETTER,
        PICK_AND_PLACE_LETTER;
    }

    private Action action;
    private Cell cell;
    private char letter;

    private GameClientMessage(Action action, Cell cell, char letter){
        this.action = action;
        this.cell = cell;
        this.letter = letter;
    }
    public static GameClientMessage placeLetter(Cell cell){
        return new GameClientMessage(Action.PLACE_LETTER, cell, (char)0);
    }

    public static GameClientMessage pickAndPlaceLetter(char letter, Cell cell){
        return new GameClientMessage(Action.PICK_AND_PLACE_LETTER, cell, letter);
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
        return "(" + action + ", " + letter + ", " + cell + ")";
    }
}
