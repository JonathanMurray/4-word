package fourword;

import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameClient implements Client{

    private int serverPort;
    private String serverAddress;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private Listener listener;

    public GameClient(String serverAddress, int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void setMessageListener(Listener listener){
        this.listener = listener;
    }

    public void start(){
        try {
            Debug.e("creating socket...");
            Socket socket = new Socket(serverAddress, serverPort);
            Debug.e("success!");
            fromServer = new ObjectInputStream(socket.getInputStream());
            toServer = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean connected = true;
                    while(connected){
                        Debug.e("Client waiting for msg from server ... ");
                        try {
                            GameServerMessage msg = (GameServerMessage) fromServer.readObject();
                            Debug.e("   Received message: " + msg);
                            listener.handleServerMessage(msg);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            connected = false;
                            Debug.e("not connected anymore");
                        }

                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(GameClientMessage msg){
        try {
            toServer.writeObject(msg);
            Debug.e("   Sent msg: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
