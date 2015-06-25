package fourword;


import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-24.
 */
public interface Client extends Serializable{
    public void sendMessage(GameClientMessage msg);
    public void setMessageListener(Listener listener);
    public void removeMessageListener();
    public void start();

    public static interface Listener extends Serializable{
        public void handleServerMessage(GameServerMessage msg);
    }
}
