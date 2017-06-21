package center.control.system.vash.controlcenter.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.microsoft.projectoxford.face.FaceServiceClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.recognition.ImageHelper;
import center.control.system.vash.controlcenter.script.ScriptDeviceEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.server.VolleySingleton;

/**
 * Created by Thuans on 5/26/2017.
 */

public class ControlMonitorService extends Service {
    private static final String TAG = "---Read Sensor---";
    public static final String CONTROL = "control.action";
    public static final String MONITOR = "monitor.action";
    private static Camera camera= null;
    private static Camera.PictureCallback mCallBack;
    private static Timer repeatScheduler;
    private LocalBroadcastManager broadcaster;

    public void sendResult(String message) {
        Intent intent = new Intent(ControlPanel.CONTROL_FILTER_RECEIVER);
        intent.putExtra(ControlPanel.ACTION_TYPE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void sendControl(DeviceEntity deviceEntity, final String status){
        if (SmartHouse.getAreaById(deviceEntity.getAreaId()) != null) {
            String url = "http://" + SmartHouse.getAreaById(deviceEntity.getAreaId()).getConnectAddress().trim()
                    + "/" + deviceEntity.getPort().trim() + "/" + status.trim();
            Log.d(TAG, url);
            StringRequest control = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            if (status.equals("on") || status.equals("off")) {
                SmartHouse house = SmartHouse.getInstance();
                deviceEntity.setState(status);
                house.updateDeviceStateById(deviceEntity.getId(),status);
                Log.d(TAG,deviceEntity.getName()+ " với lệnh: " + status);
                sendResult(CONTROL);
            }
            VolleySingleton.getInstance(this).addToRequestQueue(control);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
//        openBackCamera();
//        final SurfaceView preview = new SurfaceView(this);
//        final SurfaceHolder holder = preview.getHolder();
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        final WindowManager wm = (WindowManager) this
//                .getSystemService(Context.WINDOW_SERVICE);
//
//        Log.i(TAG, "Opened camera");

        final Context context = this;
        broadcaster = LocalBroadcastManager.getInstance(context);
        repeatScheduler = new Timer();
        repeatScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
//                takePhoto(context, holder,wm,preview);
                SmartHouse smartHouse = SmartHouse.getInstance();
                try {
                    ScriptDeviceEntity command = smartHouse.getOwnerCommand().take();
                    if (command == null){
                        for (AreaEntity area: smartHouse.getAreas()){
                            Log.d(TAG,"check hang " +area.getName() + " "+area.getConnectAddress().trim()+"/check");
                            checkArea(area);
                        }
                    } else {
                        DeviceEntity device = smartHouse.getDeviceById(command.getDeviceId());
                        if (device != null) {
                            sendControl(device,command.getDeviceState());
                        } else {
                            Log.d(TAG," null cmnr với lệnh: " + command.getDeviceState());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }, 0, 3000);
    }

    private  void checkArea(AreaEntity area){

        String url = area.getConnectAddress()+"/check";
        Log.d(TAG,url);
//            StringRequest readRoom = new StringRequest(Request.Method.GET, area.getConnectAddress(),
//                    new Response.Listener<String>(){
//                        @Override
//                        public void onResponse(String response) {
////                                    response = "7:on,A0:25,A1:open";
////                                    SmartHouse.getInstance().updateSensorArea(currentAreaId,response);
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    Log.e(TAG,error.getMessage());
//                }
//            });
//            VolleySingleton.getInstance(null).addToRequestQueue(readRoom);
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
    private void openBackCamera() {
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
//        camera = Camera.open(cameraId);
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

