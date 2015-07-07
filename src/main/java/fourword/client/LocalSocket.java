package fourword.client;

import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.Msg;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.GridModel;
import fourword.server.PlayerSocket;
import org.andengine.util.debug.Debug;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-26.
 */
public class LocalSocket extends PlayerSocket implements MsgListener<ClientMsg> {

    private final MsgListener<ServerMsg> serverMsgListener;
    private final Queue<Msg<ClientMsg>> clientMsgQueue = new LinkedList<Msg<ClientMsg>>();

    public LocalSocket(String name, MsgListener<ServerMsg> serverMsgListener){
        super(name);
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
    public void initializeWithGrid(GridModel grid) {
        //DO nothing
    }

    @Override
    public boolean isRemote() {
        return false;
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