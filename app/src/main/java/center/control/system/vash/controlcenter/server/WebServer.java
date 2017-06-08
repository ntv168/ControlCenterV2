package center.control.system.vash.controlcenter.server;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Thuans on 4/19/2017.
 */

public class WebServer implements Runnable {
    private static final String TAG = "SimpleWebServer";

    private final int mPort;

    private boolean mIsRunning;

    private ServerSocket serverSocket;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WebServer(int port) {
        mPort = port;
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != serverSocket) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    public int getPort() {
        return mPort;
    }

    @Override
    public void run() {
        Log.d(TAG,"Start thread");
        mIsRunning = true;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(getPort());

            while(true){
                socket = serverSocket.accept();

                HttpResponseThread httpResponseThread =
                        new HttpResponseThread(
                                socket);
                httpResponseThread.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
