package center.control.system.vash.controlcenter.configuration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.event.MapDeviceTriggerActivity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.ConfigControlCenterDTO;
import center.control.system.vash.controlcenter.server.EventDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.StateDTO;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurationActivity extends AppCompatActivity {

    private static final String TAG = "Configuration Activity";
    private CloudApi configApi;
    private List<StateEntity> liststats;
    private ProgressDialog waitDialog;


    @Override
    protected void onResume() {
        super.onResume();
        configApi.getConfig(ConstManager.HOUSE_ID).enqueue(new Callback<ConfigControlCenterDTO>() {
            @Override
            public void onResponse(Call<ConfigControlCenterDTO> call, Response<ConfigControlCenterDTO> response) {
                Log.d(TAG,call.request().url().toString());
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
                        statEnt.setDefautState(state.getTimeoutState());
                        StateConfigurationSQL.insertState(statEnt);
                        ScriptSQLite.clearStateCmd(state.getId());
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
                    SmartHouse.getInstance().setCurrentState(SmartHouse.getInstance().getStateById(ConstManager.NO_BODY_HOME_STATE));

                    liststats = SmartHouse.getInstance().getStates();
                    ListConfigurationAdapter adapter = new ListConfigurationAdapter(ConfigurationActivity.this,liststats);
                    ListView lwConfiguration = (ListView) findViewById(R.id.lsConfig);
                    lwConfiguration.setAdapter(adapter);
//                    selectStateDiag.setAdapter(getStateAdapter(), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            btnSltState.setText(stats.get(which).getName());
//                            cmdAdapter.setScriptEntities(stats.get(which).getCommands());
//                            Log.d(TAG,stats.get(which).getEvents().size()+" s");
//                            eventAdapter.setScriptEntities(stats.get(which).getEvents());
//                            currentState = stats.get(which);
//                        }
                    waitDialog.dismiss();
//                    });
                }else {
                    waitDialog.dismiss();
//                    MessageUtils.makeText(SetConfigActivity.this, "Không tải được dữ liệu"+ VolleySingleton.SERVER_HOST).show();
                }
            }

            @Override
            public void onFailure(Call<ConfigControlCenterDTO> call, Throwable t) {
//                MessageUtils.makeText(SetConfigActivity.this, "Không kết nối được "+ VolleySingleton.SERVER_HOST).show();
            }
        });
        waitDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        liststats = new ArrayList<>();
        configApi = RetroFitSingleton.getInstance().getCloudApi();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Tải dữ liệu");
        waitDialog.setIndeterminate(true);

        ListConfigurationAdapter adapter = new ListConfigurationAdapter(ConfigurationActivity.this,liststats);
        ListView lwConfiguration = (ListView) findViewById(R.id.lsConfig);
        lwConfiguration.setAdapter(adapter);

    }


    public void clicktoMapTrigger(View view) {
        startActivity(new Intent(ConfigurationActivity.this,MapDeviceTriggerActivity.class));
    }

    public void back(View view) {
        finish();
    }
}
