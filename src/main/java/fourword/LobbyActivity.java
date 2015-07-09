package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.android_test.R;
import fourword_shared.messages.*;
import fourword_shared.model.Lobby;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends Activity implements MsgListener<ServerMsg>, HorizontalNumberPicker.NumberPickerListener {

    private boolean isPlayerHost;

    private Lobby lobby;
    private boolean waitingForServer;

    private HorizontalNumberPicker colPicker;
    private HorizontalNumberPicker rowPicker;

    public final static String IS_HOST = "IS_HOST"; //Instead of R.string since the string is also used by a dialogfragment
    //that doesn't have access to R.string (not attached to an activity yet)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        isPlayerHost = getIntent().getBooleanExtra(IS_HOST, false);
        Debug.e("lobby, isHost: " + isPlayerHost);

        colPicker = ((HorizontalNumberPicker)findViewById(R.id.col_picker));
        rowPicker = ((HorizontalNumberPicker)findViewById(R.id.row_picker));
        colPicker.setValueListener(this);
        rowPicker.setValueListener(this);

        lobby = new Lobby(Persistent.instance().playerName());
        updateLayout();
        Connection.instance().setMessageListener(this);
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
                                try {
                                    Connection.instance().sendMessage(new Msg.LeaveLobby());
                                    ChangeActivity.change(LobbyActivity.this, MenuActivity.class, new Bundle());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        isPlayerHost = lobby.getHost().equals(Persistent.instance().playerName());

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
                    if (connected) {
                        ((TextView) avatarView.findViewById(R.id.avatar_pending_text)).setText("");
                    }
                    boolean selfAvatar = lobbyPlayer.equals(Persistent.instance().playerName());
                    Button kickButton = ((Button) avatarView.findViewById(R.id.avatar_kick_button));
                    if (isPlayerHost && !selfAvatar) {
                        kickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickedKickPlayer(playerIndex);
                            }
                        });
                        kickButton.setVisibility(View.VISIBLE);
                    }else{
                        kickButton.setVisibility(View.GONE);
                    }

                    avatarRow.addView(avatarView);
                    i++;
                }

                ViewGroup hostSection = (ViewGroup) findViewById(R.id.lobby_host_section);
                if(isPlayerHost){
                    hostSection.setVisibility(View.VISIBLE);
                }else{
                    hostSection.setVisibility(View.GONE);
                }
            }
        });

    }

    private void clickedKickPlayer(int playerIndex){
        String name = lobby.getNameAtIndex(playerIndex);
        Debug.e("Clicked kick player " + name);
        try {

            Connection.instance().sendMessage(new Msg.KickFromLobby(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                final EditText textInput = (EditText) findViewById(R.id.lobby_player_name);
//                imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
//            }
//        });
    }



    public void clickedAddPlayer(View view){
//        String playerName = ((EditText)findViewById(R.id.lobby_player_name)).getText().toString();

        //TODO Use same kind of view here as in menu when seeing other online players
        //The players that are in game or lobby should somehow be disabled, so u cant invite them
        final String[] names = Persistent.instance().getOtherOnlinePlayers().toArray(new String[0]);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LobbyActivity.this)
            .setItems(names, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String playerName = names[which];
                    try {
                        Connection.instance().sendMessage(new Msg.InviteToLobby(playerName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    waitingForServer = true;
                    setButtonsEnabled(false);
                    setInfoText("Waiting for server...");
                }
            });
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.invite_other_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Invite player");
        alertDialog.show();
    }

    public void clickedAddBot(View view){
        try {
            Connection.instance().sendMessage(new Msg.AddBot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickedStartGame(View view){

        try {
            Connection.instance().sendMessage(new Msg.StartGameFromlobby());
            waitingForServer = true;
            setInfoText("Waiting for server...");
            setButtonsEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    setInfoText(((Msg.No)msg).get());
                    return true;
                }else{
                    throw new RuntimeException(msg.toString());
                }

            case GAME_IS_STARTING:
                try {

                    Connection.instance().sendMessage(new Msg.ConfirmGameStarting(lobby.getHost()));
                    Bundle extras = new Bundle();
                    extras.putInt(getString(R.string.NUM_COLS), ((Msg.GameIsStarting) msg).numCols);
                    extras.putInt(getString(R.string.NUM_ROWS), ((Msg.GameIsStarting) msg).numRows);
                    String[] playerNames = ((Msg.GameIsStarting)msg).sortedPlayerNames;
                    extras.putStringArray(getString(R.string.PLAYER_NAMES), playerNames);
                    ChangeActivity.change(this, GameActivity.class, extras);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case LOBBY_STATE:
                this.lobby = ((Msg.LobbyState)msg).get();
                updateLayout();
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

    @Override
    public void onNumberPickerChange(int newValue) {
        try {
            Connection.instance().sendMessage(new Msg.LobbySetDim(colPicker.getValue(), rowPicker.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
