package fourword;

import android.content.Context;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by jonathan on 2015-06-22.
 */
public class GridModel {
    private static final char NULL_CHAR = '\u0000';
    private char[][] charCells;
    private int numCols;
    private int numRows;

    public GridModel(int numCols, int numRows){
        charCells = new char[numCols][numRows];
        this.numCols = numCols;
        this.numRows = numRows;
    }

    public void setCharAtCell(char ch, Cell cell){
        charCells[cell.x()][cell.y()] = ch;
    }

    public char getCharAtCell(Cell cell){
        return charCells[cell.x()][cell.y()];
    }

    public boolean hasCharAtCell(Cell cell){
        return getCharAtCell(cell) != NULL_CHAR;
    }

    public void removeCharAtCell(Cell cell){
        charCells[cell.x()][cell.y()] = NULL_CHAR;
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
}
