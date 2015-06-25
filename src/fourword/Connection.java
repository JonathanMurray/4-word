package fourword;

/**
 * Created by jonathan on 2015-06-25.
 */
public class Connection {

    private static final Connection INSTANCE = new Connection();
    public static Connection instance(){
        return INSTANCE;
    }

    private MultiplayerClient client;

    public void start(Client.Listener listener, String serverAddress, int port){
        client = new MultiplayerClient(serverAddress, port);
        client.setMessageListener(listener);
        client.start();
    }

    public void setMessageListener(Client.Listener listener){
        client.setMessageListener(listener);
    }

    public void removeMessageListener(){
        client.removeMessageListener();
    }
    public void sendMessage(GameClientMessage msg){
        client.sendMessage(msg);
    }

}
