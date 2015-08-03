package fourword;

import android.app.Activity;
import android.os.Bundle;
import com.example.fourword.R;
import fourword_shared.messages.Msg;
import fourword_shared.messages.MsgListener;
import fourword_shared.messages.ServerMsg;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-08-02.
 */
public class ReconnectableActivity extends Activity implements ConnectionSection.Listener, MsgListener<ServerMsg> {

    private ConnectionSection connectionSection;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        connectionSection = (ConnectionSection) findViewById(R.id.connection_section);
        connectionSection.setListener(this);
    }

    @Override
    public void clickedReconnect() {
        Connection.instance().reconnectDefault(this);
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        return false;
    }

    @Override
    public void lostConnection() {
        Debug.e("lostConnection()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionSection.setOnline(false);
            }
        });
    }

    @Override
    public void establishedConnection() {
        Debug.e("establishedConnection()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionSection.setOnline(true);
            }
        });
    }
}
