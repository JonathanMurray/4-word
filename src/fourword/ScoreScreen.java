package fourword;

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
        GameServerMessage msg = (GameServerMessage) data;
        result = msg.result();
        String s = buildString();
        activity.hideKeyboard();
        activity.setInfoText(s);
    }

    private String buildString(){
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, GridModel>> it = result.grids().entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, GridModel> e = it.next();
            sb.append("\n");
            sb.append(e.getKey() + "\n");
            sb.append("-----------------\n");
            GridModel grid = e.getValue();
            sb.append("ROWS:\n");
            for(String row : grid.getRows()){
                int score = scoreCalculator.computeScore(row);
                sb.append(row + " (" + score + ")\n");
            }
            sb.append("COLS:\n");
            for(String col : grid.getCols()){
                int score = scoreCalculator.computeScore(col);
                sb.append(col + " (" + score + ")\n");
            }
        }
        return sb.toString();
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
