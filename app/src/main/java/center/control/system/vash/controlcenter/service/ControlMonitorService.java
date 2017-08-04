package center.control.system.vash.controlcenter.service;

import android.app.Service;
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.panel.ControlPanel;

import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.utils.BotUtils;
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
    public static final String NEW_UPDATE = "new config update";
    public static final String BOT_UPDATE = "new bot update";
    public static final String SCHEDULER = "scheduler trig";
    private static Timer repeatScheduler;
    private boolean areaChecked = false;
    public static String CHANGE_STATE = "state.change";

    public void sendResult(String message, int areaId) {
        Intent intent = new Intent(ControlPanel.CONTROL_FILTER_RECEIVER);
        intent.putExtra(ControlPanel.ACTION_TYPE, message);
        intent.putExtra(ControlPanel.AREA_ID, areaId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void sendControl(final DeviceEntity deviceEntity, final String status){
        if (SmartHouse.getAreaById(deviceEntity.getAreaId()) != null
                && !deviceEntity.getState().equals(status)) {
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
                                sendResult(CONTROL, SUCCESS);
                            }
                            if (CurrentContext.getInstance().finishCurrentScript(deviceEntity.getId())) {
                                sendResult(CONTROL, SUCCESS);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG,"send fail");
                    sendResult(CONTROL,FAIL);
                }
            });
            control.setRetryPolicy(new DefaultRetryPolicy(1000,0,1f));

            VolleySingleton.getInstance(this).addToRequestQueue(control);
        } else {
            Log.d(TAG,deviceEntity.getName()+" đã được "+deviceEntity.getState());
            sendResult(CONTROL, SUCCESS);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        repeatScheduler = new Timer();
        Log.d(TAG,"Start service");
        repeatScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                SmartHouse smartHouse = SmartHouse.getInstance();
                boolean checkConfig = false;
                if (smartHouse.getContractId() == null){
                    sendResult(DEACTIVATE,-1);
                    return;
                } else  if (smartHouse.isRequireBotUpdate()) {
                    sendResult(BOT_UPDATE,-1);
                }else  if (smartHouse.isRequireUpdate()) {
                    sendResult(NEW_UPDATE,-1);
                }
                if (smartHouse.getCurrentState()!= null &&
                        !smartHouse.isDefaultState()){
                    long waitedTime = ((new Date()).getTime() - smartHouse.getStateChangedTime())/1000;
                    Log.d(TAG,waitedTime+ " Delay:  "+smartHouse.getCurrentState().getDelaySec()+" "+smartHouse.getCurrentState().getDuringSec()+"" +
                            " "+smartHouse.getCurrentState().getName());

                    if (waitedTime < (smartHouse.getCurrentState().getDuringSec() + smartHouse.getCurrentState().getDelaySec()) ) {
                        Log.d(TAG,"Cấu hình chờ lệnh");
                        checkConfig = true;
                    }
                    if (waitedTime >= smartHouse.getCurrentState().getDelaySec() &&
                            !smartHouse.getCurrentState().isActivated()){
                        Log.d(TAG,"Cấu hình tự động kích hoạt : "+smartHouse.getCurrentState().getName());
                        smartHouse.startConfigCmds();
                        smartHouse.getCurrentState().setActivated(true);
                    } else
                    if ( waitedTime >= (smartHouse.getCurrentState().getDuringSec() + smartHouse.getCurrentState().getDelaySec())
                            && smartHouse.getCurrentState().getDuringSec() != ConstManager.DURING_MAX
                            && !smartHouse.isDefaultState()){
                        Log.d(TAG,smartHouse.getCurrentState().getName()+ " Cấu hình tự động chuyển time out ");
                        smartHouse.revertCmdState();
//                        int nextStId = smartHouse.getCurrentState().getDefautState();
//                        Log.d(TAG, smartHouse.getCurrentState().getEvents().size()+ " s next state : "+ nextStId);
//                        if (nextStId != -1 ){
//                            smartHouse.setCurrentState(smartHouse.getStateById(nextStId));
//                            smartHouse.setStateChangedTime((new Date()).getTime());
//                            sendResult(CHANGE_STATE,-1);
//                        }
                        smartHouse.resetStateToDefault();
                        sendResult(CHANGE_STATE,-1);
                    }
                }
                if (smartHouse.getOwnerCommand().size() > 0) {
                    Log.d(TAG, "thuc hien lenh");
                    try {
                        CommandEntity command = smartHouse.getOwnerCommand().take();
                        DeviceEntity device = smartHouse.getDeviceById(command.getDeviceId());
                        if (device != null) {
                            Log.d(TAG, device.getName() + " thao tac "+ command.getDeviceState());
                            CurrentContext.getInstance().setDevice(device);
                            if (command.getDeviceState().equals("on")) {
                                CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_ON));
                             } else if (command.getDeviceState().equals("off")) {
                                CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_OFF));
                            }
                            sendControl(device, command.getDeviceState());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    if (checkConfig){
                        Set<Integer> areaIds = new HashSet<Integer>();
                        for (EventEntity event : smartHouse.getCurrentState().getEvents()) {
                            if (SmartHouse.getAreaById(event.getAreaId())!= null) {
                                if (!areaIds.contains(event.getAreaId())) {
                                    areaIds.add(event.getAreaId());
                                    if (event.getSenName().equals(AreaEntity.attrivutesValues[3])) {
                                        checkCamera(SmartHouse.getAreaById(event.getAreaId()));
                                    } else {
                                        checkArea(SmartHouse.getAreaById(event.getAreaId()));
                                    }
                                }
                            } else {
                                Log.d(TAG," chưa đặt không gian cho cấu hình");
                            }

                        }
                    } else {
                        for (AreaEntity area : smartHouse.getAreas()) {
                            Log.d(TAG, area.getName() + "   " + area.isHasCamera());
                            if (area.isHasCamera() && areaChecked) {
                                checkCamera(area);
                            } else if (!areaChecked) {
                                checkArea(area);
                            }
                        }
                        areaChecked = !areaChecked;
                    }
                }
                for (ScriptEntity todayMode : smartHouse.getRunToday()){
                    if (todayMode.isEnabled() && todayMode.getHour()==((new Date()).getHours())
                            && todayMode.getMinute()<=((new Date()).getMinutes())){
                        DetectFunctionEntity funct = DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_START_MODE);
                        CurrentContext cont = CurrentContext.getInstance();
                        cont.setDetectedFunction(funct);
                        cont.setDevice(null);
                        cont.setSchedulerMode(true);
                        cont.setScript(todayMode);
                        sendResult(SCHEDULER,-1);
                    }
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
                            if (area.getUpdatePerson() == -1 || (new Date()).getTime() - area.getUpdatePerson() > AreaEntity.HOLD_PERSON) {
                                area.setDetect(AreaEntity.NOBODY);
                                SmartHouse.getInstance().updateAreaById(area.getId(), area);
                                sendResult(MONITOR, area.getId());
                            }
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

