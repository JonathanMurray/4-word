package fourword.server;

import fourword_shared.messages.*;
import fourword_shared.model.Cell;
import fourword_shared.model.GridModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jonathan on 2015-06-25.
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

    public Msg<ClientMsg> handleServerMessageAndProduceReply(Msg<ServerMsg> msg){
        assertIsInitialized();
        Cell randomEmptyCell;
        switch (msg.type()){
            case DO_PICK_AND_PLACE_LETTER:
                char letter = randomLetter();
                randomEmptyCell = randomEmptyCell();
                grid.setCharAtCell(letter, randomEmptyCell);
                return new MsgPickAndPlaceLetter(letter, randomEmptyCell);
            case DO_PLACE_LETTER:
                randomEmptyCell = randomEmptyCell();
                grid.setCharAtCell(((MsgRequestPlaceLetter)msg).letter, randomEmptyCell);
                return new MsgPlaceLetter(randomEmptyCell);
            default:
                return null;
        }
//        throw new RuntimeException();
    }

    private Cell randomEmptyCell(){
        return emptyCells.remove(new Random().nextInt(emptyCells.size()));
    }

    private char randomLetter(){
        char letter = 'A';
        int offset = new Random().nextInt('Z' - 'A');
        letter = (char)(letter + offset);
        return letter;
    }

    private void assertIsInitialized(){
        if(grid == null){
            throw new RuntimeException("AI has not been initialized with a grid!");
        }
    }

    public String toString(){
        return grid.toString();
    }

}
