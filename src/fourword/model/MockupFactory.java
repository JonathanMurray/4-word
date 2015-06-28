package fourword.model;

import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-29.
 */
public class MockupFactory {

    public static GameResult createResult(){
        HashMap<String,GridModel> grids = new HashMap<>();
        grids.put("PLAYER_1", createGrid(4, 4));
        grids.put("PLAYER_2", createGrid(4, 4));
        return new GameResult(grids);
    }

    public static GridModel createGrid(int numCols, int numRows){
        GridModel grid = new GridModel(numCols, numRows);
        for(int x = 0; x < numCols; x++){
            for(int y =0; y < numRows; y++){
                grid.setCharAtCell('X', new Cell(x,y));
            }
        }
        return grid;
    }

}
