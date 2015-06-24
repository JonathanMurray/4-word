package fourword;

/**
 * Created by jonathan on 2015-06-23.
 */
public abstract class GameState {

    protected final GameActivity activity;
    protected final GridScene scene;
    protected final GridModel grid;
    protected final Client client;

    public GameState(GameActivity activity, GridScene scene, GridModel grid, Client client){
        this.activity = activity;
        this.scene = scene;
        this.grid = grid;
        this.client = client;
    }

    public abstract void enter(Object data);
    public abstract void exit();
    public abstract StateTransition userTypedLetter(char letter);
    public abstract StateTransition userClickedCell(Cell cell);
    public abstract StateTransition onUpdate();
    public abstract StateTransition userClickedDone();
    public abstract StateTransition handleServerMessage(GameServerMessage msg);
}