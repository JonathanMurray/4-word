package fourword;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.fourword.R;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.*;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-24.
 */
public class ScoreActivity extends SimpleLayoutGameActivity implements MsgListener<ServerMsg>{

    private final int camWidth = 1000;
    private Font smallFont;
    private Camera camera;
    private ScoreScene scene;
    private ViewGroup avatarRow;
    private int selectedPlayer;
    private List<AvatarWithScoreView> avatars = new ArrayList<AvatarWithScoreView>();
    private List<GridModel> grids = new ArrayList<GridModel>();
    private ViewGroup colButtons;
    private ViewGroup rowButtons;
    private ViewGroup shownWords;
    private FixedAspectRatioFrameLayout renderContainer;
    private RenderSurfaceView renderSurfaceView;

    private boolean hasSelectedRow;
    private int selectedRowColIndex;


    private Dictionary dictionary;
    private ScoreCalculator calculator;


    private GameResult result;
    private Color backgroundColor;
    private int numCols;
    private int numRows;
    private int cellSize;
    private int cellPixelSize;
    private float camHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        result = (GameResult) getIntent().getSerializableExtra("data");
        if(result == null){
            result = MockupFactory.createResult();
        }
        dictionary = new Dictionary(result.lowerWords());
        calculator = new ScoreCalculator(dictionary);

//        String text = buildString(result);
        final int GRID_HEIGHT_ASPECT_RATIO = getResources().getInteger(R.integer.scoreHeightAspectRatio);
        final int GRID_WIDTH_ASPECT_RATIO = getResources().getInteger(R.integer.scoreWidthAspectRatio);
        float HEIGHT_PROPORTION = (float) GRID_HEIGHT_ASPECT_RATIO / (float)GRID_WIDTH_ASPECT_RATIO;
        camHeight = camWidth * HEIGHT_PROPORTION;
        Iterator<GridModel> it = result.grids().values().iterator();
        GridModel someGrid = it.next();
        numCols = someGrid.getNumCols();
        numRows = someGrid.getNumRows();
        cellSize = (int) Math.min(camWidth/(float)numCols, camHeight/(float)numRows);
        System.out.println("cellSize: " + cellSize);
        super.onCreate(savedInstanceState);
        Connection.instance().setMessageListener(this);

