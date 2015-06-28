package fourword.model;

import fourword.model.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-22.
 */
public class GridModel implements Serializable {
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
        assertIsLetter(ch);
        charCells[cell.x()][cell.y()] = ch;
    }

    private boolean isLetter(char letter){
        return (letter >= 'A' && letter <= 'Z') || letter == 'Å' || letter == 'Ä' || letter == 'Ö';
    }

    private void assertIsLetter(char letter){
        if(!isLetter(letter)){
            throw new IllegalArgumentException("invalid leter: '" + letter + "'  (" + (int)letter + ")");
        }
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

    private String getCol(int colIndex){
        StringBuilder s = new StringBuilder();
        for(int y = 0; y < numRows; y++){
            s.append(getCharAtCell(new Cell(colIndex, y)));
        }
        return s.toString();
    }

    private String getRow(int rowIndex){
        StringBuilder s = new StringBuilder();
        for(int x = 0; x < numCols; x++){
            s.append(getCharAtCell(new Cell(x, rowIndex)));
        }
        return s.toString();
    }

    public List<String> getCols(){
        List<String> cols = new ArrayList<String>();
        for(int x = 0; x < numCols; x++){
            cols.add(getCol(x));
        }
        return cols;
    }

    public List<String> getRows(){
        List<String> rows = new ArrayList<String>();
        for(int y = 0; y < numRows; y++){
            rows.add(getRow(y));
        }
        return rows;
    }

    public int getNumCols(){
        return numCols;
    }

    public int getNumRows(){
        return numRows;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("\n ----------\n");
        for(int y = 0; y < numRows; y++){
            s.append("| ");
            for(int x = 0; x < numCols; x++){
                char cell = charCells[x][y];
                s.append(cell != 0 ? cell : "*");
                s.append(" ");
            }
            s.append("|\n");
        }
        s.append(" ----------");
        return s.toString();
    }
}
