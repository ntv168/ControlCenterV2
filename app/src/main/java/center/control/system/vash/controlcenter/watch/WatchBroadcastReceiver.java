package center.control.system.vash.controlcenter.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Thuans on 4/25/2017.
 */

public class WatchBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("BroadCast receiver", "onReceive: " + intent.getAction()+" contxt ");
//
//        if (intent.getAction().equals("com.sonyericsson.extras.liveware.aef.registration.EXTENSION_REGISTER_REQUEST")){
            intent.setClass(context, WatchService.class);
            context.startService(intent);
//        }
    }
}
