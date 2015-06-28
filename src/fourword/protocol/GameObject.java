package fourword.protocol;

import fourword.model.GridModel;

import java.util.ArrayList;

/**
 * Created by jonathan on 2015-06-27.
 */
public class GameObject {
    private int numPlayers;
    private String host;
    public ArrayList<PlayerSocket> playerSockets = new ArrayList<>();
    public ArrayList<GridModel> grids = new ArrayList<>();
    private final int numCols;
    private final int numRows;
    private boolean hasFinished = false;


    public GameObject(int numPlayers, String host, int numCols, int numRows){
        this.numPlayers = numPlayers;
        this.host = host;
        this.numCols = numCols;
        this.numRows = numRows;
    }

    public void join(PlayerSocket socket){
        playerSockets.add(socket);
        GridModel grid = new GridModel(numCols, numRows);
        grids.add(grid);
        socket.initializeWithGrid(grid);
    }

    public boolean isReadyToStart(){
        return playerSockets.size() == numPlayers;
    }

    public boolean hasFinished(){
        return hasFinished;
    }

    public void setFinished(){
        hasFinished = true;
    }
}
