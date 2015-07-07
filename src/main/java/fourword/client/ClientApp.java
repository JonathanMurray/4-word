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
    public static void main(String[] args) {
        try {
            WebSocketClient s = new WebSocketClient(new URI("ws://play-1-test.herokuapp.com:80/")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("onopen");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("msg: " + message);
                }

                @Override
                public void onMessage(ByteBuffer bytes){
                    try {
                        Msg<ServerMsg> msg = objectFromBytes(bytes.array());
                        System.out.println("Received from server: " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("close: " + reason + "code: " +  code);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("error: " + ex);
                    ex.printStackTrace();
                }
            };
            s.connectBlocking();
            System.out.println("Sending msg to server");
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
