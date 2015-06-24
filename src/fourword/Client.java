package fourword;


/**
 * Created by jonathan on 2015-06-24.
 */
public interface Client {
    public void sendMessage(GameClientMessage msg);
    public void setMessageListener(Listener listener);
    public void start();

    public static interface Listener{
        public void handleServerMessage(GameServerMessage msg);
    }
}
