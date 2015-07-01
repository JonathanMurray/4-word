package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;
import org.andengine.util.debug.Debug;

import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MenuActivity extends Activity implements MsgListener<ServerMsg>, Persistent.OnlineListener {

    private ArrayAdapter onlinePlayersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        ((TextView)findViewById(R.id.menu_name)).setText("Welcome " + Persistent.instance().playerName());
        Connection.instance().setMessageListener(this);

        onlinePlayersAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Persistent.instance().getOtherOnlinePlayers());
//        onlinePlayersAdapter.setNotifyOnChange(true); //doens't seem to work
        Persistent.instance().setOnlineListener(this);
        ((ListView) findViewById(R.id.other_players_list)).setAdapter(onlinePlayersAdapter);
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
                        .setTitle("Log out")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Connection.instance().sendMessage(new Msg(ClientMsg.LOGOUT));
                                ChangeActivity.change(MenuActivity.this, LoginActivity.class, new Bundle());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
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

    @Override
    public void notifyOthersOnline(final List<String> othersOnline) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Debug.e("updating adapter");
                onlinePlayersAdapter.notifyDataSetChanged();
                //  onlinePlayersAdapter.setNotifyOnChange() doesn't seem to work
            }
        });
    }
}
