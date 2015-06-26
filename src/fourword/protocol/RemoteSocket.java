package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jonathan on 2015-06-26.
 */
public class RemoteSocket implements PlayerSocket{

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket clientSocket;
    private String name;

    public static RemoteSocket acceptSocket(ServerSocket serverSocket, int index){
        try {
            RemoteSocket socket = new RemoteSocket();
            socket.clientSocket = serverSocket.accept();
            socket.out = new ObjectOutputStream(socket.clientSocket.getOutputStream());
            socket.in = new ObjectInputStream(socket.clientSocket.getInputStream());
            socket.name = "Human_" + index;
            return socket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(ServerMsg msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ClientMsg receiveMessage() {
        try {
            return (ClientMsg) in.readObject();
        } catch (ClassNotFoundException|IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            clientSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetAddress getInetAddress() {
        return clientSocket.getInetAddress();
    }

    @Override
    public String getName() {
        return name;
    }
}
