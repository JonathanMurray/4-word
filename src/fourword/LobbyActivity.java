package fourword;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;
import fourword.model.LobbyPlayer;
import org.andengine.util.debug.Debug;

import java.util.HashMap;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends Activity implements MsgListener<ServerMsg> {

    private String playerName;
    private boolean isPlayerHost;

    private HashMap<String, LobbyPlayer> lobbyState = new HashMap<>();
    private boolean waitingForServer;

    public final static String IS_HOST = "IS_HOST"; //Instead of R.string since the string is also used by a dialogfragment
    //that doesn't have access to R.string (not attached to an activity yet)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        playerName = getIntent().getStringExtra(getString(R.string.PLAYER_NAME));
        isPlayerHost = getIntent().getBooleanExtra(IS_HOST, false);
        Debug.e("lobby, isHost: " + isPlayerHost);

        if(isPlayerHost){
            findViewById(R.id.lobby_host_section).setVisibility(View.VISIBLE);
        }

        System.out.println("playername from intent: " + playerName);
        if(playerName == null){
            throw new RuntimeException(getIntent().toString());
        }
        lobbyState.put(playerName, new LobbyPlayer(playerName, true, true));
        updateLayout();
        Connection.instance().setMessageListener(this);
//        Connection.instance().startOnline(this, IP_ADDRESS, PORT);
//        Connection.instance().startOffline(this, 2, 2, 3);
    }

    private void updateLayout(){
        Debug.e("LobbyActivity.updateLayout()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup avatarRow =((ViewGroup)findViewById(R.id.avatar_row));
                avatarRow.removeAllViews();
                int i = 0;
                for(LobbyPlayer player : lobbyState.values()){
                    final int playerIndex = i;
                    ViewGroup avatarView = (ViewGroup) View.inflate(LobbyActivity.this, R.layout.lobby_avatar, null);
                    ((TextView)avatarView.findViewById(R.id.avatar_name)).setText(player.name);
                    if(!player.hasConnected){
                        ((TextView)avatarView.findViewById(R.id.avatar_pending_text)).setText("Pending");
                    }
                    if(isPlayerHost){
                        Button kickButton = ((Button)avatarView.findViewById(R.id.avatar_kick_button));
                        kickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickedKickPlayer(playerIndex);
                            }
                        });
                        kickButton.setVisibility(View.VISIBLE);
                    }

                    avatarRow.addView(avatarView);
                    i ++;
                }
            }
        });

    }

    private void clickedKickPlayer(int playerIndex){
        Debug.e("Klicked kick player " + playerIndex);
    }



    public void clickedAddPlayer(View view){
        String playerName = ((EditText)findViewById(R.id.lobby_player_name)).getText().toString();
        Connection.instance().sendMessage(new MsgText(ClientMsg.INVITE, playerName));
        waitingForServer = true;
        setButtonsEnabled(false);
        setInfoText("Waiting for server...");
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
                findViewById(R.id.lobby_add_button).setEnabled(enabled);
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
                Bundle extras = new Bundle();
                extras.putInt(getString(R.string.NUM_COLS), ((MsgGameIsStarting) msg).numCols);
                extras.putInt(getString(R.string.NUM_ROWS), ((MsgGameIsStarting) msg).numRows);
                ChangeActivity.change(this, GameActivity.class, extras);
                return true;
            case LOBBY_STATE:
                this.lobbyState = ((MsgLobbyState)msg).lobbyPlayers;
                updateLayout();
//                ((TextView)findViewById(R.id.lobby_info_text)).setText("Waiting for more players ...");
                return true;
            default:
                Debug.e("Received in lobby: " + msg.toString());
                //It may happen that server sends game-related messages even though we are still in the lobby.
                //This is our way of saying that the message should be sent later to a new listener.
                return false;
        }
    }
}
