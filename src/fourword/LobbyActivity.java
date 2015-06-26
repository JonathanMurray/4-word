package fourword;

import android.app.Activity;
import android.content.Intent;
import com.example.android_test.R;
import fourword.messages.ServerMsg;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends Activity implements Client.Listener{

    private static final int SERVER_PORT = 4444;
    private static final String SERVER_IP = "192.168.1.2";

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.lobby);
        Connection.instance().start(this, SERVER_IP, SERVER_PORT);
    }

    @Override
    public void handleServerMessage(ServerMsg msg) {
        Debug.d("Lobby-client received msg from server: " + msg);
        switch (msg.type()){
            case GAME_IS_STARTING:
                Intent intent = new Intent(this, GameActivity.class);
                Connection.instance().removeMessageListener();
                startActivity(intent);
                break;
//            case LOBBY_WAITING_FOR_MORE_PLAYERS:
//                findViewById(R.id.info_text).
            default:
                break;
        }
    }
}
