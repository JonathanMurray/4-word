package fourword;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.messages.MsgListener;
import fourword.messages.ServerMsg;
import fourword.client.Client;
import fourword.client.OfflineClient;
import fourword.client.OnlineClient;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-06-25.
 */
public class Connection {

    private static final Connection INSTANCE = new Connection();
    public static Connection instance(){
        return INSTANCE;
    }

    private Client client;

    public void startOnline(MsgListener<ServerMsg> listener, String serverAddress, int port){
        client = new OnlineClient(serverAddress, port);
        client.setMessageListener(listener);
        client.start();
    }

    public void startOffline(MsgListener<ServerMsg> listener, int numAIs, int numCols, int numRows){
        client = new OfflineClient(numAIs, numCols, numRows);
        client.setMessageListener(listener);
        client.start();
    }


    public void setMessageListener(MsgListener<ServerMsg> listener){
        if(client == null){
            Debug.e("Connection.setMessageListener(), but client has not been initialized yet!");
        }else{
            client.setMessageListener(listener);
        }

    }

    public void removeMessageListener(){
        client.removeMessageListener();
    }
    public void sendMessage(Msg<ClientMsg> msg){
        client.sendMessage(msg);
    }

}