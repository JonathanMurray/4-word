package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.MsgListener;
import fourword.messages.Msg;
import fourword.messages.ServerMsg;
import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.util.debug.Debug;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-26.
 */
public class LocalSocket implements PlayerSocket, MsgListener<ClientMsg> {

    private final String name;
    private final MsgListener<ServerMsg> serverMsgListener;
    private final Queue<Msg<ClientMsg>> clientMsgQueue = new LinkedList<Msg<ClientMsg>>();

    public LocalSocket(String name, MsgListener<ServerMsg> serverMsgListener){
        this.name = name;
        this.serverMsgListener = serverMsgListener;
    }

    @Override
    public void sendMessage(Msg msg) {
        Debug.d("LocalSocket.sendMessage(" + msg + ")");
        serverMsgListener.handleMessage(msg);
    }

    @Override
    public Msg<ClientMsg> receiveMessage() {
        while(clientMsgQueue.isEmpty()){
            sleep(500);
        }
        Msg<ClientMsg> msg = clientMsgQueue.remove();
        Debug.d("LocalSocket.receiveMessage(" + msg + ")");
        return msg;
    }

    @Override
    public void close() {
       //Do nothing
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean handleMessage(Msg<ClientMsg> msg) {
        clientMsgQueue.add(msg);
        return true;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}