package fourword;

/**
 * Created by jonathan on 2015-06-23.
 */
public class WaitForServer extends GameState {

    public WaitForServer(GameActivity activity, GridScene scene, GridModel grid, Client client) {
        super(activity, scene, grid, client);
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
        switch(msg.command()){
            case PLACE_LETTER:
                return StateTransition.change(StateName.PLACE_OPPONENTS_LETTER, msg.letter());
            case PICK_AND_PLACE_LETTER:
                return StateTransition.change(StateName.PICK_AND_PLACE_LETTER);
            default:
                throw new RuntimeException("unhandled action for msg: " + msg);
        }
    }
}
