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
import fourword_shared.messages.*;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.List;

/**
 * Created by jonathan on 2015-06-27.
 */
public class MenuActivity extends Activity implements MsgListener<ServerMsg>, Persistent.OnlineListener {

    private ArrayAdapter onlinePlayersAdapter;

    int numCols = 4;
    int numRows = 4;

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
        try {
            Connection.instance().sendMessage(new Msg.CreateLobby());
            Bundle extras = new Bundle();
            extras.putBoolean(LobbyActivity.IS_HOST, true);
            ChangeActivity.change(this, LobbyActivity.class, extras);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clickedPlayAI(View view){
        try {
            Connection.instance().sendMessage(new Msg.QuickStartGame(numCols, numRows, 2));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                                try {
                                    Connection.instance().sendMessage(new Msg.LogOut());
                                    ChangeActivity.change(MenuActivity.this, LoginActivity.class, new Bundle());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

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
                String inviterName = ((Msg.YouAreInvited)msg).get();
                DialogFragment dialog = new InviteDialogFragment();
                Bundle args = new Bundle();
                args.putString(InviteDialogFragment.INVITER_NAME, inviterName);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "Invitation");
                return true;
            case GAME_IS_STARTING:
                String[] playerNames = ((Msg.GameIsStarting)msg).sortedPlayerNames;
                int numCols = ((Msg.GameIsStarting)msg).numCols;
                int numRows = ((Msg.GameIsStarting)msg).numRows;
                Bundle extras = new Bundle();
                extras.putInt(getString(R.string.NUM_COLS), numCols);
                extras.putInt(getString(R.string.NUM_ROWS), numRows);
                extras.putStringArray(getString(R.string.PLAYER_NAMES), playerNames);
                try {

                    Connection.instance().sendMessage(new Msg.ConfirmGameStarting(Persistent.instance().playerName()));
                    ChangeActivity.change(this, GameActivity.class, extras);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
