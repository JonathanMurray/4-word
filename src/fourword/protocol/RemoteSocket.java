package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by jonathan on 2015-06-26.
 */
public class RemoteSocket implements PlayerSocket{

    private int counter = 0;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket clientSocket;
    private String name;
    public boolean pendingInvite;
    public String invitedBy;

    public static final int SOCKET_READ_TIMEOUT_MS = 200;

    public static RemoteSocket acceptSocket(ServerSocket serverSocket, int index) throws IOException {
        RemoteSocket socket = new RemoteSocket();
        socket.clientSocket = serverSocket.accept();
        socket.out = new ObjectOutputStream(socket.clientSocket.getOutputStream());
        socket.in = new ObjectInputStream(socket.clientSocket.getInputStream());
        socket.name = "Human_" + index;
        socket.clientSocket.setSoTimeout(SOCKET_READ_TIMEOUT_MS);
        return socket;
    }

    @Override
    synchronized public void sendMessage(Msg msg) throws IOException {
        msg.setId(counter);
        System.out.print("   server-msg to " + name + ": " + msg + " ... ");
        System.out.println("   id: " + counter);
        out.writeObject(msg);
        System.out.println("[x]");
        counter ++;
    }

    @Override
    public Msg<ClientMsg> receiveMessage() throws IOException {
        while(true){
            try {
                return receiveNotBlocking();
            } catch (SocketTimeoutException|ClassNotFoundException e) {
                sleep(500); // sleep and try again
            }
        }
    }

    synchronized public Msg<ClientMsg> receiveNotBlocking() throws IOException, ClassNotFoundException {
        Msg<ClientMsg> msg = (Msg<ClientMsg>) in.readObject();
        System.out.println("   client-msg from " + name + ": " + msg);
        return msg;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    synchronized public void close() {
        try {
            clientSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    synchronized public InetAddress getInetAddress() {
        return clientSocket.getInetAddress();
    }

    @Override
    synchronized public String getName() {
        return name;
    }

    synchronized public void setName(String name){
        this.name = name;
    }
}
