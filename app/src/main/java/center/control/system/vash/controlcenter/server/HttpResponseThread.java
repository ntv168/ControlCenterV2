package center.control.system.vash.controlcenter.server;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Thuans on 4/19/2017.
 */

public class HttpResponseThread extends Thread {
    private static final String AREA_REQ = "area";
    private static final String AREA_ATTRIBUTE_REQ = "getAreaSensor";
    private static final String AREA_DEVICE = "getAreaDevice";
    private static final String MODE_REQ = "getMode";
    private static final String ACTIVE_MODE = "activateMode";
    private static final String MODE_DEVICE = "getModeDevice";
    private static final String MODE_TODAY = "getModeToday";
    private static final String RUN_MODE_TODAY = "modeToday";
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
                request = request.split(" ")[1];
                Log.d(TAG,"req:  " +request);
                SmartHouse house = SmartHouse.getInstance();
                if (request.contains(AREA_REQ)){
                    for (AreaEntity area : house.getAreas()){
                        response += area.getName()+"="+area.getId()+";";
                    }
                } else if (request.contains(AREA_ATTRIBUTE_REQ)){
                    String areaId = request.substring(AREA_ATTRIBUTE_REQ.length()+2);
                    Log.d(TAG,areaId);
//                    AreaEntity area = house.getAreaById(Integer.parseInt(areaId));
//                    for (String attr: area.generateValueArr()){
//                        response += '"'+attr+"\",";
//                    }
                }
                else if (request.contains(AREA_DEVICE)){
                    String[] param = request.split("/");
                    Log.d(TAG,request);
//                    "deviceName1=deviceId1=deviceType1=deviceStatus1=[security,temperature,light,sound,runningDevice];"
//                    AreaEntity area = house.getAreaById(Integer.parseInt(areaId));
//                    for (DeviceEntity device : house.getDevicesByAreaId(Integer.parseInt(areaId))){
//                        response += '"'+device.getName()+"\"="+device.getId()+",";
//                    }
                } else if (request.contains(ACTIVE_MODE)){
                    String areaId = request.substring(ACTIVE_MODE.length()+2);
                    Log.d(TAG,areaId);
//                    AreaEntity area = house.getAreaById(Integer.parseInt(areaId));
//                    for (DeviceEntity device : house.getDevicesByAreaId(Integer.parseInt(areaId))){
//                        response += '"'+device.getName()+"\"="+device.getId()+",";
//                    }
                } else if (request.contains(MODE_DEVICE)){
                    String areaId = request.substring(MODE_DEVICE.length()+2);
                    Log.d(TAG,areaId);
//                    AreaEntity area = house.getAreaById(Integer.parseInt(areaId));
//                    for (DeviceEntity device : house.getDevicesByAreaId(Integer.parseInt(areaId))){
//                        response += '"'+device.getName()+"\"="+device.getId()+",";
//                    }
                }else if (request.contains(MODE_REQ)){
                    response += "Thưa ông chủ ông có muốn bật đèn trước sân không ạ?";
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
