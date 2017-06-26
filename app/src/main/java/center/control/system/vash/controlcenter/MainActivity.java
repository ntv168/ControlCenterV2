package center.control.system.vash.controlcenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.nlp.VoiceUtils;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.HouseKeyDTO;
import center.control.system.vash.controlcenter.server.LoginSmarthouseDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.SmartHouse;
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

        // Set transparent status bar Android
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

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

        final AlertDialog.Builder modHost = new AlertDialog.Builder(this);
        modHost.setTitle("Nhập địa chỉ server");
        View diaView = this.getLayoutInflater().inflate(R.layout.dialog_host_edit, null);
        final EditText hostAddress = (EditText) diaView.findViewById(R.id.txtServerHost);
        hostAddress.setText(VolleySingleton.SERVER_HOST);

        modHost.setView(diaView)
                // Add action buttons
                .setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        VolleySingleton.SERVER_HOST = hostAddress.getText().toString();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        VoiceUtils.speak("Xin chào anh Đại, quán cà phê thông minh xin được phục vụ ạ");
                        dialog.dismiss();

                    }
                });
        modHost.show();
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
                if (response.body()!= null && response.body().getHouseId()!= null) {
                    Log.d(TAG,call.request().url()+" --- "+response.body().getHouseId());
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
                    Log.d(TAG,call.request().url()+"sai ten");
                    username = key.getUsername();
                    password = key.getPassword();
                    houseId = "21321321321";
                    staticAddress = "21321321321";
                    contractCode = "sads";
                    ownerName = "fake account";
                    ownerAddress = "21321321321";
                    ownerTel = "21321321321";
                    ownerCmnd = "21321321321";
                    contractId = "21321321321";
                    activeDay = "21321321321";
                    virtualAssistantName = "Sen";
                    virtualAssistantType = "21321321321";
                    Intent i = new Intent(MainActivity.this, ControlPanel.class);
                    startActivity(i);
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
