package fourword.protocol;


import fourword.messages.ServerMsgListener;
import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;
import org.andengine.util.debug.Debug;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-24.
 */
public abstract class Client implements Serializable{
    public abstract void sendMessage(ClientMsg msg);
    public abstract void start();

    private Queue<ServerMsg> messageQueue = new LinkedList<ServerMsg>();
    private Object listenerLock = new Object();
    private ServerMsgListener listener;

    public void setMessageListener(ServerMsgListener listener){
        synchronized (listenerLock){
            this.listener = listener;
            while(!messageQueue.isEmpty()){
                Debug.d("Client.setMessageListener -> process messageQueue");
                ServerMsg msgInQueue = messageQueue.remove();
                Debug.d("Found in queue: " + msgInQueue);
                boolean handledByListener = listener.handleServerMessage(msgInQueue);
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

    protected void delegateToListener(ServerMsg msg){
        synchronized (listenerLock){
            boolean handledByListener = false;
            if(listener != null){
                Debug.d("delegateToListener -> listener tries to handle it now");
                handledByListener = listener.handleServerMessage(msg);
            }

            if(!handledByListener){
                Debug.d("delegateToListener -> no listener (or it couldn't handle message right now), put in queue");
                messageQueue.add(msg);
            }
        }
    }

}
