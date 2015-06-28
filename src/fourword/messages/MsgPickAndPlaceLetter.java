package fourword.messages;

import fourword.model.Cell;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MsgPickAndPlaceLetter extends Msg<ClientMsg> {

    public final char letter;
    public final Cell cell;

    public MsgPickAndPlaceLetter(char letter, Cell cell) {
        super(ClientMsg.PICK_AND_PLACE_LETTER);
        this.letter = 0;
        this.cell = null;
    }
}
