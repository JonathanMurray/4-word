package fourword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import fourword_shared.messages.ClientMsg;
import fourword_shared.messages.Msg;

/**
 * Created by jonathan on 2015-06-27.
 */
public class InviteDialogFragment extends DialogFragment{

    private String inviterName;
    private Bundle args;

    public final static String INVITER_NAME = "INVITER_NAME";

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.args = args;
        args.putBoolean(LobbyActivity.IS_HOST, false);
        inviterName = args.getString(INVITER_NAME);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(inviterName + " has invited you to a game!")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Connection.instance().sendMessage(new Msg(ClientMsg.ACCEPT_INVITE));
                        ChangeActivity.change(getActivity(), LobbyActivity.class, args);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Connection.instance().sendMessage(new Msg(ClientMsg.DECLINE_INVITE));
                    }
                });
        return builder.create();
    }
}
