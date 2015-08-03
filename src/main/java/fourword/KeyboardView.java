package fourword;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.fourword.R;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-07-26.
 */
public class KeyboardView extends LinearLayout implements View.OnClickListener{

    private KeyboardListener listener;
    private List<Button> buttons = new ArrayList<Button>();

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.keyboard, this, true);
    }

    @Override
    protected void onFinishInflate() {
        System.out.println("keyboard on finish inflate");
        super.onFinishInflate();
        LinearLayout keyboard = (LinearLayout) getChildAt(0);
        for(int rowIndex = 0; rowIndex < 3; rowIndex++){
            System.out.println("row");
            LinearLayout row = (LinearLayout) keyboard.getChildAt(rowIndex);
            if(row != null){
                System.out.println("Row is not null");
                for(int i = 0; i < row.getChildCount(); i++){
                    View v = row.getChildAt(i);
                    if(v instanceof Button){
                        Button button = (Button) v;
                        buttons.add(button);
                        button.setPadding(0,0,0,0);
                        button.setOnClickListener(this);
                    }
                }
            }
        }
    }

    public void setListener(KeyboardListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        char letter = button.getText().charAt(0);
        Debug.e("KeyboardView.onClick() letter: " + letter);
        if(listener != null){
            listener.onTypedLetter(letter);
        }
    }

    public void setEnabled(boolean enabled){
        for(Button button : buttons){
            button.setEnabled(enabled);
        }
    }

    public interface KeyboardListener{
        void onTypedLetter(char letter);
    }
}
