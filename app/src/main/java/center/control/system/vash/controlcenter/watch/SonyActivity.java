package center.control.system.vash.controlcenter.watch;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

import java.util.ArrayList;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.panel.ControlPanel;


public class SonyActivity extends AppCompatActivity {

    public static final String ACTION_UPDATE_ACTIVITY = "smart.house.watch.SONY_ACTIVITY";
    private static final int REQ_CODE_SPEECH_INPUT = 11;
    private static final String TAG = "Sony Activity";
    public static final String SEND_COMMAND = "sony send command to Main";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sony);
        NotificationUtil.deleteAllEvents(SonyActivity.this);
        if (!ExtensionUtils.supportsHistory(getIntent())) {
            Log.d(TAG,"not support History");
        }
        promptSpeechInput();
    }
    private void addData()
    {
        String name = "message name";
        String message = "noi dung message";
        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(this,
                WatchService.EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(TAG, "Failed to insert data");
            return;
        }
        String profileImage = ExtensionUtils.getUriString(this,
                R.drawable.icon);

        // Build the notification.
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, name);
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

        NotificationUtil.addEvent(this, eventValues);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateValues(intent);
    }

    /**
     * Extracts the user event type and content and presents them in an activity.
     *
     * @param intent The intent with the data to present.
     */
    private void updateValues(Intent intent) {

        Log.d("---Intent----",intent.getAction());
        if (TextUtils.equals(intent.getAction(), ACTION_UPDATE_ACTIVITY)) {

        }
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Xin ra lệnh");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.d(TAG,"Fail to nhận diện giọng nói");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,requestCode+"--" );
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1 && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d(TAG,result.get(0));
                    Intent intent = new Intent(SonyActivity.this, ControlPanel.class);
                    Bundle b = new Bundle();
                    b.putString(ControlPanel.WATCH_STT_CONTENT, result.get(0)); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                    this.finish();
                }
                break;
            }
            default: {
                Intent intent = new Intent(SonyActivity.this, ControlPanel.class);
                Bundle b = new Bundle();
                b.putString(ControlPanel.WATCH_STT_CONTENT, "Em không hiểu. Anh vui lòng nói lại"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                this.finish();

            }

        }
    }
}
