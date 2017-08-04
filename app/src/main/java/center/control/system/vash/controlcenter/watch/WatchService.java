package center.control.system.vash.controlcenter.watch;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class WatchService extends ExtensionService {
    public static final String EXTENSION_SPECIFIC_ID = "WATCH_SERVICE_ID";

    public static final String TAG = "Watch----servivce---";

    public WatchService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new WatchRegistrationInfo(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service watch ");
    }

    @Override
    protected void onViewEvent(Intent intent) {
        String action = intent.getStringExtra(Notification.Intents.EXTRA_ACTION);
        String hostAppPackageName = intent
                .getStringExtra(Registration.Intents.EXTRA_AHA_PACKAGE_NAME);
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);

        // Determine what item a user tapped in the options menu and take
        // appropriate action.
//        int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1);
        if (Notification.SourceColumns.ACTION_1.equals(action)) {
            Toast.makeText(this, "Action 2 API level 2", Toast.LENGTH_LONG).show();
        } else if (Notification.SourceColumns.ACTION_2.equals(action)) {
            // Here we can take different actions depending on the accessory.
            if (advancedFeaturesSupported) {
                Toast.makeText(this, "Action 2 API level 2", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action 2", Toast.LENGTH_LONG).show();
            }
        } else if (Notification.SourceColumns.ACTION_3.equals(action)) {
            Toast.makeText(this, "Action 3", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }
    @Override
    public ControlExtension createControlExtension(String hostAppPackageName) {
        return new WatchControl(hostAppPackageName, this, new Handler());
    }
}
