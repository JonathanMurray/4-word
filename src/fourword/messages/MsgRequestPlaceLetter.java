package fourword.messages;

/**
 * Created by jonathan on 2015-06-26.
 */
public class MsgRequestPlaceLetter extends Msg<ServerMsg> {
    public char letter;
    public String playerName;

    public MsgRequestPlaceLetter(char letter, String playerName) {
        super(ServerMsg.DO_PLACE_LETTER);
        this.letter = letter;
        this.playerName = playerName;
    }
}
