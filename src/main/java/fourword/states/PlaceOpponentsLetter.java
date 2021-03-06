package fourword.states;

import fourword.*;
import fourword_shared.messages.Msg;
import fourword_shared.model.Cell;
import fourword_shared.model.GridModel;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PlaceOpponentsLetter extends GameState {

    private Cell placedCell;
    private char letter;

    public PlaceOpponentsLetter(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        activity.startTimer();
        Msg.RequestPlaceLetter msg = (Msg.RequestPlaceLetter) data;
        scene.dehighlightCell();
        System.out.println("place pp hide key");
        activity.hideKeyboard();
        System.out.println("after hide");
        letter = msg.letter;
        scene.setBigLetter(letter);
        activity.setInfoText(msg.playerName + " picked " + letter + ". Place it somewhere!");
        placedCell = null;
    }

    @Override
    public void exit() {
        activity.stopTimer();
        grid.setCharAtCell(letter, placedCell);
        scene.setBigLetter((char)0);
        activity.doneThinking();
        try {
            Connection.instance().sendMessage(new Msg.PlaceLetter(placedCell));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        return StateTransition.change(StateName.WAIT_FOR_SERVER);
    }

    @Override
    public StateTransition timeRanOut() {
        if(placedCell == null){
            userClickedCell(grid.getRandomFreeCell());
        }

        return userClickedDone();
    }

    @Override
    public StateTransition handleServerMessage(Msg msg) {
        return StateTransition.STAY_HERE;
    }

    public String toString(){
        return StateName.PLACE_OPPONENTS_LETTER.toString();
    }

}
