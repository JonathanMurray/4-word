package fourword.messages;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-26.
 */
public interface MsgListener<T extends MsgType> extends Serializable {
    public boolean handleMessage(Msg<T> msg);
}
