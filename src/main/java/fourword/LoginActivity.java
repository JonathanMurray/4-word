package fourword;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import fourword_shared.messages.*;
import fourword_shared.model.GameResult;
import fourword_shared.model.MockupFactory;

import java.io.IOException;

/**
 * Created by jonathan on 2015-06-27.
 */
public class LoginActivity extends Activity implements MsgListener<ServerMsg> {

//    private static boolean USE_LOCAL_SERVER = true;
//    private static final String LOCAL_SERVER_ADDRESS = "192.168.1.2";
//    private static final String LOCAL_SERVER_ADDRESS = "192.168.43.31";
//    private static final String REMOTE_SERVER_ADDRESS = "fourword-server.herokuapp.com";
    private boolean waitingForReply = false;
    private String requestedName;

    @Override
    public void onBackPressed() {

        //Do nothing

        //TODO finish just quits the activity
//        DialogCreator.dialog(this, "Quit", "Do you want to quit?", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        SoundManager.create(this);
//        String serverAddr = USE_LOCAL_SERVER ? LOCAL_SERVER_ADDRESS : REMOTE_SERVER_ADDRESS;
//        Connection.instance().connectToLAN(this);
//        Connection.instance().connectToHeroku(this);
        Connection.instance().connectToCustom(this, "192.168.1.2");

    }

    public void clickedShortcut(View view){
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", MockupFactory.createResult());
        ChangeActivity.change(this, ScoreActivity.class, bundle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final EditText textInput = (EditText) findViewById(R.id.login_input);
        textInput.setFocusableInTouchMode(true);
        textInput.setFocusable(true);
        textInput.requestFocus();
        if(textInput.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
//        showKeyboard();
    }

    public void showKeyboard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            final EditText textInput = (EditText) findViewById(R.id.login_input);
            textInput.requestFocus();
            imm.showSoftInput(textInput, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public void clickedLogin(View view){
        setButtonEnabled(false);
        String nameInput = ((TextView)findViewById(R.id.login_input)).getText().toString();
        try{
            Connection.instance().sendMessage(new Msg.LogIn(nameInput));
            waitingForReply = true;
            requestedName = nameInput;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(Msg<ServerMsg> msg) {
        if(waitingForReply){
            switch (msg.type()){
                case OK:
                    startMenuActivity(requestedName);
                    break;
                case NO:
                    waitingForReply = false;
                    setButtonEnabled(true);
                    setInfoText(((Msg.No)msg).get());
                    break;
                default:
                    throw new RuntimeException(msg.toString());
            }
        }
        return true;
    }

    private void setButtonEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.button_login).setEnabled(enabled);
            }
        });
    }

    private void setInfoText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.login_info)).setText(text);
            }
        });
    }

    private void startMenuActivity(String userName){
        Persistent.instance().init(userName);
        ChangeActivity.change(this, MenuActivity.class, new Bundle());
    }

}
