package fourword.messages;

/**
 * Created by jonathan on 2015-06-27.
 */
public enum ClientMsg implements MsgType {

    LOGIN,
    LOGOUT,
    CREATE_GAME,
    LEAVE_LOBBY,
    INVITE,
    ADD_BOT,
    KICK,
    JOIN,
    DECLINE,
    START_GAME,
    CLOSE_GAME,
    CONFIRM_GAME_STARTING,

    PLACE_LETTER,
    PICK_AND_PLACE_LETTER;
}
