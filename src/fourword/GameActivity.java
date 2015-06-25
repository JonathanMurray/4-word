package fourword;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.debug.Debug;

import java.util.*;

/**
 * Created by jonathan on 2015-06-20.
 */
public class GameActivity extends SimpleLayoutGameActivity implements GameClient.Listener{
    private Font font;
    private Camera camera;
    private static final int NUM_COLS = 2;
    private static final int NUM_ROWS = 2;
    private boolean[][] lockedCells = new boolean[NUM_COLS][NUM_ROWS];
    private GridScene scene;
    private GridModel grid;

    private int layoutID = R.layout.game;

    private GameState state;
    private boolean hasActiveState = false;
    private Queue<GameServerMessage> messageQueue = new LinkedList<GameServerMessage>();
    private HashMap<StateName, GameState> fsm = new HashMap<StateName, GameState>();

    private final Object stateLock = new Object();

    private static final int SERVER_PORT = 4444;
    //String serverIP = "127.0.0.1";
    //serverIP = "10.0.2.2";
    private static final String SERVER_IP = "192.168.1.2";


    @Override
    protected void onCreateResources() {
        font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 128, Color.WHITE);
        font.load();
    }

    @Override
    protected Scene onCreateScene() throws InterruptedException {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        scene = new GridScene(this, font, 250, NUM_COLS, NUM_ROWS, this.getVertexBufferObjectManager(), camera);
        grid = new GridModel(NUM_COLS, NUM_ROWS);

        Client client = new GameClient(SERVER_IP, SERVER_PORT);
        List<AI> AIs = new ArrayList<AI>();
        AIs.add(new AI());
        AIs.add(new AI());
        //Client client = new OfflineClient(new AI_ServerBehaviour(AIs, NUM_COLS, NUM_ROWS));
        client.setMessageListener(this);

        ScoreCalculator scoreCalculator = new ScoreCalculator(new Dictionary());

        fsm.put(StateName.PICK_AND_PLACE_LETTER, new PickAndPlaceLetter(this, scene, grid, client));
        fsm.put(StateName.WAIT_FOR_SERVER, new WaitForServer(this, scene, grid, client));
        fsm.put(StateName.PLACE_OPPONENTS_LETTER, new PlaceOpponentsLetter(this, scene, grid, client));
        fsm.put(StateName.SCORE_SCREEN, new ScoreScreen(scoreCalculator, this, scene, grid, client));
        state = fsm.get(StateName.WAIT_FOR_SERVER);
        state.enter(null);
        hasActiveState = true;

        //setGridAndView('A', new Cell(0, 0));
        //setGridAndView('X', new Cell(1, 1));
        //setGridAndView('B', new Cell(1, 3));

        final EditText textInput = (EditText) findViewById(R.id.text_input);
        textInput.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    char ch = Character.toUpperCase(s.charAt(s.length()-1));
                    if(Character.isLetter(ch)) {
                        synchronized (stateLock){
                            state.userTypedLetter(ch);
                        }

                    }
                    s.clear();
                }
            }
        });

        scene.setGridTouchListener(new GridTouchListener() {
            @Override
            public void gridTouchEvent(Cell cell) {
                synchronized (stateLock){
                    state.userClickedCell(cell);
                }

            }
        });

        scene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                synchronized (stateLock){
                    StateTransition transition = state.onUpdate();
                    handleTransition(transition);
                }

            }

            @Override
            public void reset() {

            }
        });

        client.start();

        return scene;
    }

    @Override
    public void handleServerMessage(GameServerMessage msg) {
        synchronized (stateLock){
            Debug.d("   Received msg from server: " + msg);

            if(hasActiveState){
                Debug.d("   current state: " + state);
                StateTransition transition = state.handleServerMessage(msg);
                Debug.d("   transition: " + transition);
                handleTransition(transition);
            }else{
                Debug.d("   No active state to handle message. Putting it in queue.");
                messageQueue.add(msg);
            }
        }
    }

    private void handleTransition(StateTransition transition){
        if(transition.changeState){
            synchronized (stateLock){
                //hasActiveState = false;
                GameState previousState = state;
                GameState nextState = fsm.get(transition.newState);
                previousState.exit();
                nextState.enter(transition.data);
                state = nextState;
                processMessageQueue();
    //          hasActiveState = true;
            }

        }
    }

    private void processMessageQueue(){
        while(!messageQueue.isEmpty()){
            GameServerMessage msg = messageQueue.remove();
            state.handleServerMessage(msg);
        }
    }

    public void showKeyboard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                final EditText textInput = (EditText) findViewById(R.id.text_input);
                imm.showSoftInput(textInput, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public void hideKeyboard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                final EditText textInput = (EditText) findViewById(R.id.text_input);
                imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
            }
        });
    }

    @Override
    protected int getLayoutID() {
        return layoutID;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.surface_view;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, 1000, 1000);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
    }


    private void setGridAndView(char ch, Cell cell){
        scene.setCharAtCell(ch, cell);
        grid.setCharAtCell(ch, cell);
        //lockedCells[cell.x()][cell.y()] = true;
    }

    public void clickedDone(View view){
        synchronized (stateLock){
            StateTransition transition = state.userClickedDone();
            handleTransition(transition);
        }

    }

    public void setInfoText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.info_text)).setText(text);
            }
        });
    }

    public void setButtonEnabled(final boolean enabled) {
        runOnUiThread(new Runnable(){
            public void run(){
                ((Button) findViewById(R.id.doneButton)).setEnabled(enabled);
            }
        });
    }

    public void changeLayout(int layoutID){
        this.layoutID = layoutID;

    }

}
