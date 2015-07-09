package fourword.client;



import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgText;
import fourword_shared.messages.ServerMsg;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.*;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Created by jonathan on 2015-07-06.
 */
public class ClientApp {

    public static final String HEROKU_URL = "ws://fourword-server.herokuapp.com:80/";
    public static final String LOCAL_URL = "ws://localhost:9000/ws";


    public static void main(String[] args) throws IOException, WebSocketException {






//        SocketIO socket = new SocketIO(LOCAL_URL);
//        socket.connect(new IOCallback() {
//            @Override
//            public void onMessage(JSONObject json, IOAcknowledge ack) {
//                try {
//                    System.out.println("Server said:" + json.toString(2));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onMessage(String data, IOAcknowledge ack) {
//                System.out.println("Server said: " + data);
//            }
//
//            @Override
//            public void onError(SocketIOException socketIOException) {
//                System.out.println("an Error occured");
//                socketIOException.printStackTrace();
//            }
//
//            @Override
//            public void onDisconnect() {
//                System.out.println("Connection terminated.");
//            }
//
//            @Override
//            public void onConnect() {
//                System.out.println("Connection established");
//            }
//
//            @Override
//            public void on(String event, IOAcknowledge ack, Object... args) {
//                System.out.println("Server triggered event '" + event + "'");
//            }
//        });
//
//        // This line is cached until the connection is establisched.
//        socket.send("Hello Server!");






    //            WebSocketClient s = new WebSocketClient(new URI(LOCAL_URL)) {
    //                @Override
    //                public void onOpen(ServerHandshake handshakedata) {
    //                    System.out.println(System.currentTimeMillis() +": onOpen " );
    //                    System.out.println("httpStatus: " + handshakedata.getHttpStatus());
    //                    System.out.println("httpStatusMsg: " + handshakedata.getHttpStatusMessage());
    //                    System.out.println("content: " + handshakedata.getContent());
    //
    //
    //                }
    //
    //                @Override
    //                public void onMessage(String message) {
    //                    System.out.println("onMessage: " + message);
    //                }
    //
    //                @Override
    //                public void onMessage(ByteBuffer bytes){
    //                    try {
    //                        Msg<ServerMsg> msg = objectFromBytes(bytes.array());
    //                        System.out.println("onMessageBytes: " + msg);
    //                    } catch (IOException e) {
    //                        e.printStackTrace();
    //                    } catch (ClassNotFoundException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //
    //                @Override
    //                public void onClose(int code, String reason, boolean remote) {
    //                    System.out.println(System.currentTimeMillis() + ": onClose: " + reason + ",code: " +  code + ", remote: " + remote);
    //                }
    //
    //                @Override
    //                public void onError(Exception ex) {
    //                    System.out.println("onError: " + ex);
    //                    ex.printStackTrace();
    //                }
    //            };
    //            boolean connected = s.connectBlocking();
    //            System.out.println(System.currentTimeMillis() + ": Connected to server: " + connected);
    //            Msg msg = new MsgText(ClientMsg.LOGIN, "Player-" + new Random().nextInt(1000));
    //            System.out.println("Sending msg to server: " + msg);
    //            System.out.println("remoteSocketAddress: " + s.getRemoteSocketAddress());
    //            s.send(bytesFromObject(msg));
    //            Thread.sleep(15000);
    //            s.send(bytesFromObject(new Msg(ClientMsg.LOGOUT)));





    //            URL localHttp = new URL("http://localhost:9000");
    //            System.out.println("opening conn...");
    //            URLConnection conn = localHttp.openConnection();
    //            System.out.println("done");
    //            BufferedReader in = new BufferedReader(
    //                    new InputStreamReader(
    //                            conn.getInputStream()));
    //            String inputLine;
    //            System.out.println("trying to print reply...");
    //            while ((inputLine = in.readLine()) != null)
    //                System.out.println(inputLine);
    //            in.close();
    //            System.out.println("done.");



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
