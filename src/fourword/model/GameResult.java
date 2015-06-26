package fourword.model;

import fourword.model.GridModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-24.
 */
public class GameResult implements Serializable{
    private HashMap<String, GridModel> grids;

    public GameResult(HashMap<String, GridModel> grids){
        this.grids = grids;
    }

    public HashMap<String, GridModel> grids(){
        return grids;
    }
}
