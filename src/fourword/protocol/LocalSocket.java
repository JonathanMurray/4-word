package fourword.protocol;

import fourword.messages.ClientMsgListener;
import fourword.messages.ServerMsgListener;
import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;
import org.andengine.util.debug.Debug;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-26.
 */
public class LocalSocket implements PlayerSocket, ClientMsgListener {

    private final String name;
    private final ServerMsgListener serverMsgListener;
    private final Queue<ClientMsg> clientMsgQueue = new LinkedList<ClientMsg>();

    public LocalSocket(String name, ServerMsgListener serverMsgListener){
        this.name = name;
        this.serverMsgListener = serverMsgListener;
    }

    @Override
    public void sendMessage(ServerMsg msg) {
        Debug.d("LocalSocket.sendMessage(" + msg + ")");
        serverMsgListener.handleServerMessage(msg);
    }

    @Override
    public ClientMsg receiveMessage() {
        while(clientMsgQueue.isEmpty()){
            sleep(500);
        }
        ClientMsg msg = clientMsgQueue.remove();
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
    public void handleClientMessage(ClientMsg msg) {
        clientMsgQueue.add(msg);
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}