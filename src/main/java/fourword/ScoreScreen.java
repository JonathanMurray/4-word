package fourword;

import android.content.Intent;
import fourword.messages.MsgGameFinished;
import fourword.messages.Msg;
import fourword.model.Cell;
import fourword.model.GridModel;
import fourword.states.GameState;
import fourword.states.StateTransition;

/**
 * Created by jonathan on 2015-06-24.
 */
public class ScoreScreen extends GameState {


    public ScoreScreen(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        activity.hideKeyboard();
        MsgGameFinished msg = (MsgGameFinished) data;
        Intent intent = new Intent(activity, ScoreActivity.class);
        intent.putExtra("data", msg.result);
        activity.startActivity(intent);
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
    public StateTransition handleServerMessage(Msg msg) {
        return StateTransition.STAY_HERE;
    }
}
