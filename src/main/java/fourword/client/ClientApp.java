package fourword.client;



import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.ServerMsg;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;


/**
 * Created by jonathan on 2015-07-06.
 */
public class ClientApp {

    public static final String HEROKU_URL = "ws://fourword-server.herokuapp.com:80/";
    public static final String LOCAL_URL = "ws://192.168.1.2:9000/";


    public static void main(String[] args) {
        try {
            WebSocketClient s = new WebSocketClient(new URI(LOCAL_URL)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("onOpen: " + handshakedata);
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("onMessage: " + message);
                }

                @Override
                public void onMessage(ByteBuffer bytes){
                    try {
                        Msg<ServerMsg> msg = objectFromBytes(bytes.array());
                        System.out.println("onMessage: " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("onClose: " + reason + ",code: " +  code);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("onError: " + ex);
                    ex.printStackTrace();
                }
            };
            boolean connected = s.connectBlocking();
            System.out.println("Connected to server: " + connected);
            System.out.println("Sending msg to server");
            System.out.println(s.getRemoteSocketAddress());
            s.send(bytesFromObject(new Msg(ClientMsg.LOGIN)));
        }  catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  static <T> T objectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        return (T) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }

    private static byte[] bytesFromObject(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(obj);
        return out.toByteArray();
    }
}
