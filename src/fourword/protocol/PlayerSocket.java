package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.ServerMsg;

import java.net.InetAddress;

/**
 * Created by jonathan on 2015-06-26.
 */
public interface PlayerSocket{
    void sendMessage(ServerMsg msg);
    ClientMsg receiveMessage();
    void close();
    InetAddress getInetAddress();
    String getName();
}