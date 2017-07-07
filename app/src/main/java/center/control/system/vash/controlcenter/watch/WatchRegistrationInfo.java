package center.control.system.vash.controlcenter.watch;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import center.control.system.vash.controlcenter.R;


/**
 * Created by Thuans on 4/25/2017.
 */

public class WatchRegistrationInfo extends com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation {

    final Context mContext;
    private String extensionKey;
    private static final String EXTENSION_KEY_PREF = "EXTENSION_KEY_PREF";


    /**
     * Creates a control registration object.
     *
     * @param context The context.
     */
    protected WatchRegistrationInfo(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mContext = context;
    }


    @Override
    public int getRequiredControlApiVersion() {
        // This extension supports all accessories from Control API level 1 and
        // up.
        return 1;
    }

    @Override
    public boolean controlInterceptsBackButton() {
        return true;
    }

    @Override
    public int getTargetControlApiVersion() {
        return 2;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return API_NOT_REQUIRED;
    }

    /**
     * Return the extension registration information. Specify the properties of
     * the extension that will be used when it is registered.
     *
     * @return The registration configuration.
     */
    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp = ExtensionUtils.getUriString(mContext, R.drawable.icon);
        String iconExtension = ExtensionUtils.getUriString(mContext, R.drawable.icon);
        String iconExtension48 = ExtensionUtils.getUriString(mContext, R.drawable.icon);
        String iconExtensionBw = ExtensionUtils.getUriString(mContext,R.drawable.icon);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                SonyActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,"smart home setting watch");
        values.put(Registration.ExtensionColumns.NAME, mContext.getString(R.string.app_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, getExtensionKey());
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.EXTENSION_48PX_ICON_URI, iconExtension48);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI_BLACK_WHITE, iconExtensionBw);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());

        return values;
    }

    @Override
    public boolean isDisplaySizeSupported(int width, int height) {
        return (width == WatchControl.getSupportedControlWidth(mContext) && height == WatchControl
                .getSupportedControlHeight(mContext));
    }

    /**
     * A basic implementation of getExtensionKey
     * Returns and saves a random string based on UUID.randomUUID()
     *
     * Note that this implementation doesn't guarantee random numbers on Android 4.3 and older. See <a href="https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html">https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html</a>
     *
     * @return A saved key if it exists, otherwise a randomly generated one.
     * @see RegistrationInformation#getExtensionKey()
     */
    @Override
    public synchronized String getExtensionKey() {
        if (TextUtils.isEmpty(extensionKey)) {
            // Retrieve key from preferences
            SharedPreferences pref = mContext.getSharedPreferences(EXTENSION_KEY_PREF,
                    Context.MODE_PRIVATE);
            extensionKey = pref.getString(EXTENSION_KEY_PREF, null);
            if (TextUtils.isEmpty(extensionKey)) {
                // Generate a random key if not found
                extensionKey = UUID.randomUUID().toString();
                pref.edit().putString(EXTENSION_KEY_PREF, extensionKey).commit();
            }
        }
        Log.d("registration","getExtension Key"+extensionKey);
        return extensionKey;
    }

    public ContentValues[] getSourceRegistrationConfigurations() {
        // This sample only adds one source but it is possible to add more
        // sources if needed.
        List<ContentValues> bulkValues = new ArrayList<ContentValues>();
        bulkValues.add(getSourceRegistrationConfiguration(WatchService.EXTENSION_SPECIFIC_ID));
        return bulkValues.toArray(new ContentValues[bulkValues.size()]);
    }

    public ContentValues getSourceRegistrationConfiguration(String extensionSpecificId) {
        ContentValues sourceValues = null;

        String iconSource1 = ExtensionUtils.getUriString(mContext,
                R.drawable.icon);
        String iconSource2 = ExtensionUtils.getUriString(mContext,
                R.drawable.icon);
        String iconBw = ExtensionUtils.getUriString(mContext,
                R.drawable.icon);
        String textToSpeech = "New notification event from";
        sourceValues = new ContentValues();
        sourceValues.put(Notification.SourceColumns.ENABLED, true);
        sourceValues.put(Notification.SourceColumns.ICON_URI_1, iconSource1);
        sourceValues.put(Notification.SourceColumns.ICON_URI_2, iconSource2);
        sourceValues.put(Notification.SourceColumns.ICON_URI_BLACK_WHITE, iconBw);
        sourceValues.put(Notification.SourceColumns.UPDATE_TIME, System.currentTimeMillis());
        sourceValues.put(Notification.SourceColumns.NAME, "source_name");
        sourceValues.put(Notification.SourceColumns.EXTENSION_SPECIFIC_ID, extensionSpecificId);
        sourceValues.put(Notification.SourceColumns.PACKAGE_NAME, mContext.getPackageName());
        sourceValues.put(Notification.SourceColumns.TEXT_TO_SPEECH, textToSpeech);
        // It is possible to connect actions to notifications. These will be
        // accessible when tapping the options menu on the SmartWatch 2.
        sourceValues.put(Notification.SourceColumns.ACTION_1,"action_event_1");
        sourceValues.put(Notification.SourceColumns.ACTION_2,"action_event_2");
        sourceValues.put(Notification.SourceColumns.ACTION_3,"action_event_3");
        sourceValues.put(Notification.SourceColumns.ACTION_ICON_1,"actions_1");
        sourceValues.put(Notification.SourceColumns.ACTION_ICON_2,"actions_2");
        sourceValues.put(Notification.SourceColumns.ACTION_ICON_3,"actions_3");

        sourceValues.put(Notification.SourceColumns.COLOR,
                mContext.getResources().getColor(R.color.colorPrimary));
        return sourceValues;
    }
}
