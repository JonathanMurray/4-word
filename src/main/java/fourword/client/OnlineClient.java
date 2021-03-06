package fourword.client;

import fourword.Persistent;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.PlayerInfo;
import org.andengine.util.debug.Debug;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

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

    @Override
    public boolean isConnected(){
        return true; //TODO
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
                    Debug.e("Remote addr: " + socket.getRemoteSocketAddress());
                    fromServer = new ObjectInputStream(socket.getInputStream());
                    toServer = new ObjectOutputStream(socket.getOutputStream());
                    boolean connected = true;
                    while(connected){
                        Debug.e("Client waiting for msg from server ... ");
                        Msg<ServerMsg> msg = (Msg<ServerMsg>) fromServer.readObject();
                        Debug.e("   Received message: " + msg + ", id: " + msg.id());
                        switch (msg.type()){
                            case ONLINE_PLAYERS_INFO:
                                List<PlayerInfo> onlinePlayers = ((Msg.OnlinePlayersInfo)msg).get();
                                onlinePlayers.remove(Persistent.instance().playerName());
                                Persistent.instance().notifyOnlinePlayers(onlinePlayers);
                                Debug.e("Not delegating to listener. Handled by OnlineClient.");
                                break;
                            default:
                                delegateToListener(msg);
                                break;
                        }
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
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    close(fromServer);
                    close(toServer);
                }
            }
        }).start();
    }

    @Override
    public String getUserId() {
        return ""; //TODO
    }

    private void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Msg<ClientMsg> msg){
        try {
            toServer.writeObject(msg);
            Debug.e("   Sent msg: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
