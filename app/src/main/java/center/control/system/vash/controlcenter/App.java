package center.control.system.vash.controlcenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.database.SqLiteHelper;
import center.control.system.vash.controlcenter.nlp.VoiceUtils;

/**
 * Created by Thuans on 4/27/2017.
 */

public class App extends Application {
    private static Context context;
    private static SqLiteHelper dbHelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        VoiceUtils.initializeInstance(context);
        dbHelper = new SqLiteHelper();
        SQLiteManager.initializeInstance(dbHelper);
        Facedetect singleFace = Facedetect.getInstance(context);
        Log.d("App context","initiating .....");

    }

    public static Context getContext(){
        return context;
    }
}
