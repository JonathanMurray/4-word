package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;
import fourword.protocol.Lobby;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends Activity implements MsgListener<ServerMsg> {

    private boolean isPlayerHost;

    private Lobby lobby;
    private boolean waitingForServer;

    public final static String IS_HOST = "IS_HOST"; //Instead of R.string since the string is also used by a dialogfragment
    //that doesn't have access to R.string (not attached to an activity yet)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        isPlayerHost = getIntent().getBooleanExtra(IS_HOST, false);
        Debug.e("lobby, isHost: " + isPlayerHost);

        if(isPlayerHost){
            findViewById(R.id.lobby_host_section).setVisibility(View.VISIBLE);
        }

        lobby = new Lobby(Persistent.instance().playerName());
//        lobby.addPlayer(LobbyPlayer.connectedHuman(thisPlayerName)); //already added in constructor
        updateLayout();
        Connection.instance().setMessageListener(this);
//        Connection.instance().startOnline(this, IP_ADDRESS, PORT);
//        Connection.instance().startOffline(this, 2, 2, 3);
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(LobbyActivity.this)
                        .setTitle("Leave lobby")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Connection.instance().sendMessage(new Msg(ClientMsg.LEAVE_LOBBY));
                                ChangeActivity.change(LobbyActivity.this, MenuActivity.class, new Bundle());
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

    private void updateLayout(){
        Debug.e("LobbyActivity.updateLayout()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup avatarRow = ((ViewGroup) findViewById(R.id.avatar_row));
                avatarRow.removeAllViews();
                int i = 0;
                for (String lobbyPlayer : lobby.sortedNames()) {
                    final int playerIndex = i;
                    ViewGroup avatarView = (ViewGroup) View.inflate(LobbyActivity.this, R.layout.lobby_avatar, null);
                    ((TextView) avatarView.findViewById(R.id.avatar_name)).setText(lobbyPlayer);
                    boolean connected = lobby.getPlayer(lobbyPlayer).hasConnected;
                    if (!connected) {
                        ((TextView) avatarView.findViewById(R.id.avatar_pending_text)).setText("Pending");
                    }
                    boolean selfAvatar = lobbyPlayer.equals(Persistent.instance().playerName());
                    if (isPlayerHost && !selfAvatar) {
                        Button kickButton = ((Button) avatarView.findViewById(R.id.avatar_kick_button));
                        kickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickedKickPlayer(playerIndex);
                            }
                        });
                        kickButton.setVisibility(View.VISIBLE);
                    }

                    avatarRow.addView(avatarView);
                    i++;
                }
            }
        });

    }

    private void clickedKickPlayer(int playerIndex){
        String name = lobby.getNameAtIndex(playerIndex);
        Debug.e("Klicked kick player " + name);
        Connection.instance().sendMessage(new MsgText<>(ClientMsg.KICK, name));
    }

    public void hideKeyboard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                final EditText textInput = (EditText) findViewById(R.id.lobby_player_name);
                imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
            }
        });
    }



    public void clickedAddPlayer(View view){
        String playerName = ((EditText)findViewById(R.id.lobby_player_name)).getText().toString();
        Connection.instance().sendMessage(new MsgText(ClientMsg.INVITE, playerName));
        waitingForServer = true;
        setButtonsEnabled(false);
        setInfoText("Waiting for server...");
        hideKeyboard();
    }

    public void clickedAddBot(View view){
        Connection.instance().sendMessage(new Msg(ClientMsg.ADD_BOT));
    }

    public void clickedStartGame(View view){
        Connection.instance().sendMessage(new Msg(ClientMsg.START_GAME));
        waitingForServer = true;
        setInfoText("Waiting for server...");
        setButtonsEnabled(false);
    }

    private void setButtonsEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.lobby_add_human_button).setEnabled(enabled);
                findViewById(R.id.lobby_start_button).setEnabled(enabled);
            }
        });
    }

    private void setInfoText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.lobby_info_text)).setText(text);
            }
        });
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        Debug.d("Lobby-client received msg from server: " + msg);
        switch (msg.type()){
            case OK:
                if(waitingForServer){
                    setInfoText("");
                    waitingForServer = false;
                    setButtonsEnabled(true);
                    return true;
                }else{
                    throw new RuntimeException(msg.toString());
                }

            case NO:
                if(waitingForServer){
                    waitingForServer = false;
                    setButtonsEnabled(true);
                    setInfoText(((MsgText)msg).text);
                    return true;
                }else{
                    throw new RuntimeException(msg.toString());
                }

            case GAME_IS_STARTING:
                Connection.instance().sendMessage(new Msg(ClientMsg.CONFIRM_GAME_STARTING));
                Bundle extras = new Bundle();
                extras.putInt(getString(R.string.NUM_COLS), ((MsgGameIsStarting) msg).numCols);
                extras.putInt(getString(R.string.NUM_ROWS), ((MsgGameIsStarting) msg).numRows);
                ChangeActivity.change(this, GameActivity.class, extras);
                return true;
            case LOBBY_STATE:
                this.lobby = ((MsgLobbyState)msg).lobby;
                updateLayout();
//                ((TextView)findViewById(R.id.lobby_info_text)).setText("Waiting for more players ...");
                return true;

            case YOU_WERE_KICKED:
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(LobbyActivity.this)
                            .setCancelable(false)
                            .setTitle("You were kicked!")
                            .setMessage("You have been kicked from the lobby by the hosting player.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ChangeActivity.change(LobbyActivity.this, MenuActivity.class, new Bundle());
                                }
                            })
                            .create();
                    dialog.show();
                }
            });
                return true;

            default:
                Debug.e("Received in lobby: " + msg.toString());
                //It may happen that server sends game-related messages even though we are still in the lobby.
                //This is our way of saying that the message should be sent later to a new listener.
                return false;
        }
    }
}
