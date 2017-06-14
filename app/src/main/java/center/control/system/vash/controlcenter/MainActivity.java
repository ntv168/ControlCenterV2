package center.control.system.vash.controlcenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.server.VolleySingleton;

public class MainActivity extends Activity {
    private static final String TAG = "---Main Activity---";
    SharedPreferences sharedPreferences;
    private String houseId;
    private String username;
    private String password;
    private String staticAddress;
    private String contractId;
    private String contractCode;
    private String ownerName;
    private String ownerAddress;
    private String ownerTel;
    private String ownerCmnd;
    private String systemId;
    private String activeDay;
    private String pricePlan;
    private String virtualAssistantName;
    private String virtualAssistantType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        sharedPreferences =getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        String usename = sharedPreferences.getString(ConstManager.USERNAME,"");
        ((EditText) findViewById(R.id.txtUsername)).setText(usename);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSmartHouse();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        systemId = sharedPreferences.getString(ConstManager.SYSTEM_ID,"");
        if (!systemId.equals("")){
            Intent intent = new Intent(MainActivity.this, ControlPanel.class);
            startActivity(intent);
        }
    }

    private void loginSmartHouse() {
        EditText username = (EditText) findViewById(R.id.txtUsername);
        EditText password = (EditText) findViewById(R.id.txtPassword);

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(ConstManager.USERNAME,username.getText().toString());
        edit.commit();
        Log.d(TAG,"Login :"+ username.getText()+ password.getText()+ " send "+VolleySingleton.LOGIN_HOUSE_API);
        final JsonObjectRequest loginJson = new JsonObjectRequest(Request.Method.GET,
                VolleySingleton.LOGIN_HOUSE_API, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                SharedPreferences.Editor edit = sharedPreferences.edit();

                edit.putString(ConstManager.SYSTEM_ID,houseId);
                edit.commit();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT);
                Intent i = new Intent(MainActivity.this, ControlPanel.class);
                startActivity(i);
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(loginJson);
    }

}
