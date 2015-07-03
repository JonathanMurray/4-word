package fourword;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;
import fourword.server.EnvironmentVars;
import fourword.server.Server;

/**
 * Created by jonathan on 2015-06-27.
 */
public class LoginActivity extends Activity implements MsgListener<ServerMsg> {

    private static boolean USE_LOCAL_SERVER = false;

    private static final String LOCAL_SERVER_ADDRESS = "192.168.1.2";
//    private static final String LOCAL_SERVER_ADDRESS = "192.168.43.31";
    private static final String REMOTE_SERVER_ADDRESS = "fourword-server.herokuapp.com";
    private boolean waitingForReply = false;
    private String requestedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        String serverAddr = USE_LOCAL_SERVER ? LOCAL_SERVER_ADDRESS : REMOTE_SERVER_ADDRESS;
        if(USE_LOCAL_SERVER){
            Connection.instance().startOnline(this, serverAddr, EnvironmentVars.serverPort());
        }else{
            Connection.instance().startOnline(this, serverAddr, 80);
        }

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
        Connection.instance().sendMessage(new MsgText(ClientMsg.LOGIN, nameInput));
        waitingForReply = true;
        requestedName = nameInput;
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
                    setInfoText(((MsgText)msg).text);
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
