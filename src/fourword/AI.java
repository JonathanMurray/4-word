package fourword;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jonathan on 2015-06-22.
 */
public class AI {

    private GridModel grid;
    private final List<Cell> emptyCells = new ArrayList<Cell>();



    public void initialize(GridModel grid){
        this.grid = grid;
        for(int x = 0; x < grid.getNumCols(); x++){
            for(int y = 0; y < grid.getNumRows(); y++){
                Cell cell = new Cell(x,y);
                emptyCells.add(cell);
            }
        }
    }

    public void placeLetter(char letter){
        Cell randomEmptyCell = emptyCells.remove(new Random().nextInt(emptyCells.size()));
        grid.setCharAtCell(letter, randomEmptyCell);
    }

    public char pickAndPlaceLetter(){
        char letter = randomLetter();
        placeLetter(letter);
        return letter;
    }

    private char randomLetter(){
        char letter = 'A';
        int offset = new Random().nextInt('Z' - 'A');
        letter = (char)(letter + offset);
        return letter;
    }

    public String toString(){
        return grid.toString();
    }

    public String getPlayerName(){
        return "Stupid_AI";
    }

}
