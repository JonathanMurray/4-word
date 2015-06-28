package fourword.messages;

import fourword.model.Cell;
import fourword.protocol.Client;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MsgPlaceLetter extends Msg<ClientMsg> {

    public final Cell cell;

    public MsgPlaceLetter(Cell cell) {
        super(ClientMsg.PLACE_LETTER);
        this.cell = cell;
    }
}
