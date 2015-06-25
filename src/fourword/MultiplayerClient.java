package fourword;

import org.andengine.util.debug.Debug;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jonathan on 2015-06-23.
 */
public class MultiplayerClient implements Client{

    private final int serverPort;
    private final String serverAddress;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private Listener listener;
    private Object listenerLock = new Object();

    private Queue<GameServerMessage> messageQueue = new LinkedList<GameServerMessage>();

    public MultiplayerClient(String serverAddress, int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void setMessageListener(Listener listener){
        synchronized (listenerLock){
            this.listener = listener;
            while(!messageQueue.isEmpty()){
                listener.handleServerMessage(messageQueue.remove());
            }
        }
    }

    @Override
    public void removeMessageListener() {
        synchronized (listenerLock){
            listener = null;
        }
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
                        try {
                            GameServerMessage msg = (GameServerMessage) fromServer.readObject();
                            Debug.e("   Received message: " + msg);
                            synchronized (listenerLock){
                                if(listener != null){
                                    listener.handleServerMessage(msg);
                                }else{
                                    messageQueue.add(msg);
                                }
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            connected = false;
                            Debug.e("not connected anymore");
                        }

                    }
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
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

    public void sendMessage(GameClientMessage msg){
        try {
            toServer.writeObject(msg);
            Debug.e("   Sent msg: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
