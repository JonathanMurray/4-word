package fourword;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.fourword.R;

/**
 * Created by jonathan on 2015-07-29.
 */
public class AvatarWithScoreView extends LinearLayout{

    private AvatarView avatar;
    private TextView scoreText;

    public AvatarWithScoreView(Context context){
        super(context);
        inflate(context);
        setupViews();
    }

    public AvatarWithScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        setupViews();
    }

    private void inflate(Context context){
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.avatatar_with_score, this, true);
    }

    private void setupViews(){
        avatar = (AvatarView) findViewById(R.id.score_avatar);
        scoreText = (TextView) findViewById(R.id.score_text);
    }

    public void setPlayerName(String playerName){
        avatar.setPlayerName(playerName);
    }

    public void setHighlighted(boolean highlighted){
        avatar.setHighlighted(highlighted);
    }

    public void setScore(int score){
        this.scoreText.setText("" + score);
    }

}
