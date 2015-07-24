package fourword;

import fourword_shared.model.Cell;
import fourword_shared.model.GridModel;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

/**
 * Created by jonathan on 2015-07-16.
 */
public class GridSceneGrid {

    private static int LINE_WIDTH = 10;
    private static Color HIGHLIGHT_COLOR = new Color(0.3f, 0.3f, 0.4f);
    private static Color FILLED_GRID_COLOR = new Color(0.3f, 0.3f, 0.3f);

    private final VertexBufferObjectManager vboManager;
    private final Font smallFont;
    private final Color lineColor;
    private final ExtendedScene scene;
    private final int cellSize;
    private final int topLeftXScene;
    private final int topLeftYScene;
    private final int numCols;
    private final int numRows;
    private final int gridWidth;
    private final int gridHeight;

    private final Text[][] textCells;
    private Cell highlighted;
    private Rectangle highlightedRect;
    private boolean hasHighlightedCell;

    public GridSceneGrid(ExtendedScene scene, Font smallFont, Color backgroundColor,
                     final int topLeftXScene, final int topLeftYScene, final int cellSize,
                     int numCols, int numRows, VertexBufferObjectManager vboManager
                     ){
        this.scene= scene;
        this.smallFont = smallFont;
        this.topLeftXScene = topLeftXScene;
        this.topLeftYScene = topLeftYScene;
        lineColor = backgroundColor;
        this.numCols = numCols;
        this.numRows = numRows;

        this.cellSize = cellSize;
        gridWidth = cellSize * numCols;
        gridHeight = cellSize * numRows;
        //charCells = new char[numCols][numRows];
        textCells = new Text[numCols][numRows];
        this.vboManager = vboManager;

        for(int x = topLeftXScene; x <= gridWidth + topLeftXScene; x += cellSize){
            Line line = new Line(x, topLeftYScene, x, gridHeight + topLeftYScene, LINE_WIDTH, vboManager);
            line.setColor(lineColor);
            scene.attachChild(line);
        }
        for(int y = topLeftYScene; y <= gridHeight + topLeftYScene; y += cellSize){
            Line line = new Line(topLeftXScene, y, gridWidth + topLeftXScene, y, LINE_WIDTH, vboManager);
            line.setColor(lineColor);
            scene.attachChild(line);
        }

        Rectangle filledGrid = new Rectangle(topLeftXScene, topLeftYScene, cellSize*numCols, cellSize*numRows, vboManager);
        filledGrid.setColor(FILLED_GRID_COLOR);
        filledGrid.setZIndex(-100);
        scene.attachChild(filledGrid);

    }

    public void highlightCell(Cell cell){
        assertValidCell(cell);
        if(hasHighlightedCell == true){
            dehighlightCell();
        }
        hasHighlightedCell = true;
        highlighted = cell;
        highlightedRect = new Rectangle(
                cell.x() * cellSize + topLeftXScene,
                cell.y() * cellSize + topLeftYScene,
                cellSize,
                cellSize,
                vboManager);

        highlightedRect.setColor(HIGHLIGHT_COLOR);
        highlightedRect.setZIndex(-100); //far black_border
        scene.safeAttach(highlightedRect);
    }

    public void dehighlightCell(){
        hasHighlightedCell = false;
        scene.safeDetach(highlightedRect);
        highlightedRect = null;
    }

    public Cell getHighlighted(){
        return highlighted;
    }


    public boolean hasHighlighted(){
        return hasHighlightedCell;
    }

    public boolean isValidCell(Cell cell){
        return cell.x() >= 0 && cell.x() < numCols && cell.y() >= 0 && cell.y() < numRows;
    }

    public void setCharAtCell(char ch, Cell cell){
        assertValidCell(cell);
        removeCharAtCell(cell);

        //charCells[cell.x()][cell.y()] = ch;
        Text text = new Text(topLeftXScene + (float) (cell.x()+0.2) * cellSize, topLeftYScene + (float) (cell.y()) * cellSize, smallFont, "" + ch, vboManager);
        text.setZIndex(0);
        scene.safeAttach(text);
        textCells[cell.x()][cell.y()] = text;
    }


    public void removeCharAtCell(Cell cell){
        assertValidCell(cell);
        Text currentText = textCells[cell.x()][cell.y()];
        if(currentText != null){
            scene.safeDetach(currentText);
        }
    }

    public Cell sceneTouch(float sceneX, float sceneY){
        Cell cell = new Cell(
                (int) ((sceneX - topLeftXScene) / cellSize),
                (int) ((sceneY - topLeftYScene) / cellSize));
        if (isValidCell(cell)) {
            return cell;
        }
        return null;
    }

    public void renderGrid(GridModel grid){
        for(int x = 0; x < grid.getNumCols(); x ++){
            for(int y = 0; y < grid.getNumRows(); y++){
                Cell cell = new Cell(x, y);
                setCharAtCell(grid.getCharAtCell(cell), cell);
            }
        }
    }

    private void assertValidCell(Cell cell){
        if(!isValidCell(cell)){
            throw new IllegalArgumentException("Cell " + cell + " out of bounds. numCols: " + numCols + ", numRows: " + numRows);
        }
    }
}
