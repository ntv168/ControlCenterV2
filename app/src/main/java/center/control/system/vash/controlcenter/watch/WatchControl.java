package center.control.system.vash.controlcenter.watch;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.panel.ControlPanel;


/**
 * Created by Thuans on 4/25/2017.
 */

public class WatchControl extends ControlExtension {

    private static final String TAG = "Watch_____Control" ;

    WatchControl(final String hostAppPackageName, final Context context, Handler handler) {
        super(context, hostAppPackageName);
        Log.d(TAG, "CONTRUCTORR");
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }

    }

    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Control watch");
    };

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
//        Log.d(HelloLayoutsExtensionService.LOG_TAG,
//                "onObjectClick: HelloLayoutsControl click type: " + event.getClickType());

        // Check which view was clicked and then take the desired action.
        switch (event.getLayoutReference()) {
            case R.id.btn_call_home:
                triggerListenOnMobile();
                break;
            default:
                break;
        }
    }
    private void triggerListenOnMobile(){
        Intent intent = new Intent(mContext, ControlPanel.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bun = new Bundle();
        bun.putString("watch","voice");
        intent.putExtra("bundle",bun);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void updateLayout() {
        Log.d(TAG, "UPDATEeeee");

        // Prepare a bundle to update the button text.
        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.btn_call_home);
        bundle1.putString(Control.Intents.EXTRA_TEXT, "Goi Sen");
//
//        // Prepare a bundle to update the ImageView image.
//        Bundle bundle2 = new Bundle();
//        bundle2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.image);
//        bundle2.putString(Control.Intents.EXTRA_DATA_URI,
//                ExtensionUtils.getUriString(mContext, R.drawable.icon_extension48));

        Bundle[] bundleData = new Bundle[1];
        bundleData[0] = bundle1;
//        bundleData[1] = bundle2;

        showLayout(R.layout.watch_layout, bundleData);
    }




    @Override
    public void onResume() {

        Log.d(TAG, "RESUMEEEEE");
        updateLayout();
        super.onResume();
        triggerListenOnMobile();
    }

}
