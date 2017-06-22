package center.control.system.vash.controlcenter.recognition;

import android.content.Context;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import center.control.system.vash.controlcenter.R;

/**
 * Created by Thuans on 4/18/2017.
 */

public class Facedetect {
    private static Facedetect singleton;
    private static String TAG = "Vision api singleton";
    private Detector<Face> safeDetector;
    private static Context context;
    private Facedetect(){

    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;

    public static FaceServiceClient getInstance(Context c){
        if (sFaceServiceClient == null){
            sFaceServiceClient = new FaceServiceRestClient(c.getString(R.string.endpoint), c.getString(R.string.subscription_key));
        }
        return sFaceServiceClient;
    }

    public static Context getContext(){
        return context;
    }

}
