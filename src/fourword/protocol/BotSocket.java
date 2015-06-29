package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.messages.ServerMsg;
import fourword.model.GridModel;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jonathan on 2015-06-26.
 */
public class BotSocket extends PlayerSocket{

    private AI ai;
    private Msg<ClientMsg> replyFromAI;

    public BotSocket(AI ai, String name){
        super(name);
        this.ai = ai;
    }

    public void initializeWithGrid(GridModel grid){
        ai.initialize(grid);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void sendMessage(Msg<ServerMsg> msg) {
        System.out.println("   server-msg to " + getName() + ": " + msg);
        switch (msg.type()){
            case GAME_IS_STARTING:
                replyFromAI = new Msg(ClientMsg.CONFIRM_GAME_STARTING);
                break;
            case YOU_WERE_KICKED:
                //No reply needed. Just need to consume message
                break;
            default:
                replyFromAI = ai.handleServerMessageAndProduceReply(msg);
                break;
        }
    }

    @Override
    public Msg<ClientMsg> receiveMessage() {
        if(replyFromAI == null){
            throw new RuntimeException();
        }
        sleep(500);
        Msg<ClientMsg> msg = replyFromAI;
        System.out.println("Receive client-msg from " + getName() + ": " + msg);
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

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
