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

    public static final String DEFAULT_IP = "192.168.1.2";

    public boolean connectDefault(MsgListener<ServerMsg> listener){
        return connect(listener, DEFAULT_IP);
    }

    public boolean reconnectDefault(MsgListener<ServerMsg> listener){
        return connect(listener, DEFAULT_IP, client.getUserId());
    }

    public boolean connect(MsgListener<ServerMsg> listener, String ip){
        String userId = "id_" + new Random().nextLong();
        return connect(listener, ip, userId);
    }

    public boolean connect(MsgListener<ServerMsg> listener, String ip, String userId){
        client = new WS_Client("ws://" + ip + ":9000", userId);
        client.setMessageListener(listener);
        client.start();
        return client.isConnected();
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
    public void sendMessage(Msg<ClientMsg> msg) throws IOException{
        client.sendMessage(msg);
    }

}
