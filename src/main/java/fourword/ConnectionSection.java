package fourword;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.fourword.R;

/**
 * Created by jonathan on 2015-08-02.
 */
public class ConnectionSection extends LinearLayout {

    private TextView onlineText;
    private Button reconnectButton;
    private Listener listener;

    public ConnectionSection(Context context) {
        super(context);
        setup(context);
    }

    public ConnectionSection(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context context){
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.connection_section, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onlineText = (TextView) findViewById(R.id.online_text);
        reconnectButton = (Button) findViewById(R.id.reconnect_button);
        reconnectButton.setVisibility(View.GONE);
        reconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.clickedReconnect();
                }
            }
        });
    }

    public void setOnline(boolean online){
        if(online){
            onlineText.setText("ONLINE");
            reconnectButton.setVisibility(View.GONE);
        }else{
            onlineText.setText("OFFLINE");
            reconnectButton.setVisibility(View.VISIBLE);
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void clickedReconnect();
    }
}
