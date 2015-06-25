package fourword;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jonathan on 2015-06-25.
 */
public class MessageAI {
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

    public GameClientMessage handleServerMessageAndProduceReply(GameServerMessage msg){
        assertIsInitialized();
        Cell randomEmptyCell;
        switch (msg.type()){
            case PICK_AND_PLACE_LETTER:
                char letter = randomLetter();
                randomEmptyCell = randomEmptyCell();
                grid.setCharAtCell(letter, randomEmptyCell);
                return GameClientMessage.pickAndPlaceLetter(letter, randomEmptyCell);
            case PLACE_LETTER:
                randomEmptyCell = randomEmptyCell();
                grid.setCharAtCell(msg.letter(), randomEmptyCell);
                return GameClientMessage.placeLetter(randomEmptyCell);
            case GAME_FINISHED:
                return null;
            case WAITING_FOR_MORE_PLAYERS:
                return null;
            case GAME_IS_STARTING:
                return null;
        }
        throw new RuntimeException();
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
