package fourword;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.android_test.R;

/**
 * Created by jonathan on 2015-07-03.
 */
public class NumberPickerView extends LinearLayout {

    private String title;
    private int minValue;
    private int maxValue;
    private int value;
    private NumberPickerListener listener;
    private Button minusButton;
    private Button plusButton;
    private boolean enabled = true;

    public NumberPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NumberPickerView,
                0, 0);

        try {
            title = a.getString(R.styleable.NumberPickerView_title);
            value = a.getInt(R.styleable.NumberPickerView_value, 0);
            minValue = a.getInt(R.styleable.NumberPickerView_minValue, 0);
            maxValue = a.getInt(R.styleable.NumberPickerView_maxValue, 0);
        } finally {
            a.recycle();
        }

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.hor_number_picker, this, true);

        minusButton = ((Button)findViewById(R.id.numberpicker_minus));
        plusButton = ((Button)findViewById(R.id.numberpicker_plus));

        updateLayout();
        updateTitle();

        minusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });
        plusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        if(enabled){
            updateLayout();
        }else{
            minusButton.setEnabled(false);
            plusButton.setEnabled(false);
        }
    }



    public int getValue(){
        return value;
    }

    private void updateTitle(){
        ((TextView)findViewById(R.id.numberpicker_title)).setText(title);
    }

    private void updateLayout(){
        ((TextView)findViewById(R.id.numberpicker_value)).setText("" + value);
        if(enabled){ //whole component disabled? ==> don't set individual buttons
            minusButton.setEnabled(true);
            plusButton.setEnabled(true);
            if(value == minValue){
                minusButton.setEnabled(false);
            }else if(value == maxValue){
                plusButton.setEnabled(false);
            }
        }
        invalidate();
        requestLayout();
    }

    private void increase(){
        if(value + 1 <= maxValue){
            value ++;
            if(listener != null){
                listener.onUserPickedNumber(this, value);
            }
            updateLayout();
        }
    }

    private void decrease(){
        if(value - 1 >= minValue){
            value --;
            if(listener != null){
                listener.onUserPickedNumber(this, value);
            }
            updateLayout();
        }
    }

    public void setValue(int value){
        this.value = value;
        updateLayout();
    }

    public void setClickedChangeListener(NumberPickerListener listener){
        this.listener = listener;
    }

    public interface NumberPickerListener {
        void onUserPickedNumber(View view, int newValue);
    }


}
