package fourword;

import fourword.client.Client;
import fourword.client.OfflineClient;
import fourword.client.OnlineClient;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-06-25.
 */
public class Connection {

    private static final int HEROKU_PORT = 80;

    private static final Connection INSTANCE = new Connection();
    public static Connection instance(){
        return INSTANCE;
    }

    private Client client;

    public void startLocalNetwork(MsgListener<ServerMsg> listener, String serverAddress, int port){
        client = new OnlineClient(serverAddress, port);
        client.setMessageListener(listener);
        client.start();
    }

    public void startHeroku(MsgListener<ServerMsg> listener, String serverAddress){
        client = new OnlineClient(serverAddress, HEROKU_PORT);
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
