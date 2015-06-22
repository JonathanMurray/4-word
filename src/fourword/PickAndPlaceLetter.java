package fourword;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.android_test.R;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PickAndPlaceLetter extends GameState {

    private Cell placedCell;
    private char placedLetter;

    public PickAndPlaceLetter(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        activity.setInfoText("Pick and place a letter");
        placedCell = null;
        placedLetter = 0;
        scene.dehighlightCell();
    }

    @Override
    public void exit() {

    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        Cell highlighted = scene.getHighlighted();
        //boolean cellIsLocked = lockedCells[highlighted.x()][highlighted.y()];
        boolean cellIsLocked = grid.hasCharAtCell(highlighted);
        if(!cellIsLocked){
            boolean alreadyPlacedLetter = placedLetter != 0 && placedCell != highlighted;
            if(alreadyPlacedLetter){
                scene.removeCharAtCell(placedCell);
            }
            //((TextView)findViewById(R.id.title)).setText(s.toString());
            scene.setCharAtCell(letter, highlighted);
            placedCell = highlighted;
            placedLetter = letter;
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        scene.highlightCell(cell);
        activity.bringUpKeyboard();
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
        grid.setCharAtCell(placedLetter, placedCell);
        return StateTransition.change(StateName.WAIT_FOR_OPPONENT);
    }
}
