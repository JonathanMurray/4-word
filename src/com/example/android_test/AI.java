package com.example.android_test;

/**
 * Created by jonathan on 2015-06-22.
 */
public class AI {

    int numCols = 4;
    int numRows = 4;

    public UserAction nextAction(GridScene grid){
        for(int x = 0; x < numCols; x++){
            for(int y = 0; y < numRows; y++){
                Cell cell = new Cell(x,y);
                if(!grid.hasCharAtCell(cell)){
                    return new UserAction(cell, 'Y');
                }
            }
        }
        throw new IllegalStateException("No possible action! Grid is full.");
    }
}
