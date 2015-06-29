package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.model.GridModel;

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
public class RemoteSocket extends PlayerSocket {

    private int counter = 0;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket clientSocket;

    public static final int SOCKET_READ_TIMEOUT_MS = 500;

    public RemoteSocket(String name) {
        super(name);
    }

    @Override
    public void initializeWithGrid(GridModel grid) {
        //Do nothing
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    public static RemoteSocket acceptSocket(ServerSocket serverSocket, int index) throws IOException {
        RemoteSocket socket = new RemoteSocket("Human_" + index);
        socket.clientSocket = serverSocket.accept();
        socket.out = new ObjectOutputStream(socket.clientSocket.getOutputStream());
        socket.in = new ObjectInputStream(socket.clientSocket.getInputStream());
        socket.clientSocket.setSoTimeout(SOCKET_READ_TIMEOUT_MS);
        return socket;
    }

    @Override
    synchronized public void sendMessage(Msg msg) throws IOException {
        msg.setId(counter);
        System.out.print("   server-msg to " + getName() + ": " + msg + " ... ");
        System.out.println("   id: " + counter);
        out.writeObject(msg);
        System.out.println("[x]");
        counter ++;
    }

    @Override
    public Msg<ClientMsg> receiveMessage() throws IOException, ClassNotFoundException {
        while(true){
            try {
                return receiveNotBlocking();
            } catch (SocketTimeoutException e) {
                sleep(500); // sleep and try again
            } catch(ClassCastException e){
                e.printStackTrace();
                System.out.println("out: " + out);
                System.out.println("in: " + in);
                System.out.println("clientSocket: " + clientSocket);
                System.out.println("counter: " + counter);
                System.out.println("name: " + getName());
                System.out.println("lobby: " + getLobby());
                System.out.println("invitedBy: " + getInvitedBy());
            }
        }
    }

    synchronized private Msg<ClientMsg> receiveNotBlocking() throws IOException, ClassNotFoundException {
        Msg<ClientMsg> msg = (Msg<ClientMsg>) in.readObject();
        System.out.println("   client-msg from " + getName() + ": " + msg);
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

    public String toString(){
        return "RemoteSocket{" + getName() + ", " + clientSocket + (isInvited()? ", " + getInvitedBy() : "") + "}";
    }

}
