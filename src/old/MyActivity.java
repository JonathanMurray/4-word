package old;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import old.LetterGrid;

public class MyActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LetterGrid letterGrid = new LetterGrid(this);
        setContentView(letterGrid);
        letterGrid.setChar('A', 0, 0);
        letterGrid.setChar('B', 1, 0);
        letterGrid.setChar('C', 2, 0);
        letterGrid.setChar('D', 3, 0);
        letterGrid.invalidate();

        letterGrid.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                letterGrid.setCharAtCoords('X', x, y);
                return true;
            }
        });
    }
}
