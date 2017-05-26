package center.control.system.vash.controlcenter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.util.Timer;
import java.util.TimerTask;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.utils.Facedetect;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.utils.VolleySingleton;

/**
 * Created by Thuans on 5/26/2017.
 */

public class ReadSensorService extends Service {
    private static final String TAG = "---Read Sensor---";
    private static Camera camera= null;
    private static Camera.PictureCallback mCallBack;
    private static Timer repeatScheduler;
    @Override
    public void onCreate() {
        super.onCreate();
        int cameraId = findFrontFacingCamera();
        final SurfaceView preview = new SurfaceView(this);
        final SurfaceHolder holder = preview.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final WindowManager wm = (WindowManager)this
                .getSystemService(Context.WINDOW_SERVICE);


        camera = Camera.open(cameraId);
        Log.i(TAG,"Opened camera");
        mCallBack =new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap tmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                SmartHouse smartHouse = SmartHouse.getInstance();
                smartHouse.updateCameraArea(smartHouse.getAreas().get(0).getId(),tmp);
            }
        };
        final Context context = this;
        repeatScheduler = new Timer();
        repeatScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                takePhoto(context, holder,wm,preview);
                SmartHouse smartHouse = SmartHouse.getInstance();
                for (AreaEntity area: smartHouse.getAreas()){
                    final int currentAreaId = area.getId();
                    StringRequest readRoom = new StringRequest(Request.Method.GET, VolleySingleton.DEMO_ARDUINO_ADDRESS,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                response = "7:on,A0:25,A1:open";
                                SmartHouse.getInstance().updateSensorArea(currentAreaId,response);
                            }
                        }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,error.getMessage());
                        }
                    });
                    VolleySingleton.getInstance(context).addToRequestQueue(readRoom);
                }

            }
        }, 0, 3000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("deprecation")
    private static void takePhoto(final Context context,SurfaceHolder holder, WindowManager wm, SurfaceView preview) {
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.set("orientation", "portrait");
                parameters.setRotation(90);
                camera.setParameters(parameters);
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                    camera.takePicture(null, null, mCallBack);
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    e.printStackTrace();
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}


        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,PixelFormat.UNKNOWN);
        wm.addView(preview, params);
    }
    private static int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.i(TAG, "Camera found "+i);
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    @Override
    public void onDestroy(){
        Toast.makeText(this, "Stop read sensor", Toast.LENGTH_SHORT).show();
        if (camera != null){
            camera.release();
            Log.i(TAG,"release CAMERA FOR PHONE");
        }
        if (repeatScheduler !=null){
            repeatScheduler.cancel();
        }
        stopSelf();
    }
}
