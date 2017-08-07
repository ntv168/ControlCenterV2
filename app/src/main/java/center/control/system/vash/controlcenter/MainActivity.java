package center.control.system.vash.controlcenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.HouseKeyDTO;
import center.control.system.vash.controlcenter.server.LoginSmarthouseDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.StaffCodeDTO;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.MessageUtils;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private static final String TAG = "---Main Activity---";
    SharedPreferences sharedPreferences;
    private String username;
    private String password;
    private ProgressDialog loginDia;
    private EditText txtusername;
    private EditText txtpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
          txtusername = (EditText) findViewById(R.id.txtUsername);
          txtpassword = (EditText) findViewById(R.id.txtPassword);
        sharedPreferences =getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        txtusername.setText(sharedPreferences.getString(ConstManager.USERNAME,""));

        loginDia = new ProgressDialog(this);
        loginDia.setTitle("Đăng nhập vào hệ thống");
        loginDia.setMessage("Vui lòng đợi");
        loginDia.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtusername.getText().toString().contains("admin")){
                    loginStaffCode();
                    loginDia.show();
                } else {
                    loginDia.show();
                    loginSmartHouse();
                }
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
                        dialog.dismiss();
                    }
                });
//        modHost.show();
    }

    private void loginStaffCode() {
        final StaffCodeDTO stff = new StaffCodeDTO();
        stff.setUsername(ConstManager.HOUSE_ID);
        stff.setStaffCode(txtpassword.getText().toString());
        final CloudApi loginStaffApi = RetroFitSingleton.getInstance().getCloudApi();
        loginStaffApi.staffLogin(stff).enqueue(new Callback<StaffCodeDTO>() {
            @Override
            public void onResponse(Call<StaffCodeDTO> call, Response<StaffCodeDTO> response) {
                Log.d(TAG,call.request().url()+"");
                if (response.body()!=null && response.body().getMessage()!= null
                        && response.body().getMessage().equals("success")){
                    startActivity(new Intent(MainActivity.this,SettingPanel.class));
                }else {
                    MessageUtils.makeText(MainActivity.this,"Sai code nhân viên ").show();
                    if (txtusername.getText().toString().equals("admin") &&
                            txtpassword.getText().toString().equals("admin")) {
                        startActivity(new Intent(MainActivity.this,SettingPanel.class));
                    }
                }
                loginDia.dismiss();
            }
            @Override
            public void onFailure(Call<StaffCodeDTO> call, Throwable t) {
                Log.d(TAG,call.request().url()+" sai staff cose");
                MessageUtils.makeText(MainActivity.this,"Không kết nối được máy chủ").show();
                if (txtusername.getText().toString().equals("admin") &&
                        txtpassword.getText().toString().equals("admin")) {
                    startActivity(new Intent(MainActivity.this,SettingPanel.class));
                }
                loginDia.dismiss();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        username = sharedPreferences.getString(ConstManager.USERNAME,"");
        password = sharedPreferences.getString(ConstManager.PASSWORD,"");
    }

    private void loginSmartHouse() {
//        Log.d(TAG, username + " --" + password);
//        if (username.equals("") || password.equals("")) {
            final HouseKeyDTO key = new HouseKeyDTO();
            key.setUsername(txtusername.getText().toString());
            key.setPassword(txtpassword.getText().toString());
            key.setHouseId(ConstManager.HOUSE_ID);
            final CloudApi loginApi = RetroFitSingleton.getInstance().getCloudApi();
            loginApi.mobileLogin(key).enqueue(new Callback<LoginSmarthouseDTO>() {
                @Override
                public void onResponse(Call<LoginSmarthouseDTO> call, retrofit2.Response<LoginSmarthouseDTO> response) {
                    if (response.body() != null && response.body().getContractId() != null) {
                        Log.d(TAG, call.request().url() + " --- " + response.body().getContractId());
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(ConstManager.USERNAME, key.getUsername());
                        edit.putString(ConstManager.PASSWORD, key.getPassword());
                        edit.putString(ConstManager.STATIC_ADDRESS, response.body().getStaticAddress());
                        edit.putString(ConstManager.CONTRACT_CODE, response.body().getContractCode());
                        edit.putString(ConstManager.OWNER_NAME, response.body().getOwnerName());
                        edit.putString(ConstManager.OWNER_ADD, response.body().getOwnerAddress());
                        edit.putString(ConstManager.OWNER_TEL, response.body().getOwnerTel());
                        edit.putString(ConstManager.OWNER_CMND, response.body().getOwnerCmnd());
                        edit.putString(ConstManager.CONTRACT_ID, response.body().getContractId());
                        edit.putString(ConstManager.ACTIVE_DAY, response.body().getActiveDay());
                        edit.putString(ConstManager.BOT_NAME, response.body().getVirtualAssistantName());
                        edit.putString(ConstManager.BOT_TYPE, response.body().getVirtualAssistantType());
                        edit.putInt(ConstManager.BOT_TYPE_ID, response.body().getVirtualAssistantTypeId());
                        SmartHouse.getInstance().setContractId(response.body().getContractId());
                        edit.commit();
                        startActivity(new Intent(MainActivity.this, ControlPanel.class));
                        finish();
                    } else {
                        Log.d(TAG, call.request().url() + " sai ten "+ username + ConstManager.HOUSE_ID);
                        MessageUtils.makeText(MainActivity.this, "Sai tên đăng nhập mật khẩu").show();
                    }
                    loginDia.dismiss();
                }

                @Override
                public void onFailure(Call<LoginSmarthouseDTO> call, Throwable t) {
                    Log.d(TAG, call.request().url() + "  ---err " + t.getMessage());
                    MessageUtils.makeText(MainActivity.this, "Không kết nối được server").show();
                    loginDia.dismiss();
                }
            });
//        } else {
//            if (txtusername.getText().toString().equals(username) &&
//                    txtpassword.getText().toString().equals(password)){
//                startActivity(new Intent(MainActivity.this, ControlPanel.class));
//                finish();
//            } else {
//                Log.d(TAG, " sai ten mat khau local");
//                MessageUtils.makeText(MainActivity.this, "Sai tên đăng nhập mật khẩu").show();
//            }
//        }
    }
}
