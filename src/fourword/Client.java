package fourword;


import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-24.
 */
public interface Client extends Serializable{
    public void sendMessage(ClientMsg msg);
    public void setMessageListener(Listener listener);
    public void removeMessageListener();
    public void start();

    public static interface Listener extends Serializable{
        public void handleServerMessage(ServerMsg msg);
    }
}
