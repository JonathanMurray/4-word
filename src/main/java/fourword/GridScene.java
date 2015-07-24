package fourword;

import android.content.Context;
import fourword_shared.model.Cell;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

/**
 * Created by jonathan on 2015-06-20.
 */
public class GridScene extends ExtendedScene{

    private GridTouchListener gridTouchListener;

    private Text bigLetter;

    final private GridSceneGrid grid;

    public GridScene(int topLeftX, int topLeftY, Color backgroundColor, Font smallFont,
                     Font bigFont, final int cellSize,
                     int numCols, int numRows, VertexBufferObjectManager vboManager
                     ){

        grid = new GridSceneGrid(this, smallFont, backgroundColor,
                topLeftX, topLeftY, cellSize,
                numCols, numRows, vboManager
        );

        setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
                    final float x = pSceneTouchEvent.getX();
                    final float y = pSceneTouchEvent.getY();
                    Cell clickedCell = grid.sceneTouch(x,y);
                    if(clickedCell != null){
                        gridTouchListener.gridTouchEvent(clickedCell);
                    }
                }
                return true;
            }
        });

        //Must be initialized with non-empty string since the max lngth of the text is set in the constructor
//        Debug.d("bigFont.getTexture.getWidth: " + bigFont.getTexture().getWidth());

        bigLetter = new Text(750-100, 250-150, bigFont, " ", vboManager);
        attachChild(bigLetter);
//        Debug.d("new Text(" + bigLetter.getX() + ", " + bigLetter.getY());


    }


    public void setGridTouchListener(GridTouchListener listener){
        gridTouchListener = listener;
    }



    public void setBigLetter(char letter) {
        String text = "" + letter;
        Debug.d("setBigLetter(" + text + ")");
        bigLetter.setText(text);
    }

    public boolean hasHighlighted() {
        return grid.hasHighlighted();
    }


    public void dehighlightCell() {
        grid.dehighlightCell();
    }


    public Cell getHighlighted() {
        return grid.getHighlighted();
    }

    public void removeCharAtCell(Cell cell) {
        grid.removeCharAtCell(cell);
    }

    public void setCharAtCell(char letter, Cell cell) {
        grid.setCharAtCell(letter, cell);
    }

    public void highlightCell(Cell cell) {
        grid.highlightCell(cell);

    }
}
