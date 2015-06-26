package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;
import fourword.protocol.Client;
import org.andengine.util.debug.Debug;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jonathan on 2015-06-23.
 */
public class OnlineClient extends Client {

    private final int serverPort;
    private final String serverAddress;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    public OnlineClient(String serverAddress, int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Debug.e("creating socket (address: " + serverAddress + ", port:" + serverPort + "):...");
                Socket socket = null;
                try{
                    socket = new Socket(serverAddress, serverPort);
                    Debug.e("success!");
                    fromServer = new ObjectInputStream(socket.getInputStream());
                    toServer = new ObjectOutputStream(socket.getOutputStream());
                    boolean connected = true;
                    while(connected){
                        Debug.e("Client waiting for msg from server ... ");
                        ServerMsg msg = (ServerMsg) fromServer.readObject();
                        Debug.e("   Received message: " + msg);
                        delegateToListener(msg);
                    }
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    close(socket);
                    close(fromServer);
                    close(toServer);
                }
            }
        }).start();
    }

    private void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ClientMsg msg){
        try {
            toServer.writeObject(msg);
            Debug.e("   Sent msg: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
