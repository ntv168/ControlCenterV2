package center.control.system.vash.controlcenter.server;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import center.control.system.vash.controlcenter.area.AreaEntity;

import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Thuans on 4/19/2017.
 */

public class HttpResponseThread extends Thread {
    private static final String AREA_REQ = "area";
    private static final String AREA_ATTRIBUTE_REQ = "getAreaSensor";
    private static final String AREA_CAMERA_REQ = "getAreaCamera";
    private static final String AREA_DEVICE = "getAreaDevice";
    private static final String MODE_REQ = "getAllMode";
    private static final String ACTIVE_MODE = "activateMode";
    private static final String MODE_DEVICE = "getModeDevice";
    private static final String ON_DEVICE = "deviceOn";
    private static final String OFF_DEVICE = "deviceOff";
    private static final String MODE_TODAY = "getModeToday";
    private static final String RUN_MODE_TODAY = "modeToday";
    private static final String ACTIVATE_CONFIG = "activeConfig";
    private static final String UPDATE_PERSON = "updatePerson";
    private static final String DEACTIVATE_CONFIG = "cancelConfig";
    private static final String RESPONSE_SUCCESS = "tung=success";
    private static final String DATABASE_VERS = "databaseVersion";
    private static final String NEWUPDATE = "newUpdate";
    private static final String BOTUPDATE = "botUpdate";
    private static final String DEACTIVATE = "deactivateSystem";
    private static final String SEND_MESSAGE = "sendMessage";
    private final String TAG = "HttpResponseThread";
    Socket socket;

    HttpResponseThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader is;
        OutputStream os;
        String request;

        try {
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = is.readLine();
            String response = "";
            os = socket.getOutputStream();
            if (request != null ) {
                Log.d(TAG,"req:  " +request);
                request = request.split(" ")[1];
                SmartHouse house = SmartHouse.getInstance();
                if (request.contains(AREA_REQ)){
                    for (AreaEntity area : house.getAreas()){
                        response += area.getName()+"="+area.getId()+";";
                    }
                } else if (request.contains(AREA_ATTRIBUTE_REQ)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"area id:  "+reqElement[2]);
                    if (!house.isDefaultState()) {
                        response += "config;"+house.getCurrentState().getNoticePattern()+
                                ";"+house.getCurrentState().getName();
                    } else {
                        AreaEntity area = SmartHouse.getAreaById(Integer.parseInt(reqElement[2]));
                        response += area.generateAttributeForApi();
                    }
                }
                else if (request.contains(AREA_CAMERA_REQ)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"area id:  "+reqElement[2]);
                    AreaEntity area = SmartHouse.getAreaById(Integer.parseInt(reqElement[2]));
//                    response += area.getImageBitmap();
                    if (area.getImageBitmap() != null) {
                        ByteArrayOutputStream osb = new ByteArrayOutputStream();
                        area.getImageBitmap().compress(Bitmap.CompressFormat.JPEG, 100, osb);
                        String encoded = Base64.encodeToString(osb.toByteArray(), Base64.NO_WRAP);
                        response = encoded;
                    }

                }
                else if (request.contains(AREA_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"area id:  "+reqElement[2]);
                    response += house.generateDeviceByAreaForApi(Integer.parseInt(reqElement[2]));
                    Log.d(TAG,response);
                } else if (request.contains(ACTIVE_MODE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"device id:  "+reqElement[2]);
                    ScriptEntity item = SmartHouse.getInstance().getModeById(Integer.parseInt(reqElement[2]));
                    DetectFunctionEntity funct = DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_START_MODE);
                    CurrentContext.getInstance().setDetectedFunction(funct);
                    CurrentContext.getInstance().setScript(item);
                    for (CommandEntity cmd : ScriptSQLite.getCommandByScriptId(Integer.parseInt(reqElement[2]))){
                        house.addCommand(cmd);
                    }
                    response += RESPONSE_SUCCESS;
                } else if (request.contains(ON_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"device id:  "+reqElement[2]);
                    house.addCommand(new CommandEntity(Integer.parseInt(reqElement[2]),"on"));
                    CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_ON));
                    CurrentContext.getInstance().setDevice(house.getDeviceById(Integer.parseInt(reqElement[2])));
                    response += RESPONSE_SUCCESS;
                } else if (request.contains(OFF_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"device id:  "+reqElement[2]);
                    house.addCommand(new CommandEntity(Integer.parseInt(reqElement[2]),"off"));
                    CurrentContext.getInstance().setDevice(house.getDeviceById(Integer.parseInt(reqElement[2])));
                    CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_OFF));
                    response += RESPONSE_SUCCESS;
                } else if (request.contains(MODE_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"mode id:  "+ reqElement[2]);
                    response += "";
                }else if (request.contains(MODE_REQ)){
                    for (ScriptEntity script : house.getScripts()){
                        response += script.getName()+"="+script.getId()+";";
                    }
                    response += "";
                } else if (request.contains(DATABASE_VERS)){
                    response += "ver=6";
                }else if (request.contains(DEACTIVATE)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].contains(house.getContractId())) {
                            SmartHouse.getInstance().setContractId(null);
                        }
                    }
                }else if (request.contains(SEND_MESSAGE)) {
                    String[] reqElement = request.split("/");
                    Log.d(TAG, "h Id :  " + reqElement[2]);
                    Log.d(TAG, "message :  " + reqElement[3]);
                    response += BotUtils.botReplyToSentence(reqElement[3]);
                }else if (request.contains(MODE_TODAY)){
                    for (ScriptEntity script : house.getRunToday()){
                        response += script.getName()+"="+script.getId()+"="+
                                (script.isEnabled()?"on":"off")+"="+script.getHour()+":"+script.getMinute()+";";
                    }
                }else if (request.contains(NEWUPDATE)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].equals(house.getContractId())) {
                            SmartHouse.getInstance().setRequireUpdate(true);
                        }
                    }
                }else if (request.contains(BOTUPDATE)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].equals(house.getContractId())) {
                            SmartHouse.getInstance().setRequireBotUpdate(true);
                        }
                    }
                }else if (request.contains(UPDATE_PERSON)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].equals(house.getContractId())) {
                            SmartHouse.getInstance().setRequirePersonUpdate(true);
                            response += "success";
                        }
                    }
                }  else if (request.contains(ACTIVATE_CONFIG)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].equals(house.getContractId())) {
                            SmartHouse.getInstance().startConfigCmds();
                            SmartHouse.getInstance().setStateChangedTime(
                                    (new Date().getTime()) - SmartHouse.getInstance().getCurrentState().getDelaySec()*1000);
                        }
                    }
                } else if (request.contains(DEACTIVATE_CONFIG)) {
                    String[] reqElement = request.split("/");
                    if (reqElement.length>=3) {
                        Log.d(TAG, "Code :  " + reqElement[2]);
                        if (reqElement[2].equals(house.getContractId())) {
                            SmartHouse.getInstance().resetStateToDefault();
                        }
                    }
                }
            }
            Log.d(TAG,response);

            os.write(("HTTP/1.0 200" + "\r\n").getBytes());
            os.write(("Content type: text/html" + "\r\n").getBytes());
            os.write(("Content length: " + response.length() + "\r\n").getBytes());
            os.write(("\r\n").getBytes());
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.write(("\r\n").getBytes());
            os.flush();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }
}
