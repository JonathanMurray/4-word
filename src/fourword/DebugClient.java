package fourword;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by jonathan on 2015-06-23.
 */
public class DebugClient {

    public static void main(String[] args) {

        int serverPort = Integer.parseInt(args[0]);


        String serverIP = "127.0.0.1";
        serverIP = "10.0.2.2";
        serverIP = "192.168.1.2";
        //serverIP = "127.0.0.1";

//        try {
//            System.out.println("creating socket...");
//            Socket socket = new Socket(serverIP, serverPort);
//            System.out.println("success!");
//            System.out.println("is connected : " + socket.isConnected());
//            System.out.println("socket inet addr: " + socket.getInetAddress());
//            System.out.println(socket.getInetAddress().toString());
//            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
//
//            System.out.println("Waiting for msg from server");
//            GameServerMessage msg = (GameServerMessage) fromServer.readObject();
//            System.out.println("Received msg from server: " + msg.getString());
//            System.out.println("sending to server...");
//            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
//            toServer.writeObject(new GameClientMessage());
//            System.out.println("Sent msg to server!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
