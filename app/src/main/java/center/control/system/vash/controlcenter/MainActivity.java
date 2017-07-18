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

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.StateConfigurationSQL;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.HouseKeyDTO;
import center.control.system.vash.controlcenter.server.LoginSmarthouseDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.StaffCodeDTO;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private static final String TAG = "---Main Activity---";
    SharedPreferences sharedPreferences;
    private String username;
    private String password;
    private String houseId;
    private String contractId;
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
        modHost.show();
    }

    private void loginStaffCode() {
        final StaffCodeDTO stff = new StaffCodeDTO();
        stff.setUsername(txtusername.getText().toString().replace("admin",""));
        stff.setStaffCode(txtpassword.getText().toString());
        final CloudApi loginStaffApi = RetroFitSingleton.getInstance().getCloudApi();
        loginStaffApi.staffLogin(stff).enqueue(new Callback<StaffCodeDTO>() {
            @Override
            public void onResponse(Call<StaffCodeDTO> call, Response<StaffCodeDTO> response) {
                Log.d(TAG,call.request().url()+"");
                if (response.body()!=null && response.body().getMessage()!= null && response.body().getMessage().equals("success")){
                    startActivity(new Intent(MainActivity.this,SettingPanel.class));
                    initStateMachine();
                }else {
                    Toast.makeText(MainActivity.this,"Sai code nhân viên ",Toast.LENGTH_LONG).show();
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
                Toast.makeText(MainActivity.this,"Sai code nhân viên ",Toast.LENGTH_LONG).show();
                if (txtusername.getText().toString().equals("admin") &&
                        txtpassword.getText().toString().equals("admin")) {
                    startActivity(new Intent(MainActivity.this,SettingPanel.class));
                }
                loginDia.dismiss();
            }
        });
    }

    private void initStateMachine() {
        StateConfigurationSQL.removeAll();
        EventEntity ev1 = new EventEntity();
        ev1.setId(1);
        ev1.setNextStateId(3);
        ev1.setPriority(ConstManager.PRIORITY_MAX);
        ev1.setSenName(AreaEntity.attrivutesValues[3]);
        ev1.setSenValue(AreaEntity.DETECT_STRANGE);
        StateConfigurationSQL.insertEvent(ev1);

        EventEntity ev2 = new EventEntity();
        ev2.setId(2);
        ev2.setNextStateId(2);
        ev2.setPriority(3);
        ev2.setSenName(AreaEntity.attrivutesValues[0]);
        ev2.setSenValue(AreaEntity.DOOR_OPEN);
        StateConfigurationSQL.insertEvent(ev2);

        EventEntity ev3 = new EventEntity();
        ev3.setId(3);
        ev3.setNextStateId(4);
        ev3.setPriority(0);
        ev3.setSenName(AreaEntity.attrivutesValues[3]);
        ev3.setSenValue(AreaEntity.DETECT_AQUAINTANCE);
        StateConfigurationSQL.insertEvent(ev3);

        EventEntity ev4 = new EventEntity();
        ev4.setId(4);
        ev4.setNextStateId(5);
        ev3.setPriority(0);
        ev4.setSenName(AreaEntity.attrivutesValues[0]);
        ev4.setSenValue(AreaEntity.DOOR_OPEN);
        StateConfigurationSQL.insertEvent(ev4);

        EventEntity ev5 = new EventEntity();
        ev5.setId(5);
        ev5.setNextStateId(1);
        ev3.setPriority(0);
        ev5.setSenName(AreaEntity.attrivutesValues[0]);
        ev5.setSenValue(AreaEntity.DOOR_CLOSE);
        StateConfigurationSQL.insertEvent(ev5);

        EventEntity ev6 = new EventEntity();
        ev6.setId(6);
        ev6.setNextStateId(6);
        ev6.setPriority(2);
        ev6.setSenName(AreaEntity.attrivutesValues[2]);
        ev6.setSenValue(AreaEntity.TEMP_WARM);
        StateConfigurationSQL.insertEvent(ev6);

        EventEntity ev7 = new EventEntity();
        ev7.setId(7);
        ev7.setNextStateId(1);
        ev7.setPriority(2);
        ev7.setSenName(AreaEntity.attrivutesValues[2]);
        ev7.setSenValue(AreaEntity.TEMP_COLD);
        StateConfigurationSQL.insertEvent(ev7);

        StateEntity stat = new StateEntity();
        stat.setName("Home Safe");
        stat.setId(ConstManager.DEFAULT_STATE_ID);
        stat.setNoticePattern("An toàn");
        stat.setDelaySec(0);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("2,6");
        StateConfigurationSQL.insertState(stat);
        ScriptSQLite.clearStateCmd(1);
        SmartHouse.getInstance().setCurrentState(stat);

        stat = new StateEntity();
        stat.setName("Incomin");
        stat.setId(2);
        stat.setNoticePattern("Mở cửa vui lòng nhìn vào camera");
        stat.setDelaySec(0);
        stat.setDuringSec(10); //choose event highest priority
        stat.setNextEvIds("1,3");
        ScriptSQLite.clearStateCmd(2);
        StateConfigurationSQL.insertState(stat);

        stat = new StateEntity();
        stat.setName("Intrusion");
        stat.setId(3);
        stat.setNoticePattern("Có người lạ vào nhà");
        stat.setDelaySec(5);
        stat.setDuringSec(8);
        stat.setNextEvIds("3");
        ScriptSQLite.clearStateCmd(3);
        StateConfigurationSQL.insertState(stat);

        stat = new StateEntity();
        stat.setName("Aquain Come");
        stat.setId(4);
        stat.setNoticePattern("Xin chào <result-value>");
        stat.setDelaySec(0);
        stat.setDuringSec(10);
        stat.setNextEvIds("4");
        ScriptSQLite.clearStateCmd(4);
        StateConfigurationSQL.insertState(stat);

        stat = new StateEntity();
        stat.setName("Forget close door");
        stat.setId(5);
        stat.setNoticePattern("<owner-role> quên đóng cửa kìa");
        stat.setDelaySec(5);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("5");
        ScriptSQLite.clearStateCmd(5);
        StateConfigurationSQL.insertState(stat);

        stat = new StateEntity();
        stat.setName("Room warm");
        stat.setId(6);
        stat.setNoticePattern("Phòng đang nóng <owner-role> muốn bật máy lạnh không");
        stat.setDelaySec(10);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("7");
        ScriptSQLite.clearStateCmd(6);
        StateConfigurationSQL.insertState(stat);
//        stat = new StateEntity();
//        stat.setName("Forget close door");
//        stat.setAvailable(true);
//        stat.setId(6);
//        stat.setConfigurationId(-1);
//        stat.setSocialIntentId(2060);
//        stat.setDelaySec(15);
//        StateConfigurationSQL.insertState(stat);
        SmartHouse.getInstance().setStates(StateConfigurationSQL.getAll());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if (response.body()!= null && response.body().getContractId()!= null) {
                    Log.d(TAG,call.request().url()+" --- "+response.body().getContractId());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(ConstManager.USERNAME,key.getUsername());
                    edit.putString(ConstManager.STATIC_ADDRESS,response.body().getStaticAddress());
                    edit.putString(ConstManager.CONTRACT_CODE,response.body().getContractCode());
                    edit.putString(ConstManager.OWNER_NAME,response.body().getOwnerName());
                    edit.putString(ConstManager.OWNER_ADD,response.body().getOwnerAddress());
                    edit.putString(ConstManager.OWNER_TEL,response.body().getOwnerTel());
                    edit.putString(ConstManager.OWNER_CMND,response.body().getOwnerCmnd());
                    edit.putString(ConstManager.CONTRACT_ID,response.body().getContractId());
                    edit.putString(ConstManager.ACTIVE_DAY,response.body().getActiveDay());
                    edit.putString(ConstManager.BOT_NAME,response.body().getVirtualAssistantName());
                    edit.putString(ConstManager.BOT_TYPE,response.body().getVirtualAssistantType());
                    edit.putInt(ConstManager.BOT_TYPE_ID,response.body().getVirtualAssistantTypeId());
                    SmartHouse.getInstance().setContractId(response.body().getContractId());
                    edit.commit();
                    Log.d(TAG,response.body().getVirtualAssistantType());
                    startActivity( new Intent(MainActivity.this, ControlPanel.class));
                    finish();
                } else {
                    Log.d(TAG,call.request().url()+" sai ten");
                    Toast.makeText(MainActivity.this,"Sai tên đăng nhập mật khẩu",Toast.LENGTH_LONG).show();
                }
                loginDia.dismiss();
            }
            @Override
            public void onFailure(Call<LoginSmarthouseDTO> call, Throwable t) {
                Log.d(TAG,call.request().url()+"  ---err "+t.getMessage());
                Toast.makeText(MainActivity.this,"Không kết nối được server",Toast.LENGTH_LONG).show();
//                username = key.getUsername();
//                password = key.getPassword();
//                houseId = "123123";
//                staticAddress = "123123";
//                contractCode = "123123";
//                ownerName = "Mỹ";
//                ownerAddress = "123123";
//                ownerTel = "123123";
//                ownerCmnd = "123123";
//                contractId = "123123";
//                activeDay = "123123";
//                virtualAssistantName = "Sen";
//                virtualAssistantType = "Quản gia già";
//                Log.d(TAG,virtualAssistantType);
//                virtualAssistantId = response.body().getVirtualAssistantTypeId();
                loginDia.dismiss();
            }
        });

    }
}
