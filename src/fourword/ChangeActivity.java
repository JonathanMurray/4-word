package fourword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jonathan on 2015-06-27.
 */
public class ChangeActivity {


    public static <T extends Activity> void change(Activity context, Class<T> newActivity, Bundle extras){
        Intent intent = new Intent(context, newActivity);
        intent.putExtras(extras);
        Connection.instance().removeMessageListener();
        Persistent.instance().removeOnlineListener();
        context.startActivity(intent);
    }
}
