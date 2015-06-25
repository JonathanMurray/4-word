package fourword;

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
    public StateTransition handleServerMessage(GameServerMessage msg) {
        switch(msg.type()){
            case PLACE_LETTER:
                return StateTransition.change(StateName.PLACE_OPPONENTS_LETTER, msg);
            case PICK_AND_PLACE_LETTER:
                return StateTransition.change(StateName.PICK_AND_PLACE_LETTER);
            case GAME_FINISHED:
                return StateTransition.change(StateName.SCORE_SCREEN, msg);
        }
        throw new RuntimeException();
    }

    public String toString(){
        return StateName.WAIT_FOR_SERVER.toString();
    }
}
