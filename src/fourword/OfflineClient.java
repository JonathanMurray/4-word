package fourword;

import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-24.
 */
public class OfflineClient implements Client {

    private final AI_ServerBehaviour aiServerBehaviour;
    private Listener listener;

    public OfflineClient(AI_ServerBehaviour aiServerBehaviour){
        this.aiServerBehaviour = aiServerBehaviour;
    }

    @Override
    public void sendMessage(final GameClientMessage msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Debug.d("   to mockup-server: " + msg);
                GameServerMessage reply = aiServerBehaviour.act(msg);
                Debug.d("   from mockup-server: " + reply);
                Debug.d("   Server grid: ");
                Debug.d(aiServerBehaviour.ai().toString());
                listener.handleServerMessage(reply);
            }
        }).start();
    }

    @Override
    public void start(){
        Debug.e("Starting offline client ...");
        GameServerMessage msg = GameServerMessage.pickAndPlaceLetter();
        Debug.d("   from mockup-server: " + msg);
        listener.handleServerMessage(msg);
    }

    @Override
    public void setMessageListener(Listener listener) {
        this.listener = listener;
    }
}
