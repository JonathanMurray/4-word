package fourword;

import android.widget.Button;
import android.widget.TextView;
import com.example.android_test.R;

/**
 * Created by jonathan on 2015-06-23.
 */
public class WaitForOpponent extends GameState {

    private char receivedLetter;

    public WaitForOpponent(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        activity.setInfoText("Waiting for opponent...");
        activity.setButtonEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    receivedLetter = new AI().nextAction(grid);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void exit() {
        receivedLetter = 0;
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
        if(receivedLetter != 0){
            return StateTransition.change(StateName.PLACE_OPPONENTS_LETTER, receivedLetter);
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedDone() {
        return StateTransition.STAY_HERE;
    }
}
