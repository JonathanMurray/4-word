package fourword;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android_test.R;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.Cell;
import fourword_shared.model.GameSettings;
import fourword_shared.model.GridModel;
import fourword.states.*;



import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.util.*;

/**
 * Created by jonathan on 2015-06-20.
 */
public class GameActivity extends SimpleLayoutGameActivity implements MsgListener<ServerMsg> {
    private Font smallFont;
    private Font bigFont;
    private Camera camera;
    private GridScene scene;
    private GridModel grid;
    private boolean isGameRunning;

    private int layoutID = R.layout.game;

//    private int NUM_COLS;
//    private int NUM_ROWS;
//    private int TIME_PER_TURN;
    private GameSettings SETTINGS;
    private int cellSize;
    private float HEIGHT_PROPORTION;
    private final int MARGIN = 20;
    private int camWidth = 1000;
    private float camHeight;

    private final HashMap<String, Boolean> thinkingPlayers = new HashMap<String, Boolean>();
    private String[] playerNames;
    private String activePlayer;

    private GameState state;
    private Queue<Msg> messageQueue = new LinkedList<Msg>();
    private HashMap<StateName, GameState> fsm = new HashMap<StateName, GameState>();
    private int secondsLeft;

    private Handler messageHandler;
    private Ticker ticker;

    private final Object stateLock = new Object();

    ViewGroup timerSection;
    ProgressBar timer;

