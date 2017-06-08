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

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Thuans on 4/19/2017.
 */

public class HttpResponseThread extends Thread {
    private static final String AREA_REQ = "area";
    private static final String AREA_ATTRIBUTE_REQ = "areaAttribute";
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

                if (request.contains(AREA_REQ)){
                    SmartHouse house = SmartHouse.getInstance();
                    for (AreaEntity area : house.getAreas()){
                        response += '"'+area.getName()+"\",";
                    }
                    Log.d(TAG, request.substring(5,9));
                } else if (request.contains(AREA_ATTRIBUTE_REQ)){
                     String areaId = request.substring(20,request.length()-1);
                    Log.d(TAG, areaId);
                }
//                String content = "\"Phòng ăn\"=1,\"Phòng khách\"=2,\"Sân\"=3,";
//                response = content;
            }

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
