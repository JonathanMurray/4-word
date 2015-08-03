package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.fourword.R;
import fourword_shared.messages.*;
import fourword_shared.model.GameSettings;
import fourword_shared.model.Lobby;
import fourword_shared.model.LobbyPlayer;
import fourword_shared.model.PlayerInfo;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.List;

/**
 * Created by jonathan on 2015-06-25.
 */
public class LobbyActivity extends ReconnectableActivity implements NumberPickerView.NumberPickerListener, Persistent.OnlineListener {

    private boolean isPlayerHost;

    private Lobby lobby;
    private GameSettings settings;
    private boolean waitingForServer;

    private NumberPickerView colPicker;
    private NumberPickerView rowPicker;
    private RadioGroup timeLimitRadioGroup;
    private RadioButton timeNoLimit;
    private RadioButton timeStress;
    private RadioButton timeNormal;
    private RadioButton timeLong;
    private Button startButton;
    private Button inviteButton;
    private Button addBotButton;
    private ViewGroup avatarRow;

    private ScrollView settingsScrollSection;
    private ViewGroup customRulesSection;
    private CheckBox useCustomRulesCheckbox;
    private CheckBox preplacedRandomLettersCheckbox;

    public final static String IS_HOST = "IS_HOST"; //Instead of R.string since the string is also used by a dialogfragment

    //that doesn't have access to R.string (not attached to an activity yet)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        SoundManager.instance().play(SoundManager.JOINED_LOBBY);

        isPlayerHost = getIntent().getBooleanExtra(IS_HOST, false);
        Debug.e("lobby, isHost: " + isPlayerHost);

        startButton = (Button) findViewById(R.id.lobby_start_button);
        inviteButton = (Button) findViewById(R.id.lobby_add_human_button);
        addBotButton = (Button) findViewById(R.id.lobby_add_bot_button);
        colPicker = ((NumberPickerView)findViewById(R.id.col_picker));
        rowPicker = ((NumberPickerView)findViewById(R.id.row_picker));
        timeLimitRadioGroup = (RadioGroup) findViewById(R.id.time_limit_radiogroup);
        timeNoLimit = (RadioButton) findViewById(R.id.radio_no_time_limit);
        timeStress = (RadioButton) findViewById(R.id.radio_time_limit_stress);
        timeStress.setText(timeStress.getText() + " (" + getResources().getInteger(R.integer.timePerTurnStress) + ")");
        timeNormal = (RadioButton) findViewById(R.id.radio_time_limit_normal);
        timeNormal.setText(timeNormal.getText() + " (" + getResources().getInteger(R.integer.timePerTurnNormal) + ")");
        timeLong = (RadioButton) findViewById(R.id.radio_time_limit_long);
        timeLong.setText(timeLong.getText() + " (" + getResources().getInteger(R.integer.timePerTurnLong) + ")");
        useCustomRulesCheckbox = (CheckBox) findViewById(R.id.use_custom_rules_checkbox);
        customRulesSection = (ViewGroup) findViewById(R.id.custom_rules_section);
        preplacedRandomLettersCheckbox = (CheckBox) findViewById(R.id.preplaced_random_letters_checkbox);
        settingsScrollSection = (ScrollView) findViewById(R.id.lobby_host_section);
        avatarRow = ((ViewGroup) findViewById(R.id.avatar_row));

        colPicker.setClickedChangeListener(this);
        rowPicker.setClickedChangeListener(this);

