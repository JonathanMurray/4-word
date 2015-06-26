package fourword;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsgListener;
import fourword.protocol.Client;
import fourword.protocol.OfflineClient;
import fourword.protocol.OnlineClient;

/**
 * Created by jonathan on 2015-06-25.
 */
public class Connection {

    private static final Connection INSTANCE = new Connection();
    public static Connection instance(){
        return INSTANCE;
    }

    private Client client;

    public void startOnline(ServerMsgListener listener, String serverAddress, int port){
        client = new OnlineClient(serverAddress, port);
        client.setMessageListener(listener);
        client.start();
    }

    public void startOffline(ServerMsgListener listener, int numAIs, int numCols, int numRows){
        client = new OfflineClient(numAIs, numCols, numRows);
        client.setMessageListener(listener);
        client.start();
    }


    public void setMessageListener(ServerMsgListener listener){
        client.setMessageListener(listener);
    }

    public void removeMessageListener(){
        client.removeMessageListener();
    }
    public void sendMessage(ClientMsg msg){
        client.sendMessage(msg);
    }

}
