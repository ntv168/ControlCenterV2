package center.control.system.vash.controlcenter;

import android.app.Activity;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.HouseKeyDTO;
import center.control.system.vash.controlcenter.server.LoginSmarthouseDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends Activity {
    private static final String TAG = "---Main Activity---";
    SharedPreferences sharedPreferences;
    private String username;
    private String password;
    private String houseId;
    private String staticAddress;
    private String contractCode;
    private String ownerName;
    private String ownerAddress;
    private String ownerTel;
    private String ownerCmnd;
    private String contractId;
    private String activeDay;
    private String virtualAssistantName;
    private String virtualAssistantType;
    private ProgressDialog loginDia;
    EditText txtusername;
    EditText txtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
          txtusername = (EditText) findViewById(R.id.txtUsername);
          txtpassword = (EditText) findViewById(R.id.txtPassword);

        txtpassword.setText(password);
        txtusername.setText(username);
        sharedPreferences =getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        String usename = sharedPreferences.getString(ConstManager.USERNAME,"");
        ((EditText) findViewById(R.id.txtUsername)).setText(usename);
        loginDia = new ProgressDialog(this);
        loginDia.setTitle("Đăng nhập vào hệ thống");
        loginDia.setMessage("Vui lòng đợi");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDia.show();
                loginSmartHouse();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        houseId = sharedPreferences.getString(ConstManager.SYSTEM_ID,"");
        if (!houseId.equals("")){
            Intent intent = new Intent(MainActivity.this, ControlPanel.class);
            startActivity(intent);
        }
        username = sharedPreferences.getString(ConstManager.USERNAME,"");
        password = sharedPreferences.getString(ConstManager.PASSWORD,"");
    }

    private void loginSmartHouse() {
        final HouseKeyDTO key = new HouseKeyDTO();
        key.setUsername(txtusername.getText().toString());
        key.setPassword(txtpassword.getText().toString());
        final CloudApi loginApi = RetroFitSingleton.getInstance().getCloudApi();
        loginApi.mobileLogin(key).enqueue(new Callback<LoginSmarthouseDTO>() {
            @Override
            public void onResponse(Call<LoginSmarthouseDTO> call, retrofit2.Response<LoginSmarthouseDTO> response) {
                Log.d(TAG,call.request().url()+" --- "+response.body().getHouseId());
                if (response.body().getHouseId()!= null) {
                    username = key.getUsername();
                    password = key.getPassword();
                    houseId = response.body().getHouseId();
                    staticAddress = response.body().getStaticAddress();
                    contractCode = response.body().getContractCode();
                    ownerName = response.body().getOwnerName();
                    ownerAddress = response.body().getOwnerAddress();
                    ownerTel = response.body().getOwnerTel();
                    ownerCmnd = response.body().getOwnerCmnd();
                    contractId = response.body().getContractId();
                    activeDay = response.body().getActiveDay();
                    virtualAssistantName = response.body().getVirtualAssistantName();
                    virtualAssistantType = response.body().getVirtualAssistantType();
                    Intent i = new Intent(MainActivity.this, ControlPanel.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this,"Sai tên đăng nhập mật khẩu",Toast.LENGTH_LONG);
                }
                loginDia.dismiss();
            }

            @Override
            public void onFailure(Call<LoginSmarthouseDTO> call, Throwable t) {
                Log.d(TAG,call.request().url()+"  ---err "+t.getMessage());
                Toast.makeText(MainActivity.this,"Sai tên đăng nhập mật khẩu",Toast.LENGTH_LONG);
                loginDia.dismiss();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(ConstManager.SYSTEM_ID,houseId);
        edit.putString(ConstManager.USERNAME,username);
        edit.putString(ConstManager.PASSWORD,password);
        edit.putString(ConstManager.STATIC_ADDRESS,staticAddress);
        edit.putString(ConstManager.CONTRACT_CODE,contractCode);
        edit.putString(ConstManager.OWNER_NAME,ownerName);
        edit.putString(ConstManager.OWNER_ADD,ownerAddress);
        edit.putString(ConstManager.OWNER_TEL,ownerTel);
        edit.putString(ConstManager.OWNER_CMND,ownerCmnd);
        edit.putString(ConstManager.CONTRACT_ID,contractId);
        edit.putString(ConstManager.ACTIVE_DAY,activeDay);
        edit.putString(ConstManager.BOT_NAME,virtualAssistantName);
        edit.putString(ConstManager.BOT_TYPE,virtualAssistantType);
        edit.commit();
    }
}
