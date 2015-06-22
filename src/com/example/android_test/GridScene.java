package com.example.android_test;

import android.content.Context;
import android.view.View;
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

import java.util.ArrayList;

/**
 * Created by jonathan on 2015-06-20.
 */
public class GridScene extends Scene{
    private static final char NULL_CHAR = '\u0000';
    private static int LINE_WIDTH = 4;
    private static int MARGIN = 0;
    private static Font font;
    private static Color LINE_COLOR = Color.WHITE;
    private final Context context;
    private int cellSize;
    private int numCols;
    private int numRows;
    private int gridWidth;
    private int gridHeight;
    private char[][] charCells;
    private Text[][] textCells;
    private Cell highlighted;
    private Rectangle highlightedRect;
    private boolean hasHighlightedCell;
    private VertexBufferObjectManager vboManager;
    private GridTouchListener gridTouchListener;
    private final Camera camera;

    private ArrayList<IEntity> attachQueue = new ArrayList<IEntity>();
    private ArrayList<IEntity> detachQueue = new ArrayList<IEntity>();

    public GridScene(Context context, Font font, final int cellSize, int numCols, int numRows, VertexBufferObjectManager vboManager, Camera camera){
        this.context = context;
        this.font = font;
        this.cellSize = cellSize;
        this.numCols = numCols;
        this.numRows = numRows;
        gridWidth = cellSize * numCols;
        gridHeight = cellSize * numRows;
        charCells = new char[numCols][numRows];
        textCells = new Text[numCols][numRows];
        this.vboManager = vboManager;
        this.camera = camera;

        for(int x = MARGIN; x <= gridWidth + MARGIN; x += cellSize){
            Line line = new Line(x, MARGIN, x, gridHeight + MARGIN, LINE_WIDTH, vboManager);
            line.setColor(LINE_COLOR);
            attachChild(line);
        }
        for(int y = MARGIN; y <= gridHeight + MARGIN; y += cellSize){
            Line line = new Line(MARGIN, y, gridWidth + MARGIN, y, LINE_WIDTH, vboManager);
            line.setColor(LINE_COLOR);
            attachChild(line);
        }

        setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN){
                    float x = pSceneTouchEvent.getX();
                    float y = pSceneTouchEvent.getY();
                    Cell cell = new Cell((int) ((x-MARGIN) / cellSize), (int) ((y-MARGIN) / cellSize));
                    if(gridTouchListener != null && isValidCell(cell)){
                        gridTouchListener.gridTouchEvent(cell);
                    }
                }
                return true;
            }
        });

        registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) { //
                for(IEntity e : detachQueue){
                    detachChild(e);
                }
                detachQueue.clear();
                for(IEntity e : attachQueue){
                    attachChild(e);
                }
                attachQueue.clear();
            }

            @Override
            public void reset() {

            }
        });
    }

    public void highlightCell(Cell cell){
        assertValidCell(cell);
        if(hasHighlightedCell == true){
            dehighlightCell();
        }
        hasHighlightedCell = true;
        highlighted = cell;
        highlightedRect = new Rectangle(
                cell.x() * cellSize + MARGIN,
                cell.y() * cellSize + MARGIN,
                cellSize,
                cellSize,
                vboManager);

        highlightedRect.setColor(new Color(0.3f, 0.5f, 0.6f));
        highlightedRect.setZIndex(-100); //far back
        attachChild(highlightedRect);
        sortChildren();
    }

    public void dehighlightCell(){
        hasHighlightedCell = false;
        detachChild(highlightedRect);
        highlightedRect = null;
    }

    public Cell getHighlighted(){
        return highlighted;
    }


    public boolean highlightedCell(){
        return hasHighlightedCell;
    }

    public boolean isValidCell(Cell cell){
        return cell.x() >= 0 && cell.x() < numCols && cell.y() >= 0 && cell.y() < numRows;
    }

    public void setCharAtCell(char ch, Cell cell){
        assertValidCell(cell);
        removeCharAtCell(cell);

        charCells[cell.x()][cell.y()] = ch;
        Text text = new Text((float) (cell.x()+0.3) * cellSize, (float) (cell.y()+0.2) * cellSize, font, "" + ch, vboManager);
        text.setZIndex(0);
        attachQueue.add(text);
        //attachChild(text);
        textCells[cell.x()][cell.y()] = text;
    }

    public char getCharAtCell(Cell cell){
        return charCells[cell.x()][cell.y()];
    }

    public boolean hasCharAtCell(Cell cell){
        return getCharAtCell(cell) != NULL_CHAR;
    }

    public void removeCharAtCell(Cell cell){
        assertValidCell(cell);
        Text currentText = textCells[cell.x()][cell.y()];
        if(currentText != null){
            if(attachQueue.contains(currentText)){ //hasn't been attached yet, just cancel it from the queue
                attachQueue.remove(currentText);
            }else{
                detachQueue.add(currentText); //has been attached, demand detachment
            }
        }
    }

    public void setGridTouchListener(GridTouchListener gridTouchListener) {
        this.gridTouchListener = gridTouchListener;
    }

    public String getCol(int colIndex){
        StringBuilder s = new StringBuilder();
        for(int y = 0; y < numRows; y++){
            s.append(getCharAtCell(new Cell(colIndex, y)));
        }
        return s.toString();
    }

    public String getRow(int rowIndex){
        StringBuilder s = new StringBuilder();
        for(int x = 0; x < numCols; x++){
            s.append(getCharAtCell(new Cell(x, rowIndex)));
        }
        return s.toString();
    }

    private void assertValidCell(Cell cell){
        if(!isValidCell(cell)){
            throw new IllegalArgumentException("Cell " + cell + " out of bounds. numCols: " + numCols + ", numRows: " + numRows);
        }
    }
}
