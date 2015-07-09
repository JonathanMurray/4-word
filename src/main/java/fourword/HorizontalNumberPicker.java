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
public class HorizontalNumberPicker extends LinearLayout {

    private String title;
    private int minValue;
    private int maxValue;
    private int value;
    private NumberPickerListener listener;

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HorizontalNumberPicker,
                0, 0);

        try {
            title = a.getString(R.styleable.HorizontalNumberPicker_title);
            value = a.getInt(R.styleable.HorizontalNumberPicker_value, 0);
            minValue = a.getInt(R.styleable.HorizontalNumberPicker_minValue, 0);
            maxValue = a.getInt(R.styleable.HorizontalNumberPicker_maxValue, 0);

        } finally {
            a.recycle();
        }

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.hor_number_picker, this, true);

        updateValue();
        updateTitle();

        ((Button)findViewById(R.id.numberpicker_plus)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        ((Button)findViewById(R.id.numberpicker_minus)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });
    }

    public int getValue(){
        return value;
    }

    private void updateTitle(){
        ((TextView)findViewById(R.id.numberpicker_title)).setText(title);
    }

    private void updateValue(){
        if(listener != null){
            listener.onNumberPickerChange(value);
        }
        ((TextView)findViewById(R.id.numberpicker_value)).setText("" + value);
        invalidate();
        requestLayout();
    }

    public void increase(){
        if(value + 1 <= maxValue){
            value ++;
            updateValue();
        }
    }

    public void decrease(){
        if(value - 1 >= minValue){
            value --;
            updateValue();
        }
    }

    public void setValueListener(NumberPickerListener listener){
        this.listener = listener;
    }

    public static interface NumberPickerListener {
        void onNumberPickerChange(int newValue);
    }


}
