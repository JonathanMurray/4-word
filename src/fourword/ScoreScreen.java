package fourword;

import android.content.Intent;
import android.widget.EditText;
import com.example.android_test.R;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-24.
 */
public class ScoreScreen extends GameState {

    private GameResult result;
    private final ScoreCalculator scoreCalculator;

    public ScoreScreen(ScoreCalculator scoreCalculator, GameActivity activity, GridScene scene, GridModel grid, Client client) {
        super(activity, scene, grid, client);
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public void enter(Object data) {
        activity.hideKeyboard();
        GameServerMessage msg = (GameServerMessage) data;
        result = msg.result();
        Intent intent = new Intent(activity, ScoreActivity.class);
        intent.putExtra("data", result);
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
    public StateTransition handleServerMessage(GameServerMessage msg) {
        return StateTransition.STAY_HERE;
    }
}