        shownWords = (ViewGroup) findViewById(R.id.result_shown_words);
        renderContainer = (FixedAspectRatioFrameLayout) findViewById(R.id.render_container);
        setupAvatars();

    }

    private void setupAvatars(){
        avatarRow = (ViewGroup) findViewById(R.id.avatar_row);
        avatarRow.removeAllViews();

//        avatars.clear()

        int i = 0;
        for(String playerName : result.grids().keySet()){
            GridModel grid = result.grids().get(playerName);
            grids.add(grid);
            AvatarWithScoreView avatar = new AvatarWithScoreView(this);
            avatar.setPlayerName(playerName);
            int score = calculator.computeScore(grid);
            avatar.setScore(score);
            avatarRow.addView(avatar);
            avatars.add(avatar);
            final int avatarIndex = i;
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPlayer(avatarIndex);
                }
            });
            i++;
        }

    }

    private void selectPlayer(final int index){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avatars.get(selectedPlayer).setHighlighted(false);
                selectedPlayer = index;
                avatars.get(selectedPlayer).setHighlighted(true);
            }
        });

        if(scene != null){
            scene.renderGrid(grids.get(index));
        }
        if(hasSelectedRow){
            selectRow(selectedRowColIndex);
        }else{
            selectCol(selectedRowColIndex);
        }
        updateButtonsForSelectedPlayer(grids.get(index));
    }

    private void updateButtonsForSelectedPlayer(GridModel grid){
        final List<String> rows = grid.getRows();
        final List<String> cols = grid.getCols();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < rowButtons.getChildCount(); i++){
                    Button rowButton = (Button) rowButtons.getChildAt(i);
                    String row = rows.get(i);
                    int score = calculator.computeScore(row);
                    rowButton.setText("" + score);
                }
                for(int i = 0; i < colButtons.getChildCount(); i++){
                    Button colButton = (Button) colButtons.getChildAt(i);
                    String col = cols.get(i);
                    int score = calculator.computeScore(col);
                    colButton.setText("" + score);
                }
            }
        });

    }

    private int getButtonSize(){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
        return (int) px;
    }

    private void setupButtons(int numCols, int numRows, int celPixelSize){
        rowButtons = (ViewGroup) findViewById(R.id.result_row_buttons);
        rowButtons.removeAllViews();
        for(int i = 0; i < numRows; i++){
            Button rowButton = new Button(this);
            rowButton.setLayoutParams(new ViewGroup.LayoutParams(getButtonSize(), cellPixelSize));
            final int rowIndex = i;
            rowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectRow(rowIndex);
                }
            });
            rowButtons.addView(rowButton);
        }

        colButtons = (ViewGroup) findViewById(R.id.result_col_buttons);
        colButtons.removeAllViews();
        for(int i = 0; i < numCols; i++){
            Button colButton = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cellPixelSize, getButtonSize());
            boolean isFirstCol = i == 0;
            params.setMargins(0,0,0,0);
            if(isFirstCol){
                int margin = findViewById(R.id.result_row_buttons).getWidth();
                params.setMarginStart(margin);
            }
            colButton.setLayoutParams(params);

            final int colIndex = i;
            colButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectCol(colIndex);
                }
            });
            colButtons.addView(colButton);
        }
    }

    private void selectCol(int index){
        String text = grids.get(selectedPlayer).getCols().get(index);
        scene.highlightCol(index);
        showWordsFromColOrRow(text);
        hasSelectedRow = false;
        selectedRowColIndex = index;
    }

    private void selectRow(int index){
        String text = grids.get(selectedPlayer).getRows().get(index);
        scene.highlightRow(index);
        showWordsFromColOrRow(text);
        hasSelectedRow = true;
        selectedRowColIndex = index;
    }

    private void showWordsFromColOrRow(final String colOrRow){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shownWords.removeAllViews();
                TextView headerLine = new TextView(ScoreActivity.this);
                headerLine.setTextColor(android.graphics.Color.YELLOW);
                int totalScore = calculator.computeScore(colOrRow);
                headerLine.setText(colOrRow + " (" + totalScore + ")");
                shownWords.addView(headerLine);
                List<String> words = calculator.extractLowerWords(colOrRow);
                for(String word : words){
                    TextView wordView = new TextView(ScoreActivity.this);
                    int wordScore = calculator.getValidWordScore(word);
                    wordView.setText(word.toUpperCase() + " (" + wordScore + ")");
                    shownWords.addView(wordView);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }

    public void clickedReturn(View view){
        goToMenu();
    }

    private void goToMenu(){
        ChangeActivity.change(this, MenuActivity.class, new Bundle());
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        switch (msg.type()){
            case YOU_ARE_INVITED:
                String inviterName = ((Msg.YouAreInvited)msg).get();
                DialogFragment dialog = new InviteDialogFragment();
                Bundle args = new Bundle();
                args.putString(InviteDialogFragment.INVITER_NAME, inviterName);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "Invitation");
                return true;
            default:
                throw new RuntimeException(msg.toString());
        }
    }

    @Override
    public void lostConnection() {

    }

    @Override
    public void establishedConnection() {

    }

    @Override
    protected void onCreateResources() {
        int backgroundInt = getResources().getColor(android.R.color.holo_blue_light);
        backgroundColor = new Color(android.graphics.Color.red(backgroundInt)/255f,
                android.graphics.Color.green(backgroundInt)/255f, android.graphics.Color.blue(backgroundInt)/255f);

        int cellTextColor = android.graphics.Color.rgb(220, 220, 235);
        Typeface cellTextTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
        smallFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 1024, 1024, cellTextTypeface, (float) (0.7 * cellSize), cellTextColor);
        smallFont.load();
    }


    @Override
    protected Scene onCreateScene() throws InterruptedException {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        Map.Entry<String, GridModel> gridEntry = result.grids().entrySet().iterator().next();
        GridModel grid = gridEntry.getValue();

        scene = new ScoreScene(grid, backgroundColor, smallFont, cellSize,
                this.getVertexBufferObjectManager());
        scene.setBackground(new Background(backgroundColor));
        Connection.instance().setMessageListener(this);
        scene.renderGrid(grids.get(0));

        hasSelectedRow = true;
        selectedRowColIndex = 0;


        cellPixelSize = (int) ((float)cellSize/1000.0*renderContainer.getWidth());
        System.out.println("cellpixelsize: " + cellPixelSize);
        System.out.println("cellSize: " + cellSize);
        System.out.println("render withdt: " + renderContainer.getWidth());
        System.out.println("render height: " + renderContainer.getHeight());
        System.out.println("render measured height: " + renderContainer.getMeasuredHeight());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupButtons(numCols, numRows, cellPixelSize);
                selectPlayer(0);
            }
        });


        return scene;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.score_screen;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.score_surface_view;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, camWidth, camHeight);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
    }
}
