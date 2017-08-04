package center.control.system.vash.controlcenter.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 7/11/2017.
 */

public class MessageUtils {
    public static AlertDialog.Builder makeText(Context context, String message){
        return   new AlertDialog.Builder(context)
                .setIcon(R.drawable.icon)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
    }
}
