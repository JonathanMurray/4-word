package fourword;

import fourword.client.Client;
import fourword.client.WS_Client;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.Random;

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

    public void startLocalNetwork(MsgListener<ServerMsg> listener){

        String userId = "id_" + new Random().nextLong();
        client = new WS_Client("ws://192.168.1.2:9000", userId);
        client.setMessageListener(listener);
        client.start();
    }

    public void startHeroku(MsgListener<ServerMsg> listener, String serverAddress){
        String userId = "id_" + new Random().nextLong();
        client = new WS_Client(serverAddress, userId);
        client.setMessageListener(listener);
        client.start();
    }

//    public void startOffline(MsgListener<ServerMsg> listener, int numAIs, int numCols, int numRows){
//        client = new OfflineClient(numAIs, numCols, numRows);
//        client.setMessageListener(listener);
//        client.start();
//    }


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
    public void sendMessage(Msg<ClientMsg> msg) throws IOException{
        client.sendMessage(msg);
    }

}