        timeLimitRadioGroup.check(R.id.radio_no_time_limit);
        timeLimitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!isPlayerHost){
                    return; //This event is triggered even when programmatically checking the boxes
                }
                try{
                    switch (checkedId){
                        case R.id.radio_no_time_limit:
                            Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                                    GameSettings.IntAttribute.TIME_PER_TURN, 0));
                            break;
                        case R.id.radio_time_limit_stress:
                            Connection.instance().sendMessage (Msg.LobbySetAttribute.clientMsg(
                                    GameSettings.IntAttribute.TIME_PER_TURN, getResources().getInteger(R.integer.timePerTurnStress)));
                            break;
                        case R.id.radio_time_limit_normal:
                            Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                                    GameSettings.IntAttribute.TIME_PER_TURN, getResources().getInteger(R.integer.timePerTurnNormal)));
                            break;
                        case R.id.radio_time_limit_long:
                            Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                                    GameSettings.IntAttribute.TIME_PER_TURN, getResources().getInteger(R.integer.timePerTurnLong)));
                            break;
                        default:
                            throw new RuntimeException("Not handled id: " + checkedId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        setStartButtonEnabled(false);

        customRulesSection.setVisibility(View.GONE);

        if(isPlayerHost){
            lobby = new Lobby(Persistent.instance().playerName());
            settings = lobby.getSettings();
            initializeSettingsGraphics();
            updateLayout();
            setupForHost();
        }

        Connection.instance().setMessageListener(this);
    }

    private void setupForHost(){
        Persistent.instance().setOnlineListener(this);
    }



    public void clickedUseCustomRules(View view){
        settings.setAttribute(GameSettings.BoolAttribute.CUSTOM_RULES, useCustomRulesCheckbox.isChecked());
        final int visibility = settings.getBool(GameSettings.BoolAttribute.CUSTOM_RULES) ? View.VISIBLE : View.GONE;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                customRulesSection.setVisibility(visibility);
            }
        });
        settingsScrollSection.post(new Runnable() {
            @Override
            public void run() {
                settingsScrollSection.pageScroll(ScrollView.FOCUS_DOWN);
            }
        });
        try {
            Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                    GameSettings.BoolAttribute.CUSTOM_RULES, settings.getBool(GameSettings.BoolAttribute.CUSTOM_RULES)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickedPreplacedRandomLetters(View view){
//        if(!isPlayerhost){
//            return; //only host sends to server
//        }
        settings.setAttribute(GameSettings.BoolAttribute.PREPLACED_RANDOM, preplacedRandomLettersCheckbox.isChecked());
        try {
            Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                    GameSettings.BoolAttribute.PREPLACED_RANDOM, settings.getBool(GameSettings.BoolAttribute.PREPLACED_RANDOM)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        DialogCreator.changeActivityQuestion(this, "Leave lobby",
                "Are you sure?",
                new Msg.LeaveLobby(), MenuActivity.class);
    }

    private void updateLayout(){
        if(lobby == null){
            return;
        }
        isPlayerHost = Persistent.instance().playerName().equals(lobby.getHost());
        Debug.e("updateLayout() isHost: " + isPlayerHost);

        Debug.e("LobbyActivity.updateAvatarLayout()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                avatarRow.removeAllViews();
                int i = 0;
                for (final String playerName : lobby.sortedNames()) {
                    final int playerIndex = i;
                    boolean connected = lobby.getPlayer(playerName).hasConnected;
                    boolean selfAvatar = playerName.equals(Persistent.instance().playerName());

                    AvatarView avatarView = new AvatarView(LobbyActivity.this);
                    avatarView.setPlayerName(playerName);
                    if(!connected){
                        avatarView.setUnknownAvatar(true);
                    }

                    if (isPlayerHost && !selfAvatar) {
                        avatarView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogCreator.dialog(LobbyActivity.this, "Kick " + playerName + " from lobby", "Are you sure?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clickedKickPlayer(playerIndex);
                                    }
                                });
                            }
                        });
                    }
                    avatarRow.addView(avatarView);
                    i++;
                }

                if(isPlayerHost){
                    setSettingsEnabled(true);
                }else{
                    setSettingsEnabled(false);
//                    copySettingsFromLobbyObject(lobby);
                }
            }
        });
    }

    private void initializeSettingsGraphics(){
        for(GameSettings.Entry e : settings.getAllEntries()){
            renderSetting(e.attribute, e.value);
        }
    }


    private void renderSetting(final GameSettings.Attribute attribute, final Object value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (attribute instanceof GameSettings.IntAttribute) {
                    GameSettings.IntAttribute intAttribute = (GameSettings.IntAttribute) attribute;
                    switch (intAttribute) {
                        case ROWS:
                            rowPicker.setValue((Integer) value);
                            break;
                        case COLS:
                            colPicker.setValue((Integer) value);
                            break;
                        case TIME_PER_TURN:
                            final int stressTime = getResources().getInteger(R.integer.timePerTurnStress);
                            final int normalTime = getResources().getInteger(R.integer.timePerTurnNormal);
                            final int longTime = getResources().getInteger(R.integer.timePerTurnLong);
                            int timeLimit = (Integer) value;
                            if (timeLimit == 0) {
                                timeNoLimit.setChecked(true);
                            } else if (timeLimit == stressTime) {
                                timeStress.setChecked(true);
                            } else if (timeLimit == normalTime) {
                                timeNormal.setChecked(true);
                            } else if (timeLimit == longTime) {
                                timeLong.setChecked(true);
                            } else {
                                Debug.e("Unknown time limit: " + timeLimit);
                            }
                            break;
                    }
                } else if (attribute instanceof GameSettings.BoolAttribute) {
                    GameSettings.BoolAttribute boolAttribute = (GameSettings.BoolAttribute) attribute;
                    switch (boolAttribute) {
                        case CUSTOM_RULES:
                            boolean useCustomRules = (Boolean) value;
                            useCustomRulesCheckbox.setChecked(useCustomRules);
                            int visibility = useCustomRules ? View.VISIBLE : View.GONE;
                            customRulesSection.setVisibility(visibility);
                            break;
                        case PREPLACED_RANDOM:
                            preplacedRandomLettersCheckbox.setChecked((Boolean) value);
                            break;
                    }
                }
            }
        });

    }

    private void setSettingsEnabled(boolean isHost){
        int visibility = isHost ? View.VISIBLE : View.GONE;
        startButton.setVisibility(visibility);
        inviteButton.setVisibility(visibility);
        addBotButton.setVisibility(visibility);
        colPicker.setEnabled(isHost);
        rowPicker.setEnabled(isHost);
        for(int i = 0; i < timeLimitRadioGroup.getChildCount(); i++){
            timeLimitRadioGroup.getChildAt(i).setEnabled(isHost);
        }
        useCustomRulesCheckbox.setEnabled(isHost);
        preplacedRandomLettersCheckbox.setEnabled(isHost);
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

    public void clickedAddPlayer(View view){
        final String[] strings = Persistent.instance().getOtherOnlinePlayerStrings();
        final String[] othersPlayerNames = Persistent.instance().getOtherOnlinePlayerNames();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LobbyActivity.this)
            .setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    waitingForServer = true;
                    setButtonsEnabled(false);
                    try {
                        String name = othersPlayerNames[which];
                        Connection.instance().sendMessage(new Msg.InviteToLobby(name));
                        SoundManager.instance().play(SoundManager.ADDED_TO_LOBBY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setInfoText("Waiting for server...");
                }
            });
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.invite_other_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Invite player (" + (othersPlayerNames.length) + " online)");
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

    private void setInviteButtonsEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inviteButton.setEnabled(enabled);
                addBotButton.setEnabled(enabled);
            }
        });
    }

    private void setStartButtonEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(enabled);
            }
        });
    }

    private void setButtonsEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inviteButton.setEnabled(enabled);
                addBotButton.setEnabled(enabled);
                startButton.setEnabled(enabled);
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
                    extras.putSerializable(getString(R.string.GAME_SETTINGS), ((Msg.GameIsStarting) msg).settings);
