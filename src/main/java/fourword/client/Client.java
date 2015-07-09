package fourword.client;


import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-24.
 */
public abstract class Client implements Serializable{
    public abstract void sendMessage(Msg<ClientMsg> msg) throws IOException;
    public abstract void start();

    private Queue<Msg<ServerMsg>> messageQueue = new LinkedList<Msg<ServerMsg>>();
    private Object listenerLock = new Object();
    private MsgListener listener;

    public void setMessageListener(MsgListener<ServerMsg> listener){
        synchronized (listenerLock){
            this.listener = listener;
            while(!messageQueue.isEmpty()){
                Debug.d("Client.setMessageListener -> process messageQueue");
                Msg msgInQueue = messageQueue.remove();
                Debug.d("Found in queue: " + msgInQueue);
                boolean handledByListener = listener.handleMessage(msgInQueue);
                if(!handledByListener){
                    throw new RuntimeException(msgInQueue + " not handled by " + listener);
                }
            }
        }
    }

    public void removeMessageListener() {
        synchronized (listenerLock){
            listener = null;
        }
    }

    protected void delegateToListener(Msg<ServerMsg> msg){
        synchronized (listenerLock){
            boolean handledByListener = false;
            if(listener != null){
                Debug.d("delegateToListener -> listener tries to handle it now");
                handledByListener = listener.handleMessage(msg);
            }

            if(!handledByListener){
                Debug.d("delegateToListener -> no listener (or it couldn't handle message right now), put in queue");
                messageQueue.add(msg);
            }
        }
    }

}
