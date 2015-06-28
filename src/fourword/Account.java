package fourword;

/**
 * Created by jonathan on 2015-06-28.
 */
public class Account {
    private static Account INSTANCE = new Account();
    public static Account instance(){
        return INSTANCE;
    }

    private String playerName;

    public void init(String playerName){
        this.playerName = playerName;
    }

    public String playerName(){
        return playerName;
    }
}
