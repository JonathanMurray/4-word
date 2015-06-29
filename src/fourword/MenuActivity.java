package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MenuActivity extends Activity implements MsgListener<ServerMsg>{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        ((TextView)findViewById(R.id.menu_name)).setText("Your name: " + Account.instance().playerName());
        Connection.instance().setMessageListener(this);
    }

    public void clickedCreateGame(View view){
        Connection.instance().sendMessage(new Msg(ClientMsg.CREATE_GAME));
        Bundle extras = new Bundle();
        extras.putBoolean(LobbyActivity.IS_HOST, true);
        ChangeActivity.change(this, LobbyActivity.class, extras);
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("Log out?")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Connection.instance().sendMessage(new Msg(ClientMsg.LOGOUT));
                                ChangeActivity.change(MenuActivity.this, LoginActivity.class, new Bundle());
                            }
                        })
                        .setNegativeButton("Stay logged in", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        switch (msg.type()){
            case YOU_ARE_INVITED:
                String inviterName = ((MsgText)msg).text;
                DialogFragment dialog = new InviteDialogFragment();
                Bundle args = new Bundle();
                args.putString(InviteDialogFragment.INVITER_NAME, inviterName);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "Invitation");
                return true;
            default:
                throw new RuntimeException(msg.toString());
        }
    }
}
