package fourword.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jonathan on 2015-06-24.
 */
public class GameResult implements Serializable{
    private HashMap<String, GridModel> grids;
    private HashSet<String> lowerWords;

    public GameResult(HashMap<String, GridModel> grids, HashSet<String> lowerWords){
        this.grids = grids;
        this.lowerWords = lowerWords;
    }

    public HashMap<String, GridModel> grids(){
        return grids;
    }

    public HashSet<String> lowerWords(){
        return lowerWords;
    }

    public String toString(){
        return lowerWords.toString();
    }
}
