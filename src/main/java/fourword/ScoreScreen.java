package fourword;

import android.content.Intent;
import fourword_shared.messages.Msg;
import fourword_shared.model.Cell;
import fourword_shared.model.GridModel;
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
        Msg.GameFinished msg = (Msg.GameFinished) data;
        Intent intent = new Intent(activity, ScoreActivity.class);
        intent.putExtra("data", msg.get());
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
    public StateTransition timeRanOut() {
        throw new RuntimeException("shouldnt happen here");
    }

    @Override
    public StateTransition handleServerMessage(Msg msg) {
        return StateTransition.STAY_HERE;
    }
}
