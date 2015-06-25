package fourword;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PlaceOpponentsLetter extends GameState{

    private Cell placedCell;
    private char letter;

    public PlaceOpponentsLetter(GameActivity activity, GridScene scene, GridModel grid) {
        super(activity, scene, grid);
    }

    @Override
    public void enter(Object data) {
        GameServerMessage msg = (GameServerMessage) data;
        letter = msg.letter();
        String pickingPlayer = msg.pickingPlayerName();
        scene.dehighlightCell();
        activity.showKeyboard();
        activity.setInfoText(pickingPlayer + " picked " + letter + ". Place it somewhere!");
        placedCell = null;
    }

    @Override
    public void exit() {
        grid.setCharAtCell(letter, placedCell);
        Connection.instance().sendMessage(GameClientMessage.placeLetter(placedCell));
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
    public StateTransition handleServerMessage(GameServerMessage msg) {
        return StateTransition.STAY_HERE;
    }

    public String toString(){
        return StateName.PLACE_OPPONENTS_LETTER.toString();
    }

}
