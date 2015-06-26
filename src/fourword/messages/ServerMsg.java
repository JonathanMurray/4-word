package fourword.messages;

import fourword.GameResult;

import java.io.Serializable;
import java.util.List;

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
