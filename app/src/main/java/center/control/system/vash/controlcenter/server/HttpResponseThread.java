package center.control.system.vash.controlcenter.server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.area.AreaEntity;

import center.control.system.vash.controlcenter.configuration.CommandEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.panel.SettingPanel;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.http.Path;

/**
 * Created by Thuans on 4/19/2017.
 */

public class HttpResponseThread extends Thread {
    private static final String AREA_REQ = "area";
    private static final String AREA_ATTRIBUTE_REQ = "getAreaSensor";
    private static final String AREA_DEVICE = "getAreaDevice";
    private static final String MODE_REQ = "getAllMode";
    private static final String ACTIVE_MODE = "activateMode";
    private static final String MODE_DEVICE = "getModeDevice";
    private static final String ON_DEVICE = "deviceOn";
    private static final String OFF_DEVICE = "deviceOff";
    private static final String MODE_TODAY = "getModeToday";
    private static final String RUN_MODE_TODAY = "modeToday";
    private static final String RESPONSE_SUCCESS = "tung=success";
    private static final String DATABASE_VERS = "databaseVersion";
    private static final String RESPONSE_FAIL = "tung=fail";
    private static final String DEACTIVATE = "deactivateSystem";
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
//                request = request.split(" ")[1];
                SmartHouse house = SmartHouse.getInstance();
                if (request.contains(AREA_REQ)){
                    for (AreaEntity area : house.getAreas()){
                        response += area.getName()+"="+area.getId()+";";
                    }
                } else if (request.contains(AREA_ATTRIBUTE_REQ)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"area id:  "+reqElement[2]);
                    AreaEntity area = SmartHouse.getAreaById(Integer.parseInt(reqElement[2]));
                    response +=  area.generateAttributeForApi();
                }
                else if (request.contains(AREA_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"area id:  "+reqElement[2]);
                    response += house.generateDeviceByAreaForApi(Integer.parseInt(reqElement[2]));
                    Log.d(TAG,response);
                } else if (request.contains(ON_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"device id:  "+reqElement[2]);
                    house.addCommand(new CommandEntity(Integer.parseInt(reqElement[2]),"on"));
                    response += RESPONSE_SUCCESS;
                } else if (request.contains(OFF_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"device id:  "+reqElement[2]);
                    house.addCommand(new CommandEntity(Integer.parseInt(reqElement[2]),"off"));
                    response += RESPONSE_SUCCESS;
                } else if (request.contains(MODE_DEVICE)){
                    String[] reqElement  = request.split("/");
                    Log.d(TAG,"mode id:  "+ reqElement[2]);
                    response += "";
                }else if (request.contains(MODE_REQ)){
                    String[] reqElement  = request.split("/");
                    for (ScriptEntity script : house.getScripts()){
                        response += script.getName()+"="+script.getId()+";";
                    }
                    response += "";
                } else if (request.contains(DATABASE_VERS)){
                    response += "ver=6";
                }else if (request.contains(DEACTIVATE)) {
                    String[] reqElement = request.split("/");
                    Log.d(TAG, "mode id:  " + reqElement[2]);
                    if (reqElement[2].equals(house.getContractId())) {
                        SmartHouse.getInstance().setContractId(null);
                    }
                }else if (request.contains(MODE_TODAY)){
                    response += "Thức dậy buổi sáng=1=on=06:30;Đi làm=2=on=12:35;Ăn tối với cả nhà=3=on=17:00";
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