//                    extras.putInt(getString(R.string.NUM_ROWS), ((Msg.GameIsStarting) msg).numRows);
                    String[] playerNames = ((Msg.GameIsStarting)msg).sortedPlayerNames;
                    extras.putStringArray(getString(R.string.PLAYER_NAMES), playerNames);
//                    extras.putInt(getString(R.string.TIME_PER_TURN), ((Msg.GameIsStarting)msg).timePerTurn);
                    ChangeActivity.change(this, GameActivity.class, extras);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case LOBBY_STATE:
                this.lobby = ((Msg.LobbyState)msg).get();
                this.settings = this.lobby.getSettings();
                initializeSettingsGraphics(); //Needed when a new player joins the lobby
                updateLayout();
                return true;
            case LOBBY_STATE_CHANGE:
                Msg.LobbyStateChange stateChange = (Msg.LobbyStateChange) msg;
                switch (stateChange.action){
                    case INVITED:
                        lobby.addPlayer(LobbyPlayer.pendingHuman(stateChange.playerName));
                        break;
                    case ACCEPTED_INVITE:
                        lobby.setConnected(stateChange.playerName);
                        break;
                    case BOT_ADDED:
                        lobby.addPlayer(LobbyPlayer.bot(stateChange.playerName));
                        break;
                    case LEFT:
                        lobby.removePlayer(stateChange.playerName);
                        break;
                    case NEW_HOST:
                        lobby.setNewHost(stateChange.playerName);
                        if(Persistent.instance().playerName().equals(stateChange.playerName)){
                            isPlayerHost = true;
                            setupForHost();
                        }
                        break;
                }
                handleNumPlayers();
                updateLayout();
                return true;

            case LOBBY_SET_ATTRIBUTE:
                Msg.LobbySetAttribute setAttr = (Msg.LobbySetAttribute) msg;
                renderSetting(setAttr.attribute, setAttr.value);
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

    private void handleNumPlayers(){
        boolean canInviteMore = lobby.size() < Lobby.MAX_PLAYERS;
        setInviteButtonsEnabled(canInviteMore);
        boolean canStart = lobby.size() > 1;
        setStartButtonEnabled(canStart);
    }


    //Triggered by increase() and decrease() but not setValue()
    @Override
    public void onUserPickedNumber(View view, int newValue) {
        try {
            if(view.equals(colPicker)){
                Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                        GameSettings.IntAttribute.COLS, colPicker.getValue()));
            }else if(view.equals(rowPicker)){
                Connection.instance().sendMessage(Msg.LobbySetAttribute.clientMsg(
                        GameSettings.IntAttribute.ROWS, rowPicker.getValue()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyOthersOnline(List<PlayerInfo> othersOnline) {
        //Do nothing since player infos are retrieved when user clicks invite
    }

}
