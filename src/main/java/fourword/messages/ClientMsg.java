package fourword.messages;

/**
 * Created by jonathan on 2015-06-27.
 */
public enum ClientMsg implements MsgType {

    LOGIN,
    LOGOUT,

    CREATE_LOBBY,
    LEAVE_LOBBY,
    INVITE_TO_LOBBY,
    ADD_BOT_TO_LOBBY,
    KICK_FROM_LOBBY,
    ACCEPT_INVITE,
    DECLINE_INVITE,
    START_GAME_FROM_LOBBY,
    LOBBY_SET_DIMENSIONS,
    LEAVE_GAME,
    CONFIRM_GAME_STARTING,

    QUICK_START_GAME,

    PLACE_LETTER,
    PICK_AND_PLACE_LETTER;
}
