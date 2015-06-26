package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgPlaceLetter extends ServerMsg {
    public char letter;
    public String playerName;

    public MsgPlaceLetter(char letter, String playerName) {
        super(ServerMsgType.PLACE_LETTER);
        this.letter = letter;
        this.playerName = playerName;
    }
}
