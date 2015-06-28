package fourword.messages;

/**
 * Created by jonathan on 2015-06-27.
 */
public enum ClientMsg implements MsgType {

    LOGIN,
    CREATE_GAME,
    INVITE,
    KICK,
    JOIN,
    DECLINE,
    START_GAME,
    CLOSE_GAME,

    PLACE_LETTER,
    PICK_AND_PLACE_LETTER;

//    public static Class<?> getClass(ClientMsg type){
//        switch(type){
//            case LOGIN:
//                return Msg.class;
//        }
//    }
}