    @Override
    public void onBackPressed(){
        DialogCreator.changeActivityQuestion(this, "Leave game",
                "Are you sure? This will cancel the game for all participants!",
                new Msg.LeaveGame(), MenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        isGameRunning = true;
        SoundManager.instance().play(SoundManager.ENTER_GAME);
        final int GRID_HEIGHT_ASPECT_RATIO = getResources().getInteger(R.integer.gridHeightAspectRatio);
        final int GRID_WIDTH_ASPECT_RATIO = getResources().getInteger(R.integer.gridWidthAspectRatio);
        HEIGHT_PROPORTION = (float) GRID_HEIGHT_ASPECT_RATIO / (float)GRID_WIDTH_ASPECT_RATIO;
        camHeight = camWidth * HEIGHT_PROPORTION;

        SETTINGS = (GameSettings) getIntent().getExtras().getSerializable(getString(R.string.GAME_SETTINGS));

//        NUM_COLS = (Integer) getIntent().getExtras().get(getString(R.string.NUM_COLS));
//        NUM_ROWS = (Integer) getIntent().getExtras().get(getString(R.string.NUM_ROWS));
        playerNames = getIntent().getStringArrayExtra(getString(R.string.PLAYER_NAMES));
//        TIME_PER_TURN = getIntent().getExtras().getInt(getString(R.string.TIME_PER_TURN));
        for(String name : playerNames){
            thinkingPlayers.put(name, true);
        }
        cellSize = (int) (camWidth * HEIGHT_PROPORTION - MARGIN*2) / Math.max(numCols(), numRows());
        super.onCreate(pSavedInstanceState);


        timerSection = (ViewGroup) findViewById(R.id.timer_section);
        timer = (ProgressBar) findViewById(R.id.progress_bar);
        timerSection.setVisibility(View.INVISIBLE);
        messageHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Debug.e("handleMessage: " + msg.arg1);
                secondsLeft --;
                timer.setProgress((int) (100 * (secondsLeft/(double)timePerTurn())));
                timer.postInvalidate();
                if(secondsLeft < 0 ) {
                    StateTransition transition = state.timeRanOut();
                    timerSection.setVisibility(View.INVISIBLE);
                    handleTransition(transition);
                }
                return false;
            }
        });

        updateAvatarLayout();
    }

    public void doneThinking(){
        thinkingPlayers.put(Persistent.instance().playerName(), false);
        updateAvatarLayout();
    }

    public void updateAvatarLayout(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup avatarRow = ((ViewGroup) findViewById(R.id.avatar_row));
                avatarRow.removeAllViews();
                int i = 0;
                for (String player : playerNames) {
//                    final int playerIndex = i;

                    AvatarView avatarView = new AvatarView(GameActivity.this);
                    avatarView.setPlayerName(player);
                    boolean isThinking = thinkingPlayers.get(player);
                    avatarView.setHighlighted(!isThinking);
                    if(player.equals(activePlayer)){
                        avatarView.setTextColor(android.graphics.Color.RED);
                    }else{
                        avatarView.setTextColor(android.graphics.Color.WHITE);
                    }


//                    ViewGroup avatarView = (ViewGroup) View.inflate(GameActivity.this, R.layout.game_avatar, null);
//                    ((TextView) avatarView.findViewById(R.id.avatar_name)).setText(player);
//                    ViewGroup outer = ((ViewGroup)avatarView.findViewById(R.id.avatar_outer));
//                    TextView avatarName = (TextView) avatarView.findViewById(R.id.avatar_name);

//                    if (thinkingPlayers.get(player)) {
//                        outer.setBackground(null);
//                    }else{
//                        outer.setBackground(getResources().getDrawable(R.drawable.green_border));
//                    }

//                    if(player.equals(activePlayer)){
//                        avatarName.setTextColor(android.graphics.Color.RED);
//                    }else{
//                        avatarName.setTextColor(android.graphics.Color.WHITE);
//                    }

                    avatarRow.addView(avatarView);
                    i++;
                }

            }
        });
    }



    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {

        switch (msg.type()){
            case GAME_CRASHED:
                isGameRunning = false;
                DialogCreator.changeActivityForced(
                        this,
                        "Whoops",
                        "The game ended unexpectedly. A player may have disconnected.",
                        MenuActivity.class);
                break;

            case GAME_PLAYER_DONE_THINKING:
                String playerName = ((Msg.PlayerDoneThinking)msg).get();
                thinkingPlayers.put(playerName, false);
                SoundManager.instance().play(SoundManager.PLAYER_DONE_THINKING);
                updateAvatarLayout();
                break;

            case GAME_PLAYERS_TURN:
                for(String name : playerNames){
                    thinkingPlayers.put(name, true);
                }
                activePlayer = ((Msg.PlayersTurn)msg).get();
                updateAvatarLayout();;
                break;

            case GAME_ENDED:
                isGameRunning = false;
                Msg.GameEnded gameEnded = (Msg.GameEnded) msg;
                DialogCreator.changeActivityForced(
                        this,
                        "Whoops",
                        "The game ended since " + gameEnded.leaverName + " left!",
                        MenuActivity.class);
                break;

            case SET_LETTER_AT_CELL:
                Msg.SetLetterAtCell setLetter = (Msg.SetLetterAtCell) msg;
                scene.setCharAtCell(setLetter.letter, setLetter.cell);
                grid.setCharAtCell(setLetter.letter, setLetter.cell);
                break;

            default:
                if(!isGameRunning){
                    return false;
                }
                synchronized (stateLock){
                    Debug.d("   Received msg from server: " + msg);
                    Debug.d("   current state: " + state);
                    StateTransition transition = state.handleServerMessage(msg);
                    Debug.d("   transition: " + transition);
                    handleTransition(transition);
                }
                break;
        }

        return true;
    }

    public void startTimer(){
        if(timePerTurn() > 0){
            secondsLeft = timePerTurn();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timer.setProgress(100);
                    timerSection.setVisibility(View.VISIBLE);
                    timer.invalidate();
                }
            });
            updateAvatarLayout();
            ticker = new Ticker(messageHandler, timePerTurn());
            ticker.start();
        }
    }

    public void stopTimer(){
        if(timePerTurn() > 0){
            ticker.interrupt();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timerSection.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(timePerTurn() > 0) {
            ticker.interrupt();
        }
    }

    private void handleTransition(StateTransition transition){
        if(transition.changeState){
            synchronized (stateLock){
                GameState previousState = state;
                GameState nextState = fsm.get(transition.newState);
                previousState.exit();
                nextState.enter(transition.data);
                state = nextState;
                processMessageQueue();
            }
        }
    }

    private void processMessageQueue(){
        while(!messageQueue.isEmpty()){
            Msg msg = messageQueue.remove();
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

    private int numRows(){
        return SETTINGS.getInt(GameSettings.IntAttribute.ROWS);
    }

    private int numCols(){
        return SETTINGS.getInt(GameSettings.IntAttribute.COLS);
    }

    private int timePerTurn(){
        return SETTINGS.getInt(GameSettings.IntAttribute.TIME_PER_TURN);
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
    protected void onCreateResources() {
        int cellTextColor = android.graphics.Color.rgb(220, 220, 235);
        Typeface cellTextTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
        smallFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 1024, 1024, cellTextTypeface, (float) (0.7*cellSize), cellTextColor);
        smallFont.load();

        Typeface bigLetterTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
        bigFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 1024, 1024, bigLetterTypeface, 256, cellTextColor);
        bigFont.load();
    }

    @Override
    protected Scene onCreateScene() throws InterruptedException {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        int backgroundInt = getResources().getColor(android.R.color.holo_blue_light);
        Color backgroundColor = new Color(android.graphics.Color.red(backgroundInt)/255f,
                android.graphics.Color.green(backgroundInt)/255f, android.graphics.Color.blue(backgroundInt)/255f);

        scene = new GridScene(MARGIN, MARGIN, backgroundColor, smallFont, bigFont, cellSize, numCols(),
                numRows(), this.getVertexBufferObjectManager());
        scene.setBackground(new Background(backgroundColor));
        grid = new GridModel(numCols(), numRows());


        fsm.put(StateName.PICK_AND_PLACE_LETTER, new PickAndPlaceLetter(this, scene, grid));
        fsm.put(StateName.WAIT_FOR_SERVER, new WaitForServer(this, scene, grid));
        fsm.put(StateName.PLACE_OPPONENTS_LETTER, new PlaceOpponentsLetter(this, scene, grid));
        fsm.put(StateName.SCORE_SCREEN, new ScoreScreen(this, scene, grid));
        state = fsm.get(StateName.WAIT_FOR_SERVER);
        state.enter(null);
        Connection.instance().setMessageListener(this);

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


        return scene;
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
//        RenderSurfaceView renderSurface = (RenderSurfaceView) findViewById(getRenderSurfaceViewID());
//        final float heightWidthRatio = (float)renderSurface.getHeight() / (float)renderSurface.getWidth();

        camera = new Camera(0, 0, camWidth, camHeight);
//        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);

        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
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

}
