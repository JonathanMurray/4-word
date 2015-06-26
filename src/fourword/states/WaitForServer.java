package fourword.states;

import fourword.*;
import fourword.messages.MsgWaitingForPlayerMove;
import fourword.messages.ServerMsg;
import fourword.model.Cell;
import fourword.model.GridModel;

/**
 * Created by jonathan on 2015-06-23.
 */
public class WaitForServer extends GameState {

    public WaitForServer(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        activity.setInfoText("Waiting for opponent...");
        activity.setButtonEnabled(false);
    }

    @Override
    public void exit() {
        activity.setButtonEnabled(true);
    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition onUpdate() {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedDone() {
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition handleServerMessage(ServerMsg msg) {
        switch(msg.type()){
            case PLACE_LETTER:
                return StateTransition.change(StateName.PLACE_OPPONENTS_LETTER, msg);
            case PICK_AND_PLACE_LETTER:
                return StateTransition.change(StateName.PICK_AND_PLACE_LETTER);
            case GAME_FINISHED:
                return StateTransition.change(StateName.SCORE_SCREEN, msg);
            case WAITING_FOR_PLAYER_MOVE:
                String opponentName = ((MsgWaitingForPlayerMove)msg).playerName;
                activity.setInfoText("Waiting for " + opponentName + " to make a move...");
                return StateTransition.STAY_HERE;
        }
        throw new RuntimeException();
    }

    public String toString(){
        return StateName.WAIT_FOR_SERVER.toString();
    }
}
