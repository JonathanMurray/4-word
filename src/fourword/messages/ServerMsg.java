package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public enum ServerMsg implements MsgType {
    OK,
    NO,
    YOU_ARE_INVITED,
    YOU_WERE_KICKED,

    DO_PLACE_LETTER,
    DO_PICK_AND_PLACE_LETTER,
    GAME_FINISHED,
    LOBBY_STATE,
    GAME_IS_STARTING,
    WAITING_FOR_PLAYER_MOVE,
    ONLINE_PLAYERS;
}
