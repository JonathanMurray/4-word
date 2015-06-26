package fourword.messages;

import fourword.messages.ClientMsg;

/**
 * Created by jonathan on 2015-06-26.
 */
public interface ClientMsgListener {
    void handleClientMessage(ClientMsg msg);
}
