package com.example.android_test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import java.util.List;

/**
 * Created by jonathan on 2015-06-20.
 */
public class MyLayoutGameActivity extends SimpleLayoutGameActivity {
    private Font font;
    private Camera camera;
    private static final int NUM_COLS = 4;
    private static final int NUM_ROWS = 4;
    private boolean[][] lockedCells = new boolean[NUM_COLS][NUM_ROWS];
    private GridScene grid;
    private boolean hasEdited;
    private Cell editedCell;
    private boolean yourTurn = true;
    private UserAction receivedAction;

    @Override
    protected void onCreateResources() {
        font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 128, Color.WHITE);
        font.load();
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        grid = new GridScene(this, font, 250, NUM_COLS, NUM_ROWS, this.getVertexBufferObjectManager(), camera);
        lockCharAtCell('A', new Cell(0, 0));
        lockCharAtCell('X', new Cell(1, 1));
        lockCharAtCell('B', new Cell(1, 3));

        final EditText textInput = (EditText) findViewById(R.id.text_input);
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0 && grid.highlightedCell() && yourTurn){
                    char ch = Character.toUpperCase(s.charAt(s.length()-1));
                    if(Character.isLetter(ch)){
                        Cell highlighted = grid.getHighlighted();
                        boolean cellIsLocked = lockedCells[highlighted.x()][highlighted.y()];
                        if(!cellIsLocked){
                            if(hasEdited && editedCell != highlighted){
                                grid.removeCharAtCell(editedCell);
                            }
                            ((TextView)findViewById(R.id.title)).setText(s.toString());
                            grid.setCharAtCell(ch, highlighted);
                            editedCell = highlighted;
                            hasEdited = true;
                        }
                    }
                    s.clear();
                }
            }
        });

        grid.setGridTouchListener(new GridTouchListener() {
            @Override
            public void gridTouchEvent(Cell cell) {
                grid.highlightCell(cell);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textInput, InputMethodManager.SHOW_FORCED);
            }
        });



        grid.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if(!yourTurn && receivedAction != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            grid.setCharAtCell(receivedAction.letter(), receivedAction.cell());
                            ((TextView)findViewById(R.id.info_text)).setText("Make your move!");
                            ((Button)findViewById(R.id.doneButton)).setEnabled(true);
                            yourTurn = true;
                            receivedAction = null;
                        }
                    });
                }
            }

            @Override
            public void reset() {

            }
        });

        return grid;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.testlayout;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.surface_view;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, 1000, 1000);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth(), camera.getHeight()), camera);
    }


    private void lockCharAtCell(char ch, Cell cell){
        grid.setCharAtCell(ch, cell);
        lockedCells[cell.x()][cell.y()] = true;
    }

    public void clickedDone(View view){
        if(grid.hasCharAtCell(new Cell(0,0)) && grid.hasCharAtCell(new Cell(0,1)) && grid.hasCharAtCell(new Cell(0,2)) && grid.hasCharAtCell(new Cell(0,3))){
            String col = grid.getCol(0);
            List<String> words = new ScoreCalculator().extractWords(col);
            ((TextView)findViewById(R.id.title)).setText(words.toString());
        }
        if(hasEdited){
            lockCell(editedCell);
            hasEdited = false;
            yourTurn = false;
            ((TextView)findViewById(R.id.info_text)).setText("Opponent's turn ...");
            ((Button)findViewById(R.id.doneButton)).setEnabled(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        receivedAction = new AI().nextAction(grid);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void lockCell(Cell cell){
        lockedCells[cell.x()][cell.y()] = true;
    }

}
