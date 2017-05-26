package center.control.system.vash.controlcenter.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Thuans on 4/18/2017.
 */

public class Facedetect {
    private static Facedetect singleton;
    private static String TAG = "Vision api singleton";
    private Bitmap imageBitmap;
    private Detector<Face> safeDetector;
    private Facedetect(){

    }
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
        }
        return singleton;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Detector<Face> getSafeDetector() {
        return safeDetector;
    }

}
