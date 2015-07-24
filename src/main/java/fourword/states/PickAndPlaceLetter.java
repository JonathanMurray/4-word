package fourword.states;

import fourword.*;
import fourword_shared.messages.Msg;
import fourword_shared.model.Cell;
import fourword_shared.model.GridModel;

import java.io.IOException;
import java.util.Random;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PickAndPlaceLetter extends GameState {

    private Cell placedCell;
    private char pickedLetter;

    public PickAndPlaceLetter(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        SoundManager.instance().play(SoundManager.YOUR_TURN);
        activity.startTimer();
        activity.showKeyboard();
        activity.setInfoText("Pick a letter and place it somewhere!");
        placedCell = null;
        pickedLetter = 0;
        scene.dehighlightCell();
    }

    @Override
    public void exit() {
        activity.stopTimer();
        grid.setCharAtCell(pickedLetter, placedCell);
        scene.setBigLetter((char)0);
        activity.hideKeyboard();
        activity.doneThinking();
        try {
            Connection.instance().sendMessage(new Msg.PickAndPlaceLetter(pickedLetter, placedCell));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        scene.setBigLetter(letter);
        pickedLetter = letter;
        if(scene.hasHighlighted()){
            Cell highlighted = scene.getHighlighted();
            boolean cellIsFree = !grid.hasCharAtCell(highlighted);
            if(cellIsFree){
                boolean alreadyPlacedSomewhereElse = pickedLetter != 0 && placedCell != null && placedCell != highlighted;
                if(alreadyPlacedSomewhereElse){
                    scene.removeCharAtCell(placedCell);
                }
                scene.setCharAtCell(letter, highlighted);
                placedCell = highlighted;
                pickedLetter = letter;
            }
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        if(!grid.hasCharAtCell(cell)){
            if(pickedLetter != 0){
                boolean hasPlacedSomewhere = placedCell != null;
                if(hasPlacedSomewhere){
                    scene.removeCharAtCell(placedCell);
                }
                placedCell = cell;
                scene.setCharAtCell(pickedLetter, cell);
            }
            scene.highlightCell(cell);
            activity.showKeyboard();
        }

        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition onUpdate() {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedDone() {
        if(placedCell == null || pickedLetter == 0){
            return StateTransition.STAY_HERE;
        }

        return StateTransition.change(StateName.WAIT_FOR_SERVER);
    }

    @Override
    public StateTransition timeRanOut() {
        if(pickedLetter == 0){
            userTypedLetter(randomLetter());
        }
        if(placedCell == null){
            userClickedCell(grid.getRandomFreeCell());
        }
        return userClickedDone();
    }

    private char randomLetter(){
        int A = 65;
        int Z = 90;
        return (char) (A + new Random().nextInt(Z - A));
    }

    @Override
    public StateTransition handleServerMessage(Msg msg) {
        return StateTransition.STAY_HERE;
    }

    public String toString(){
        return StateName.PICK_AND_PLACE_LETTER.toString();
    }

}
