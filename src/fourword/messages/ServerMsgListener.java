package fourword.messages;

import fourword.messages.ServerMsg;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-26.
 */
public interface ServerMsgListener extends Serializable {
    public boolean handleServerMessage(ServerMsg msg);
}
