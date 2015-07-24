package fourword.client;

import com.neovisionaries.ws.client.*;
import fourword.Persistent;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.ServerMsg;
import fourword_shared.model.PlayerInfo;
import org.andengine.util.debug.Debug;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Created by jonathan on 2015-07-08.
 */
public class WS_Client extends Client {

    private WebSocket websocket;
    private final String url;
    private boolean running;
    private final String userId;

    public WS_Client(final String url, String userId) {
        this.url = url;
        this.userId = userId;
    }

    private  static <T> T objectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        return (T) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }

    private static byte[] bytesFromObject(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(obj);
        return out.toByteArray();
    }

    @Override
    public void sendMessage(Msg<ClientMsg> msg) throws IOException{
        if(!running){
            throw new IOException("Socket hasn't been setup!");
        }
        websocket.sendBinary(bytesFromObject(msg));
        System.out.println("Sent : " + msg);
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String socketUrl = url + "/" + userId;
                    Debug.d("Creating socket with url: " + socketUrl);

                    websocket = new WebSocketFactory()
                            .createSocket(socketUrl)
                            .addListener(new WebSocketAdapter() {

                                @Override
                                public void onStateChanged(WebSocket websocket, WebSocketState newState) {
                                    super.onStateChanged(websocket, newState);
                                    System.out.println("new state: " + newState);
                                }

                                @Override
                                public void onError(WebSocket websocket, WebSocketException cause) {
                                    super.onError(websocket, cause);
                                    System.out.println("error cause: " + cause);
                                }

                                @Override
                                public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
                                    super.onUnexpectedError(websocket, cause);
                                    System.out.println("unexpected error cause: " + cause);
                                }


                                @Override
                                public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                                    try {
                                        Msg<ServerMsg> msg = objectFromBytes(binary);
                                        switch (msg.type()){
                                            case ONLINE_PLAYERS_INFO:
                                                List<PlayerInfo> onlinePlayers = ((Msg.OnlinePlayersInfo)msg).get();
                                                onlinePlayers.remove(Persistent.instance().playerName());
                                                Persistent.instance().notifyOnlinePlayers(onlinePlayers);
                                                Debug.e("Handled by OnlineClient: " + msg);
                                                break;
                                            case PLAYER_INFO_UPDATE:
                                                PlayerInfo info = ((Msg.PlayerInfoUpdate)msg).get();
                                                Persistent.instance().notifyPlayerInfo(info);
                                                Debug.e("Handled by OnlineClient: " + msg);
                                                break;
                                            case PLAYER_LOGGED_OUT:
                                                String name = ((Msg.PlayerLoggedOut)msg).get();
                                                Persistent.instance().notifyLoggedOut(name);
                                                Debug.e("Handled by OnlineClient: " + msg);
                                                break;
                                            default:
                                                delegateToListener(msg);
                                                break;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }

                                public void onTextMessage(WebSocket websocket, String message) {
                                    System.out.println("text from server: " + message);
                                }
                            }).connect();
                    running = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    running = false;
                } catch (WebSocketException e) {
                    e.printStackTrace();
                    running = false;
                }
            }
        }).start();
    }
}
