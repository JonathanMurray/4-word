package fourword.server;

import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.GridModel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by jonathan on 2015-06-26.
 */
public class BotSocket extends PlayerSocket {

    private AI ai;
    private Msg<ClientMsg> replyFromAI;
    private static final int MIN_SLEEP = 500;
    private static final int MAX_SLEEP = 2500;

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
    public synchronized void sendMessage(Msg<ServerMsg> msg) {
        System.out.println("(TO " + getName() + ": " + msg + ")");
        switch (msg.type()){
            case GAME_IS_STARTING:
//                replyFromAI = new Msg(ClientMsg.CONFIRM_GAME_STARTING);
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
    public synchronized Msg<ClientMsg> receiveMessage() {
        if(replyFromAI == null){
            throw new RuntimeException();
        }
        sleep(MIN_SLEEP + new Random().nextInt(MAX_SLEEP-MIN_SLEEP));
        Msg<ClientMsg> msg = replyFromAI;
        System.out.println("(FROM " + getName() + ": " + msg + ")");
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
