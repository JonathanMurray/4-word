package old;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jonathan on 2015-06-15.
 */
public class LetterGrid extends View {
    private Paint thinPaint = new Paint();
    private Paint thickPaint = new Paint();
    private Paint textPaint = new Paint();
    private final int SCREEN_WIDTH = 1024;
    private final int SCREEN_HEIGHT = 1024;
    private final int WIDTH = 8;
    private final int HEIGHT = 8;
    private final int CELL_SIZE = SCREEN_WIDTH / WIDTH;
    private char[][] cells = new char[WIDTH][HEIGHT];

    public LetterGrid(Context context) {
        super(context);
        thinPaint.setStyle(Paint.Style.STROKE);
        thinPaint.setColor(Color.WHITE);
        thinPaint.setStrokeWidth(4);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(80);
        textPaint.setColor(Color.WHITE);
        thickPaint.setStyle(Paint.Style.FILL);
        thickPaint.setColor(Color.BLACK);
        for(int w = 0; w < WIDTH; w ++){
            for(int h = 0; h < HEIGHT; h ++){
                cells[w][h] = '0';
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawRect(0,0,SCREEN_WIDTH, SCREEN_HEIGHT, thickPaint);
        for(int x = 0; x <= SCREEN_WIDTH; x += CELL_SIZE){
            canvas.drawLine(x, 0, x, SCREEN_HEIGHT, thinPaint);
        }
        for(int y = 0; y <= SCREEN_HEIGHT; y += CELL_SIZE){
            canvas.drawLine(0, y, SCREEN_WIDTH, y, thinPaint);
        }
        for(int w = 0; w < WIDTH; w ++){
            for(int h = 0; h < HEIGHT; h ++){
                char ch = cells[w][h];
                canvas.drawText("" + ch, (float) ((w+0.25)*CELL_SIZE), (float) ((h+0.75)*CELL_SIZE), textPaint);
            }
        }
    }

    public void setChar(char ch, int xCell, int yCell){
        cells[xCell][yCell] = ch;
        invalidate();
    }

    public void setCharAtCoords(char ch, int x, int y){
        int xCell = x / CELL_SIZE;
        int yCell = y / CELL_SIZE;
        setChar(ch, xCell, yCell);
    }
}
