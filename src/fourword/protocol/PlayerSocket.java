package fourword.protocol;

import fourword.messages.ClientMsg;
import fourword.messages.Msg;
import fourword.messages.ServerMsg;

import java.io.IOException;
import java.io.OptionalDataException;
import java.net.InetAddress;

/**
 * Created by jonathan on 2015-06-26.
 */
public interface PlayerSocket{
    void sendMessage(Msg<ServerMsg> msg) throws IOException;
    Msg<ClientMsg> receiveMessage() throws IOException;
    void close();
    InetAddress getInetAddress();
    String getName();
}