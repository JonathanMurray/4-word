package fourword;

import org.andengine.util.SocketUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jonathan on 2015-06-23.
 */
public class AI_Server {

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java AI_Server <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        new AI_Server(new AI(), 4, 4).run(portNumber);
    }

    private final int numCols;
    private final int numRows;
    private AI ai;


    public AI_Server(AI ai, int numCols, int numRows){
        this.ai = ai;
        ai.initialize(new GridModel(numCols, numRows));
        this.numCols = numCols;
        this.numRows = numRows;
    }

    public void run(int portNumber){
        printTitle();

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
                communicationLoop(in, out);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void communicationLoop(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        while(true){
            System.out.println("Waiting for message from client ... ");
            GameClientMessage msg = (GameClientMessage) in.readObject();
            System.out.println("    Received message: '" + msg + "'");
            switch(msg.action()){
                case PICK_AND_PLACE_LETTER:
                    ai.placeLetter(msg.letter());
                    char pickedLetter = ai.pickAndPlaceLetter();
                    sendMessage(out, GameServerMessage.placeLetter(pickedLetter, ai.getPlayerName()));
                    break;
                case PLACE_LETTER:
                    sendMessage(out, GameServerMessage.pickAndPlaceLetter());
                    break;
            }
        }
    }

    private void printTitle() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("--------------- AI_Server ---------------");
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private void sendMessage(ObjectOutputStream out, GameServerMessage msg) throws IOException {
        out.writeObject(msg);
        System.out.println("    Sent message: " + msg);
    }
}
