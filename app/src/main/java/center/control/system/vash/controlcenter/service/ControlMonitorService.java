package center.control.system.vash.controlcenter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Timer;
import java.util.TimerTask;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.panel.ControlPanel;

import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.server.VolleySingleton;

/**
 * Created by Thuans on 5/26/2017.
 */

public class ControlMonitorService extends Service {
    private static final String TAG = "---Read Sensor---";
    public static final String CONTROL = "control.action";
    public static final String WAIT = "wait.action";
    public static final String MONITOR = "monitor.action";
    public static final String CAMERA = "camera.action";
    public static final int SUCCESS = -3;
    public static final int FAIL = -2;
    public static final String DEACTIVATE = "deactivate.action";
    public static final String NOBODY = "Nobody";
    public static final String NOT_SUPPORT = "None";
    private static Timer repeatScheduler;
    private LocalBroadcastManager broadcaster;
    private boolean areaChecked = false;

    public void sendResult(String message, int areaId) {
        Intent intent = new Intent(ControlPanel.CONTROL_FILTER_RECEIVER);
        intent.putExtra(ControlPanel.ACTION_TYPE, message);
        intent.putExtra(ControlPanel.AREA_ID, areaId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendControl(final DeviceEntity deviceEntity, final String status){
        if (SmartHouse.getAreaById(deviceEntity.getAreaId()) != null) {
            sendResult(WAIT,-1);
            String url = "http://" + SmartHouse.getAreaById(deviceEntity.getAreaId()).getConnectAddress().trim()
                    + "/" + deviceEntity.getPort().trim() + "/" + status.trim();
            Log.d(TAG, url);
            StringRequest control = new StringRequest(Request.Method.GET,
                    url,new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG,"send success");
                            if (status.equals("on") || status.equals("off")) {
                                SmartHouse house = SmartHouse.getInstance();
                                deviceEntity.setState(status);
                                house.updateDeviceStateById(deviceEntity.getId(),status);
                                Log.d(TAG,deviceEntity.getName()+ " với lệnh: " + status);
                            }
                            sendResult(CONTROL,SUCCESS);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG,"send fail");
//                    if (status.equals("on") || status.equals("off")) {
//                        SmartHouse house = SmartHouse.getInstance();
//                        deviceEntity.setState(status);
//                        house.updateDeviceStateById(deviceEntity.getId(),status);
//                        Log.d(TAG,deviceEntity.getName()+ " với lệnh: " + status);
//                    }
                    sendResult(CONTROL,SUCCESS);
                }
            });
            control.setRetryPolicy(new DefaultRetryPolicy(1000,0,1f));

            VolleySingleton.getInstance(this).addToRequestQueue(control);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;
        broadcaster = LocalBroadcastManager.getInstance(context);
        repeatScheduler = new Timer();
        Log.d(TAG,"Start service");
        repeatScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                SmartHouse smartHouse = SmartHouse.getInstance();
                try {
                    if (smartHouse.getContractId() == null){
                        sendResult(DEACTIVATE,-1);
                        return;
                    }
                    if (smartHouse.getOwnerCommand().size() == 0){
                        for (AreaEntity area: smartHouse.getAreas()){
                            if (area.isHasCamera() && areaChecked) {
                                checkCamera(area);
                            } else if (!areaChecked) {
                                checkArea(area);
                            }
                        }
                        areaChecked = !areaChecked;
                    } else {
                        CommandEntity command = smartHouse.getOwnerCommand().take();
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
        }, VolleySingleton.CHECK_CAMERA_TIMEOUT, ConstManager.SERVICE_PERIOD);
    }

    private  void checkArea(final AreaEntity area){
        String url ="http://"+ area.getConnectAddress()+"/check";
        Log.d(TAG,url);
        StringRequest readRoom = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = VolleySingleton.fixEncodingUnicode(response);
                        Log.d(TAG,response);
                        SmartHouse.getInstance().updateSensorArea(area.getId(),response);
                        sendResult(MONITOR,area.getId());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String response =  "security:có người lạ,light:phòng sáng,tempurature:12,sound:to";
                Log.d(TAG,response);
                SmartHouse.getInstance().updateSensorArea(area.getId(),response);
                sendResult(MONITOR,area.getId());
            }
        });
        readRoom.setRetryPolicy(new DefaultRetryPolicy(VolleySingleton.CHECK_AREA_TIMEOUT,0,1f));
        VolleySingleton.getInstance(this).addToRequestQueue(readRoom);
    }
    private void checkCamera(final AreaEntity area){

        String url ="http://"+ area.getConnectAddress()+"/camera";
        Log.d(TAG,url);StringRequest readRoom = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.length()+"");
                        if (response.trim().equals(NOBODY)) {
                            SmartHouse.getInstance().updateSensorArea(area.getId(), "security:Không thấy ai cả");
                            sendResult(MONITOR, area.getId());
                            return;
                        }else if (response.trim().equals(NOT_SUPPORT)) {
                            SmartHouse.getInstance().removeCameraArea(area.getId());
                            return;
                        } else if (response.length() > 10) {
                            byte[] decodedString = Base64.decode(response, Base64.NO_WRAP);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            SmartHouse.getInstance().updatePictureArea(area.getId(), decodedByte);
                            sendResult(CAMERA, area.getId());
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SmartHouse.getInstance().removeCameraArea(area.getId());
            }
        });
        readRoom.setRetryPolicy(new DefaultRetryPolicy(VolleySingleton.CHECK_CAMERA_TIMEOUT,0,1f));
        VolleySingleton.getInstance(this).addToRequestQueue(readRoom);

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy(){
        Toast.makeText(this, "Stop read sensor", Toast.LENGTH_SHORT).show();
        if (repeatScheduler !=null){
            repeatScheduler.cancel();
        }
        stopSelf();
    }

}

