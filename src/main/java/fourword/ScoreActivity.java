package fourword;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.states.*;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.*;
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

import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-24.
 */
public class ScoreActivity extends SimpleLayoutGameActivity implements MsgListener<ServerMsg>{

    private final int camWidth = 1000;
    private Font smallFont;
    private Camera camera;
    private ScoreScene scene;

    private GameResult result;
    private Color backgroundColor;
    private int cellSize;
    private float camHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        result = (GameResult) getIntent().getSerializableExtra("data");
        if(result == null){
            result = MockupFactory.createResult();
        }
        String text = buildString(result);
        final int GRID_HEIGHT_ASPECT_RATIO = getResources().getInteger(R.integer.gridHeightAspectRatio);
        final int GRID_WIDTH_ASPECT_RATIO = getResources().getInteger(R.integer.gridWidthAspectRatio);
        float HEIGHT_PROPORTION = (float) GRID_HEIGHT_ASPECT_RATIO / (float)GRID_WIDTH_ASPECT_RATIO;
        camHeight = camWidth * HEIGHT_PROPORTION;
        Iterator<GridModel> it = result.grids().values().iterator();
        GridModel someGrid = it.next();
        int numCols = someGrid.getNumCols();
        int numRows = someGrid.getNumRows();
        int gridWidth = (int) (camWidth / result.grids().size());
        cellSize = gridWidth / Math.max(numCols, numRows);
        System.out.println("cellSize: " + cellSize);
        super.onCreate(savedInstanceState);
        Connection.instance().setMessageListener(this);
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

    private String buildString(GameResult result){
        Dictionary dict = new Dictionary(result.lowerWords());
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, GridModel>> it = result.grids().entrySet().iterator();
        while(it.hasNext()){
            int totalScore = 0;
            Map.Entry<String, GridModel> e = it.next();
            sb.append("\n");
            sb.append(e.getKey() + "\n");
            sb.append("-----------------\n");
            GridModel grid = e.getValue();
            sb.append("ROWS:\n");
            for(String row : grid.getRows()){
                int score = ScoreCalculator.computeScore(row, dict);
                totalScore += score;
                sb.append(row + " (" + score + ")\n");
            }
            sb.append("COLS:\n");
            for(String col : grid.getCols()){
                int score = ScoreCalculator.computeScore(col, dict);
                totalScore += score;
                sb.append(col + " (" + score + ")\n");
            }
            sb.append("TOTAL: " + totalScore + "\n");
        }
        Debug.d(sb.toString());
        return sb.toString();
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
        scene = new ScoreScene(result, backgroundColor, smallFont, cellSize,
                this.getVertexBufferObjectManager());
        scene.setBackground(new Background(backgroundColor));

        Connection.instance().setMessageListener(this);

        scene.setColRowListener(new ScoreScene.ColRowListener() {
            @Override
            public void colRowChosen(String colRow) {
                System.out.println(colRow);
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
