package center.control.system.vash.controlcenter.recognition;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
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
    private Facedetect(){

    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;

    public static Facedetect getInstance(Context c){
        if (singleton == null){
            singleton = new Facedetect();
            FaceDetector detector = new FaceDetector.Builder(c)
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .build();
            Detector<Face> faceDetector = new SafeFaceDetector(detector);

            if (!faceDetector.isOperational()) {
                Log.w(TAG, "Face detector dependencies are not yet availab");
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = c.registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Log.w(TAG, "Khong du bo nho");
                }
            } else {
                singleton.safeDetector  = faceDetector;
            }
            sFaceServiceClient = new FaceServiceRestClient(c.getString(R.string.endpoint), c.getString(R.string.subscription_key));


        }
        return singleton;
    }

    public Detector<Face> getSafeDetector() {
        return safeDetector;
    }

}
