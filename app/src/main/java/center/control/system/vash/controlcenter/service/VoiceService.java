package center.control.system.vash.controlcenter.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * Created by Sam on 4/20/2017.
 */

public class VoiceService extends Service{
    private static final String TAG = "Voice service: ";
    static final public String VOICE_SERVICE = "resulttt";
    static final public String VOICE_CONTENT = "VContent";

    private static LocalBroadcastManager broadcaster;



    public static void sendMessageVoice(String msg) {
        Intent intent = new Intent(VOICE_SERVICE);
        showMessage(" voice Content :::::::"+ msg);
        intent.putExtra(VOICE_CONTENT, msg);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private static void showMessage(String message) {
//        sendBroadCastResult(message);
        Log.i(TAG, message);
    }
}
