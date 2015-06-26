package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jonathan on 2015-06-26.
 */
public class BotSocket implements PlayerSocket{

    private AI ai;
    private ClientMsg replyFromAI;
    private String name;

    public BotSocket(AI ai, int index){
        this.ai = ai;
        this.name = "Bot_" + index;
    }

    @Override
    public void sendMessage(ServerMsg msg) {
        replyFromAI = ai.handleServerMessageAndProduceReply(msg);
    }

    @Override
    public ClientMsg receiveMessage() {
        if(replyFromAI == null){
            throw new RuntimeException();
        }
        sleep(500);
        ClientMsg msg = replyFromAI;
        replyFromAI = null;
        return msg;
    }

    @Override
    public void close(){
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

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
