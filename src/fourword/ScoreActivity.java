package fourword;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;
import com.example.android_test.R;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.debug.Debug;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 2015-06-24.
 */
public class ScoreActivity extends SimpleLayoutGameActivity {

    private Font font;
    private Scene scene;
    private Camera camera;
    private ScoreCalculator scoreCalculator = new ScoreCalculator(new Dictionary());

    @Override
    protected void onCreateResources() {
        font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 128, Color.WHITE);
        font.load();
    }

    @Override
    protected Scene onCreateScene() throws InterruptedException {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        scene = new Scene();
        ((TextView)findViewById(R.id.info_text)).setText(buildString());
        return scene;
    }

    private String buildString(){
        GameResult result = (GameResult) getIntent().getExtras().get("data");
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
                int score = scoreCalculator.computeScore(row);
                totalScore += score;
                sb.append(row + " (" + score + ")\n");
            }
            sb.append("COLS:\n");
            for(String col : grid.getCols()){
                int score = scoreCalculator.computeScore(col);
                totalScore += score;
                sb.append(col + " (" + score + ")\n");
            }
            sb.append("TOTAL: " + totalScore + "\n");
        }
        Debug.d(sb.toString());
        return sb.toString();
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

        camera = new Camera(0, 0, 1000, 1000);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
    }
}
