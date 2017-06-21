package center.control.system.vash.controlcenter.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.panel.SettingPanel;
import center.control.system.vash.controlcenter.server.WebServer;

/**
 * Created by Thuans on 6/5/2017.
 */

public class WebServerService extends Service {

    private static final String TAG = "Web socket service --- ";
    public static final String SERVER_SUCCESS = "Server start success";
    private WebServer server;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void sendMessageToActivity(String msg) {
        Intent intent = new Intent(ControlPanel.CONTROL_FILTER_RECEIVER);
        intent.putExtra(ControlPanel.ACTION_TYPE, msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        Log.w(TAG, "starting server.....");
        final int port = 8080;
        server = new WebServer(port);

        (new Thread(server)).start();
        sendMessageToActivity(SERVER_SUCCESS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        server.stop();
        Log.w(TAG, "stopped server.....");
    }
}
