package fourword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;

import java.io.IOException;

/**
 * Created by jonathan on 2015-07-12.
 */
public class DialogCreator {



    public static void dialog(
            final Activity activity,
            final String title,
            final String dialogMessage,
            final DialogInterface.OnClickListener yesListener){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle(title)
                        .setMessage(dialogMessage)
                        .setPositiveButton("Yes", yesListener)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }


    public static void changeActivityForced(
            final Activity activity,
            final String title,
            final String dialogMessage,
            final Class newActivity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle(title)
                        .setMessage(dialogMessage)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChangeActivity.change(activity, newActivity, new Bundle());
                            }
                        })
                        .create();
                dialog.show();

            }
        });
    }

    public static void changeActivityQuestion(
            final Activity currentActivity,
            final String title,
            final String dialogMessage,
            final Msg<ClientMsg> yesMessage,
            final Class yesNewActivity){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(currentActivity)
                        .setTitle(title)
                        .setMessage(dialogMessage)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Connection.instance().sendMessage(yesMessage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ChangeActivity.change(currentActivity, yesNewActivity, new Bundle());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //DO nothing
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

}
