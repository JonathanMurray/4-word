package fourword;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.fourword.R;

/**
 * Created by jonathan on 2015-07-24.
 */
public class AvatarView extends LinearLayout{

    private String playerName;
    private boolean isUnknownAvatar;
    private boolean isHighlighted;
    private boolean isNameVisible;

    private ImageView imageView;
    private TextView nameView;
    private LinearLayout outer;


    public AvatarView(Context context){
        super(context);
        inflate(context);
        setupViews();
        setPlayerName("");
        setUnknownAvatar(false);
        setHighlighted(false);
        setNameVisible(true);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        setupViews();
        setupAttributes(context, attrs);
    }

    private void inflate(Context context){
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.game_avatar, this, true);
    }

    private void setupViews(){
        imageView = (ImageView) findViewById(R.id.avatar_img);
        nameView = (TextView) findViewById(R.id.avatar_name);
        outer = (LinearLayout) findViewById(R.id.avatar_outer);
    }

    private void setupAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AvatarView,
                0, 0);

        try {
            setPlayerName(a.getString(R.styleable.AvatarView_playerName));
            setUnknownAvatar(a.getBoolean(R.styleable.AvatarView_unknownAvatar, false));
            setHighlighted(a.getBoolean(R.styleable.AvatarView_highlighted, false));
            setNameVisible(a.getBoolean(R.styleable.AvatarView_nameVisible, true));
        } finally {
            a.recycle();
        }
    }

    public void setPlayerName(String playerName){
        this.playerName = playerName;
        nameView.setText("[" + playerName + "]");
        invalidate();
        requestLayout();
    }

    public void setUnknownAvatar(boolean unknownAvatar){
        this.isUnknownAvatar = unknownAvatar;
        if(isUnknownAvatar){
            imageView.setImageResource(R.drawable.unknown_avatar);
        }else{
            imageView.setImageResource(R.drawable.avatar);
        }
        invalidate();
        requestLayout();
    }

    public void setHighlighted(boolean highlighted){
        this.isHighlighted = highlighted;
        if (isHighlighted) {
            outer.setBackground(getResources().getDrawable(R.drawable.green_border));
        }else{
            outer.setBackground(null);
        }
        invalidate();
        requestLayout();
    }

    public void setNameVisible(boolean nameVisible){
        this.isNameVisible = nameVisible;
        int visibility = isNameVisible ? View.VISIBLE : View.GONE;
        nameView.setVisibility(visibility);
        invalidate();
        requestLayout();
    }

    public void setTextColor(int color){
        nameView.setTextColor(color);
        invalidate();
        requestLayout();
    }

}
