package fourword;

/**
 * Created by jonathan on 2015-06-23.
 */
public class PickAndPlaceLetter extends GameState {

    private Cell placedCell;
    private char placedLetter;

    public PickAndPlaceLetter(GameActivity activity, GridScene scene, GridModel grid, Client client) {
        super(activity, scene, grid, client);
    }

    @Override
    public void enter(Object data) {
        activity.setInfoText("Pick and place a letter");
        placedCell = null;
        placedLetter = 0;
        scene.dehighlightCell();
    }

    @Override
    public void exit() {

    }

    @Override
    public StateTransition userTypedLetter(char letter) {
        Cell highlighted = scene.getHighlighted();
        //boolean cellIsLocked = lockedCells[highlighted.x()][highlighted.y()];
        boolean cellIsLocked = grid.hasCharAtCell(highlighted);
        if(!cellIsLocked){
            boolean alreadyPlacedLetter = placedLetter != 0 && placedCell != highlighted;
            if(alreadyPlacedLetter){
                scene.removeCharAtCell(placedCell);
            }
            //((TextView)findViewById(R.id.title)).setText(s.toString());
            scene.setCharAtCell(letter, highlighted);
            placedCell = highlighted;
            placedLetter = letter;
        }
        return StateTransition.STAY_HERE;
    }

    @Override
    public StateTransition userClickedCell(Cell cell) {
        scene.highlightCell(cell);
        activity.bringUpKeyboard();
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
        grid.setCharAtCell(placedLetter, placedCell);
        client.sendMessage(GameClientMessage.pickAndPlaceLetter(placedLetter, placedCell));
        return StateTransition.change(StateName.WAIT_FOR_SERVER);
    }

    @Override
    public StateTransition handleServerMessage(GameServerMessage msg) {
        return StateTransition.STAY_HERE;
    }
}
