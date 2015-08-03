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

    private final static int X_MARGIN = 0;
    private final static int X_SPACE = 10;
    private final static int Y_MARGIN = 0;

//    private ColRowListener gridTouchListener;
    private final GridSceneGrid graphicGrid;

    public ScoreScene(GridModel grid, Color backgroundColor, Font smallFont,
                      final int cellSize, VertexBufferObjectManager vboManager){
        int i = 0;
        final int numCols = grid.getNumCols();
        final int numRows = grid.getNumRows();
        final int x = X_MARGIN + i*X_SPACE + i*(cellSize*numCols);
        final int y = Y_MARGIN;
        graphicGrid = new GridSceneGrid(this,
                smallFont, backgroundColor, x, y, cellSize,
                numCols, numRows, vboManager);
        graphicGrid.renderGrid(grid);
    }

    public void renderGrid(GridModel grid){
        graphicGrid.renderGrid(grid);
    }

    public void highlightRow(int index){
        graphicGrid.highlightRow(index);
    }

    public void highlightCol(int index){
        graphicGrid.highlightCol(index);
    }

//    public void setColRowListener(ColRowListener listener){
//        gridTouchListener = listener;
//    }

//    public interface ColRowListener{
//        void colRowChosen(String colRow);
//    }

}
