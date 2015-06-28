package fourword;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MenuActivity extends Activity implements MsgListener<ServerMsg>{

    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerName = getIntent().getStringExtra(getString(R.string.PLAYER_NAME));
        setContentView(R.layout.menu);
        ((TextView)findViewById(R.id.menu_name)).setText("Your name: " + playerName);
        Connection.instance().setMessageListener(this);
    }

    public void clickedStartGame(View view){
        Connection.instance().sendMessage(new Msg(ClientMsg.START_GAME));
        Bundle extras = new Bundle();
        extras.putString(getString(R.string.PLAYER_NAME), playerName);
        extras.putBoolean(LobbyActivity.IS_HOST, true);
        ChangeActivity.change(this, LobbyActivity.class, extras);
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        switch (msg.type()){
            case INVITE:
                String inviterName = ((MsgText)msg).text;
                DialogFragment dialog = new InviteDialogFragment();
                Bundle args = new Bundle();
                args.putString(InviteDialogFragment.INVITER_NAME, inviterName);
                args.putString(InviteDialogFragment.PLAYER_NAME, playerName);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "Invitation");
                return true;
            default:
                throw new RuntimeException(msg.toString());
        }
    }
}
