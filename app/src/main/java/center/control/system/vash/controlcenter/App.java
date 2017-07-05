package center.control.system.vash.controlcenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.database.SqLiteHelper;
import center.control.system.vash.controlcenter.voice.VoiceUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

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
        dbHelper = new SqLiteHelper();
        SQLiteManager.initializeInstance(dbHelper);
        sFaceServiceClient = new FaceServiceRestClient(context.getString(R.string.endpoint), context.getString(R.string.subscription_key));
        SmartHouse.getInstance().setContractId(getSharedPreferences(ConstManager.SHARED_PREF_NAME,MODE_PRIVATE).getString(ConstManager.CONTRACT_ID,""));

        Log.d("App context","initiating .....");

    }
    public static FaceServiceClient getFaceServiceClient(){
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;

    public static Context getContext(){
        return context;
    }
}
