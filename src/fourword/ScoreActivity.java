package fourword;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
public class ScoreActivity extends Activity {

    private ScoreCalculator scoreCalculator = new ScoreCalculator(new Dictionary());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GameResult result = (GameResult) getIntent().getExtras().get("data");
        String text = buildString(result);
        Debug.d("Finding textView ...");
        TextView textView = ((TextView) findViewById(R.id.info_text));
        Debug.d(textView.toString());
        Debug.d("rendering result text ...");
        textView.setText(text);
    }

    private String buildString(GameResult result){
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
}
