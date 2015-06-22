package fourword;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PlaceOpponentsLetter extends GameState{

    private Cell placedCell;
    private char letter;

    public PlaceOpponentsLetter(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        letter = (char) data;
        scene.dehighlightCell();
        activity.setInfoText("Place the letter " + letter);
        placedCell = null;
    }

    @Override
    public void exit() {

    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        if(!grid.hasCharAtCell(cell)){
            scene.highlightCell(cell);
            if(placedCell != null){
                scene.removeCharAtCell(placedCell);
            }
            placedCell = cell;
            scene.setCharAtCell(letter, cell);
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition onUpdate() {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedDone() {
        if(placedCell == null){
            return StateTransition.STAY_HERE;
        }
        grid.setCharAtCell(letter, placedCell);
        return StateTransition.change(StateName.PICK_AND_PLACE_LETTER);
    }
}
