package fourword;

/**
 * Created by jonathan on 2015-06-22.
 */
public class UserAction {
    private Cell cell;
    private char letter;

    public UserAction(Cell cell, char letter){
        this.cell = cell;
        this.letter = letter;
    }

    public Cell cell(){
        return cell;
    }

    public char letter(){
        return letter;
    }
}
