package fourword;

import android.os.Handler;
import android.os.Message;
import org.andengine.util.debug.Debug;

/**
 * Created by jonathan on 2015-07-10.
 */
public class Ticker extends Thread {

    private final Handler handler;
    private int ticks = 0;
    private final int maxTicks;

    public Ticker(Handler handler, int maxTicks){
        Debug.e("Ticker constr");
        this.handler = handler;
        this.maxTicks = maxTicks;
    }

    @Override
    public void run() {
        Debug.e("ticker run");
        try {
            while (ticks <= maxTicks) {
                Debug.e("Ticker ticks: " + ticks);
                Thread.sleep(1000);
                Debug.e("After sleep");
                ticks ++;
                Message msg = new Message();
                msg.arg1 = ticks;
                Debug.e("ticker calling handle msg");
                handler.sendMessage(msg);
                Debug.e("After ticker calling handle msg");
            }
        } catch (InterruptedException e) {
            Debug.e("Ticker interupted");
        }
    }
}
