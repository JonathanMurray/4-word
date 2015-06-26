package fourword;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.MsgGameIsStarting;
import fourword.messages.ServerMsg;
import fourword.messages.ServerMsgListener;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends Activity implements ServerMsgListener {

    private static final int SERVER_PORT = 4444;
    private static final String SERVER_IP = "192.168.1.2";

    @Override
    protected void onResume() {
        super.onResume();
        Debug.d("LobbyActivity.onResume() /Jonathan");
        setContentView(R.layout.lobby);
//        Connection.instance().startOnline(this, SERVER_IP, SERVER_PORT);
        Connection.instance().startOffline(this, 2, 2, 3);
    }

    @Override
    public boolean handleServerMessage(ServerMsg msg) {
        Debug.d("Lobby-client received msg from server: " + msg);
        switch (msg.type()){
            case GAME_IS_STARTING:
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("NUM_COLS", ((MsgGameIsStarting)msg).numCols);
                intent.putExtra("NUM_ROWS", ((MsgGameIsStarting)msg).numRows);
                Connection.instance().removeMessageListener();
                startActivity(intent);
                return true;
            case LOBBY_WAITING_FOR_MORE_PLAYERS:
                ((TextView)findViewById(R.id.lobby_info_text)).setText("Waiting for more players ...");
                return true;
            default:
                //It may happen that server sends game-related messages even though we are still in the lobby.
                //This is our way of saying that the message should be sent later to a new listener.
                return false;
        }
    }
}
