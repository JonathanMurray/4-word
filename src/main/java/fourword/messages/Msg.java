package fourword.messages;

import java.io.Serializable;

/**
 * Created by jonathan on 2015-06-23.
 */
public class Msg<T extends MsgType> implements Serializable{

    private int id;
    private final T type;

    public Msg(T type){
        this.type = type;
    }

    public void setId(int id){
        this.id = id;
    }

    public T type(){
        return type;
    }

    public int id(){
        return id;
    }

    public String toString(){
        return type.toString();
    }

}
