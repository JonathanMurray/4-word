package fourword;

import fourword_shared.model.Cell;
import fourword_shared.model.GameResult;
import fourword_shared.model.GridModel;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jonathan on 2015-07-16.
 */
public class ScoreScene extends ExtendedScene{



    private final static int X_MARGIN = 50;
    private final static int X_SPACE = 10;
    private final static int Y_MARGIN = 50;

    private ColRowListener gridTouchListener;
    private final GameResult result;
    private final List<GridSceneGrid> graphicGrids  = new ArrayList<GridSceneGrid>();

    public ScoreScene(GameResult result, Color backgroundColor, Font smallFont,
                      final int cellSize, VertexBufferObjectManager vboManager){
        this.result = result;
        int i = 0;
        Iterator<GridModel> it = result.grids().values().iterator();
        GridModel someGrid = it.next();
        final int numCols = someGrid.getNumCols();
        final int numRows = someGrid.getNumRows();
        for(String playerName : result.grids().keySet()){
            GridModel grid = result.grids().get(playerName);
            final int x = X_MARGIN + i*X_SPACE + i*(cellSize*numCols);
            final int y = Y_MARGIN;
            GridSceneGrid graphicGrid = new GridSceneGrid(this,
                    smallFont, backgroundColor, x, y, cellSize,
                    numCols, numRows, vboManager);
            graphicGrids.add(graphicGrid);
            graphicGrid.renderGrid(grid);
            i++;
        }


    }


    public void setColRowListener(ColRowListener listener){
        gridTouchListener = listener;
    }

    public interface ColRowListener{
        void colRowChosen(String colRow);
    }

}
