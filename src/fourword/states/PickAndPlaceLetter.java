package fourword.states;

import fourword.*;
import fourword.messages.Msg;
import fourword.messages.MsgPickAndPlaceLetter;
import fourword.model.Cell;
import fourword.model.GridModel;

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
        activity.showKeyboard();
        activity.setInfoText("Pick a letter and place it somewhere!");
        placedCell = null;
        placedLetter = 0;
        scene.dehighlightCell();
    }

    @Override
    public void exit() {
        grid.setCharAtCell(placedLetter, placedCell);
        Connection.instance().sendMessage(new MsgPickAndPlaceLetter(placedLetter, placedCell));
    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        if(scene.hasHighlighted()){
            Cell highlighted = scene.getHighlighted();
            boolean cellIsLocked = grid.hasCharAtCell(highlighted);
            if(!cellIsLocked){
                boolean alreadyPlacedLetter = placedLetter != 0 && placedCell != highlighted;
                if(alreadyPlacedLetter){
                    scene.removeCharAtCell(placedCell);
                }
                scene.setCharAtCell(letter, highlighted);
                placedCell = highlighted;
                placedLetter = letter;
            }
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        scene.highlightCell(cell);
        activity.showKeyboard();
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

        return StateTransition.change(StateName.WAIT_FOR_SERVER);
    }

    @Override
    public StateTransition handleServerMessage(Msg msg) {
        return StateTransition.STAY_HERE;
    }

    public String toString(){
        return StateName.PICK_AND_PLACE_LETTER.toString();
    }

}
