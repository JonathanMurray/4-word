package fourword;

import org.andengine.util.SocketUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jonathan on 2015-06-23.
 */
public class GameServer {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java GameServer <port number>");
            System.exit(1);
        }

        printTitle();

        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Creating serverSocket on port " + portNumber);
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            System.out.println("Listening on port " + portNumber);
            System.out.println("InetAddress: " + serverSocket.getInetAddress());
            System.out.println("LocalPort: " + serverSocket.getLocalPort());
            System.out.println("LocalSocketAddress: " + serverSocket.getLocalSocketAddress());
            try(
                Socket clientSocket = serverSocket.accept();
            ) {
                System.out.println("Accepted socket!");
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                sendMessage(out, GameServerMessage.pickAndPlaceLetter());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                while(true){
                    System.out.println("Waiting for message from client ... ");
                    GameClientMessage msg = (GameClientMessage) in.readObject();
                    System.out.println("    Received message: '" + msg + "'");
                    switch(msg.action()){
                        case PICK_AND_PLACE_LETTER:
                            sendMessage(out, GameServerMessage.placeLetter('X'));
                            break;
                        case PLACE_LETTER:
                            sendMessage(out, GameServerMessage.pickAndPlaceLetter());
                            break;
                    }
                }


            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printTitle() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("--------------- GameServer ---------------");
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private static void sendMessage(ObjectOutputStream out, GameServerMessage msg) throws IOException {
        out.writeObject(msg);
        System.out.println("    Sent message: " + msg);
    }
}
