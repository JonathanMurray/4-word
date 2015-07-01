package fourword.messages;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MsgText<T extends MsgType> extends Msg{

    public final String text;

    public MsgText(T type, String text) {
        super(type);
        this.text = text;
    }

    public String toString(){
        return super.toString() + "(" + text + ")";
    }
}
