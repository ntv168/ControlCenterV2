package center.control.system.vash.controlcenter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.configuration.ConfigurationActivity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.SetConfigActivity;
import center.control.system.vash.controlcenter.configuration.StateConfigurationSQL;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.panel.ModePanel;
import center.control.system.vash.controlcenter.panel.VAPanel;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.ConfigControlCenterDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.StateDTO;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPanel extends AppCompatActivity {
    private static final String TAG = "Setting Panel";
    private ProgressDialog waitDiag;
    private  CloudApi configApi;
    @Override
    protected void onResume() {
        super.onResume();

        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
            StorageHelper.clearPersonIds(Id,SettingPanel.this);
        }
        waitDiag.show();
        new GetPersonIdsTask().execute(Id);
        initStateMachine();
    }
    private void initStateMachine() {
        configApi.getConfig(ConstManager.HOUSE_ID).enqueue(new Callback<ConfigControlCenterDTO>() {
            @Override
            public void onResponse(Call<ConfigControlCenterDTO> call, Response<ConfigControlCenterDTO> response) {
                if (response.body()!=null){
                    for (StateDTO state: response.body().getStates()){
                        StateEntity statEnt = new StateEntity();
                        statEnt.setDelaySec(state.getDelay());
                        statEnt.setNextEvIds(state.getNextEvent());
                        statEnt.setDuringSec(state.getDuring());
                        statEnt.setId(state.getId());
                        statEnt.setName(state.getName());
                        statEnt.setNoticePattern(state.getNotification());
                        StateConfigurationSQL.insertState(statEnt);
                        Log.d(TAG,statEnt.getNextEvIds()+"  "+statEnt.getName());
                        SmartHouse.getInstance().setStates(StateConfigurationSQL.getAll());
//                        ScriptSQLite.clearStateCmd(state.getId());
                     }
                    SmartHouse.getInstance().resetStateToDefault();
                    Toast.makeText(SettingPanel.this, "Tai cau hinh thanh cong", Toast.LENGTH_SHORT).show();
                    if (waitDiag.isShowing()) waitDiag.dismiss();
                }else {
                    Toast.makeText(SettingPanel.this, "Không tai được "+ VolleySingleton.SERVER_HOST, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConfigControlCenterDTO> call, Throwable t) {
                Toast.makeText(SettingPanel.this, "Không kết nối được "+ VolleySingleton.SERVER_HOST, Toast.LENGTH_SHORT).show();
            }
        });
//        StateConfigurationSQL.removeAll();
//        EventEntity ev1 = new EventEntity();
//        ev1.setId(1);
//        ev1.setNextStateId(5);
//        ev1.setPriority(4);
//        ev1.setSenName(AreaEntity.attrivutesValues[2]);
//        ev1.setSenValue(AreaEntity.TEMP_BURN);
//        StateConfigurationSQL.insertEvent(ev1);
//
//        EventEntity ev2 = new EventEntity();
//        ev2.setId(2);
//        ev2.setNextStateId(6);
//        ev2.setPriority(2);
//        ev2.setSenName(AreaEntity.attrivutesValues[2]);
//        ev2.setSenValue(AreaEntity.TEMP_WARM);
//        StateConfigurationSQL.insertEvent(ev2);
//
//        EventEntity ev3 = new EventEntity();
//        ev3.setId(3);
//        ev3.setNextStateId(2);
//        ev3.setPriority(3);
//        ev3.setSenName(AreaEntity.attrivutesValues[0]);
//        ev3.setSenValue(AreaEntity.DOOR_OPEN);
//        StateConfigurationSQL.insertEvent(ev3);
//
//        EventEntity ev4 = new EventEntity();
//        ev4.setId(4);
//        ev4.setNextStateId(3);
//        ev4.setPriority(1);
//        ev4.setSenName(AreaEntity.attrivutesValues[3]);
//        ev4.setSenValue(AreaEntity.DETECT_STRANGE);
//        StateConfigurationSQL.insertEvent(ev4);
//
//        EventEntity ev5 = new EventEntity();
//        ev5.setId(5);
//        ev5.setNextStateId(4);
//        ev5.setPriority(0);
//        ev5.setSenName(AreaEntity.attrivutesValues[3]);
//        ev5.setSenValue(AreaEntity.DETECT_AQUAINTANCE);
//        StateConfigurationSQL.insertEvent(ev5);
//
//        EventEntity ev6 = new EventEntity();
//        ev6.setId(6);
//        ev6.setNextStateId(1);
//        ev6.setPriority(1);
//        ev6.setSenName("");
//        ev6.setSenValue("");
//        StateConfigurationSQL.insertEvent(ev6);
//
//        StateEntity stat = new StateEntity();
//        stat.setName("Home Safe - Như chưa có gì xảy ra");
//        stat.setId(ConstManager.DEFAULT_STATE_ID);
//        stat.setNoticePattern("");
//        stat.setDelaySec(0);
//        stat.setDuringSec(ConstManager.DURING_MAX);
//        stat.setNextEvIds("1,2,3");
//        StateConfigurationSQL.insertState(stat);
//        ScriptSQLite.clearStateCmd(1);
//        SmartHouse.getInstance().setCurrentState(stat);
//
//        stat = new StateEntity();
//        stat.setName("Incomin - Cửa mở chờ xác nhận");
//        stat.setId(2);
//        stat.setNoticePattern("Ai đến vui lòng nhìn vào camera");
//        stat.setDelaySec(0);
//        stat.setDuringSec(20); //choose event highest priority
//        stat.setNextEvIds("4,5");
//        ScriptSQLite.clearStateCmd(2);
//        StateConfigurationSQL.insertState(stat);
//
//        stat = new StateEntity();
//        stat.setName("Intrusion - Đột nhập");
//        stat.setId(3);
//        stat.setNoticePattern("Có người lạ vào nhà");
//        stat.setDelaySec(7);
//        stat.setDuringSec(5);
//        stat.setNextEvIds("5,6");
//        ScriptSQLite.clearStateCmd(3);
//        StateConfigurationSQL.insertState(stat);
//
//        stat = new StateEntity();
//        stat.setName("Owner arrived - Người thân đến");
//        stat.setId(4);
//        stat.setNoticePattern("Xin chào "+ BotUtils.RESULT_VALUE);
//        stat.setDelaySec(0);
//        stat.setDuringSec(5);
//        stat.setNextEvIds("6");
//        ScriptSQLite.clearStateCmd(4);
//        StateConfigurationSQL.insertState(stat);
//
//        stat = new StateEntity();
//        stat.setName("Burning - Cháy nhà");
//        stat.setId(5);
//        stat.setNoticePattern("Có lửa trong nhà thưa "+ BotUtils.OWNER_ROLE);
//        stat.setDelaySec(0);
//        stat.setDuringSec(10);
//        stat.setNextEvIds("6");
//        ScriptSQLite.clearStateCmd(5);
//        StateConfigurationSQL.insertState(stat);
//
//        stat = new StateEntity();
//        stat.setName("Room warm - Phòng nóng quá");
//        stat.setId(6);
//        stat.setNoticePattern("Phòng đang nóng "+BotUtils.OWNER_ROLE+" muốn bật máy lạnh không");
//        stat.setDelaySec(10);
//        stat.setDuringSec(10);
//        stat.setNextEvIds("6");
//        ScriptSQLite.clearStateCmd(6);
//        StateConfigurationSQL.insertState(stat);
//        SmartHouse.getInstance().setStates(StateConfigurationSQL.getAll());
//
//        Log.d(TAG,"inint stateSQL");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_panel);

        final Dialog dialog = new Dialog(SettingPanel.this);
        dialog.setContentView(R.layout.activate_diaglog);

       configApi = RetroFitSingleton.getInstance().getCloudApi();

        waitDiag = new ProgressDialog(this);
        waitDiag.setTitle("Dang tai cau hinh");
        waitDiag.setIndeterminate(true);
//        waitDialog.setCancelable(false);

        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button btnUpdatePerson = (Button) dialog.findViewById(R.id.btnUpdatePersons);
                btnUpdatePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
                        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
                            StorageHelper.clearPersonIds(Id,SettingPanel.this);
                        }
                        new GetPersonIdsTask().execute(Id);
                    }
                });

                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        ImageButton btnDevice = (ImageButton) findViewById(R.id.btnSetDevice);
        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, ManageDeviceActivity.class));
            }
        });

        ImageButton btnSetLogout = (ImageButton) findViewById(R.id.btnSetLogout);
        btnSetLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, MainActivity.class));
            }
        });

        ImageButton btnSetConfig = (ImageButton) findViewById(R.id.btnSetConfig);
        btnSetConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPanel.this, SetConfigActivity.class));
            }
        });
    }

    class GetPersonIdsTask extends AsyncTask<String, String, Person[]> {

        String groupid = "";

        @Override
        protected Person[] doInBackground(String... params) {


            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = App.getFaceServiceClient();
            try{

                groupid = params[0];
                Log.d(TAG,groupid);
                return faceServiceClient.listPersons(params[0]);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Person[] result) {
            Log.d(TAG,result.length+" S");
            if (result != null) {
                for (Person person : result) {
                    try {
                        String name = URLDecoder.decode(person.name, "UTF-8");
                        Log.d(TAG,person.personId.toString()+"  "+name+"  "+ groupid);
                        StorageHelper.setPersonName(person.personId.toString(), name, groupid, SettingPanel.this);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (waitDiag.isShowing()) waitDiag.dismiss();
        }
    }

}
