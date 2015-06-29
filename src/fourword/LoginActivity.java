package fourword;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.android_test.R;
import fourword.messages.*;
import fourword.protocol.Server;

/**
 * Created by jonathan on 2015-06-27.
 */
public class LoginActivity extends Activity implements MsgListener<ServerMsg> {

    private boolean waitingForReply = false;
    private String requestedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Connection.instance().startOnline(this, Server.IP_ADDRESS, Server.PORT);
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
        Account.instance().init(userName);
        ChangeActivity.change(this, MenuActivity.class, new Bundle());
    }

}