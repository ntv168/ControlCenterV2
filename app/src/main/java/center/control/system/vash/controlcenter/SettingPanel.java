package center.control.system.vash.controlcenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import center.control.system.vash.controlcenter.server.EventDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.StateDTO;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.MessageUtils;
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

    }
    private void initStateMachine() {
        configApi.getConfig(ConstManager.HOUSE_ID).enqueue(new Callback<ConfigControlCenterDTO>() {
            @Override
            public void onResponse(Call<ConfigControlCenterDTO> call, Response<ConfigControlCenterDTO> response) {
                if (response.body()!=null){
                    StateConfigurationSQL.removeAll();
                    for (StateDTO state: response.body().getStates()){
                        StateEntity statEnt = new StateEntity();
                        statEnt.setDelaySec(state.getDelay());
                        statEnt.setNextEvIds(state.getNextEvent());
                        statEnt.setDuringSec(state.getDuring());
                        statEnt.setId(state.getId());
                        statEnt.setName(state.getName());
                        statEnt.setNoticePattern(state.getNotification());
                        StateConfigurationSQL.insertState(statEnt);
//                        ScriptSQLite.clearStateCmd(state.getId());
                     }
                    for (EventDTO ev: response.body().getEvents()){
                        EventEntity eventEnt = new EventEntity();
                        eventEnt.setId(ev.getId());
                        eventEnt.setSenValue(ev.getSensorValue());
                        eventEnt.setSenName(ev.getSensorName());
                        eventEnt.setNextStateId(ev.getNextState());
                        eventEnt.setPriority(ev.getPriority());
                        StateConfigurationSQL.insertEvent(eventEnt);
                    }
                    SmartHouse.getInstance().setStates(StateConfigurationSQL.getAll());
                    SmartHouse.getInstance().resetStateToDefault();
                    if (waitDiag.isShowing()) waitDiag.dismiss();
                }else {
                    if (waitDiag.isShowing()) waitDiag.dismiss();
                    MessageUtils.makeText(SettingPanel.this, "Không tải được dữ liệu"+ VolleySingleton.SERVER_HOST).show();
                }
            }

            @Override
            public void onFailure(Call<ConfigControlCenterDTO> call, Throwable t) {
                MessageUtils.makeText(SettingPanel.this, "Không kết nối được "+ VolleySingleton.SERVER_HOST).show();
            }
        }); 
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_panel);

        final AlertDialog dialog;

        AlertDialog.Builder builer = new AlertDialog.Builder(this);

        builer.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builer.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builer.create();
        dialog.setTitle("Đặt lại thiết bị");
        dialog.setMessage("Đặt lại thiết bị sẽ xóa mọi kết nối và cấu hình trong trung tâm điều khiển?");
        dialog.setCancelable(false);


    configApi = RetroFitSingleton.getInstance().getCloudApi();

        waitDiag = new ProgressDialog(this);
        waitDiag.setTitle("Tải dữ liệu");
        waitDiag.setIndeterminate(true);
//        waitDialog.setCancelable(false);

        ImageButton btnActive = (ImageButton) findViewById(R.id.btnSetReset);
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Button btnUpdatePerson = (Button) dialog.findViewById(R.id.btnUpdatePersons);
//                btnUpdatePerson.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
//                        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
//                            StorageHelper.clearPersonIds(Id,SettingPanel.this);
//                        }
//                        new GetPersonIdsTask().execute(Id);
//                    }
//                });
//
//                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
//                btnCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });

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

        String Id = StorageHelper.getPersonGroupId("nguoinha",SettingPanel.this);
        if (!StorageHelper.getAllPersonIds(Id, SettingPanel.this).isEmpty()) {
            StorageHelper.clearPersonIds(Id,SettingPanel.this);
        }
        waitDiag.show();
        new GetPersonIdsTask().execute(Id);
        initStateMachine();
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
            if (result != null) {
                Log.d(TAG, result.length + " S");
                if (result != null) {
                    for (Person person : result) {
                        try {
                            String name = URLDecoder.decode(person.name, "UTF-8");
                            Log.d(TAG, person.personId.toString() + "  " + name + "  " + groupid);
                            StorageHelper.setPersonName(person.personId.toString(), name, groupid, SettingPanel.this);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                MessageUtils.makeText(SettingPanel.this, "Không kết nối được dữ liệu nhận diện hình ảnh").show();
            }
            if (waitDiag.isShowing()) waitDiag.dismiss();
        }
    }

}
