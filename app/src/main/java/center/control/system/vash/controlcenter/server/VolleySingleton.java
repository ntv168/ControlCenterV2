package center.control.system.vash.controlcenter.server;

import android.content.Context;
import android.text.Html;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

/**
 * Created by Thuans on 5/26/2017.
 */

public class VolleySingleton {

    public static final int CHECK_AREA_TIMEOUT = 2000;
    public static final int CHECK_CAMERA_TIMEOUT = 3000;
    public static  String SERVER_HOST = "http://54.255.183.91:8080/";
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context context;

    private VolleySingleton(Context context) {
        this.context = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public static String fixEncodingUnicode(String response) {
        String str = "";
        try {
            str = new String(response.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        String decodedStr = Html.fromHtml(str).toString();
        return  decodedStr;
    }
}
