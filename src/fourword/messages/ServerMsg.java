package fourword.messages;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public abstract class ServerMsg implements Serializable{

    private final ServerMsgType type;

    public ServerMsg(ServerMsgType type){
        this.type = type;
    }

    public ServerMsgType type(){
        return type;
    }

}
