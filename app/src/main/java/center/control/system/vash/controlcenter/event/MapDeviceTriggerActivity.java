package center.control.system.vash.controlcenter.event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.configuration.EventAdapter;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.StateConfigurationSQL;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ListDevicesTriggerAdapter;
import center.control.system.vash.controlcenter.device.TriggerDeviceEntity;
import center.control.system.vash.controlcenter.device.TriggerDeviceSQLite;
import center.control.system.vash.controlcenter.script.CommandAdapter;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.sensor.ListSensorAdapter;
import center.control.system.vash.controlcenter.sensor.SensorEntity;
import center.control.system.vash.controlcenter.sensor.SensorSQLite;
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

public class MapDeviceTriggerActivity extends AppCompatActivity implements
        RecyclerViewStateAdapter.OnAdapterItemClickListener, EventAdapter.EventListener {

    private static final String TAG = "MapDeviceTriggerActivity: ";
    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private int triggerId = 1;
    private CloudApi configApi;
    private List<StateEntity> liststats;
    private ProgressDialog waitDialog;
    List<DeviceEntity> listDevices;
    List<SensorEntity> listSensors;
    DeviceSQLite deviceSQLite = new DeviceSQLite();
    SmartHouse house;
    EventAdapter eventAdapter;
    TriggerSQLite triggerSQLite = new TriggerSQLite();
    SensorSQLite sensorSQLite = new SensorSQLite();
    TriggerDeviceSQLite triggerdevicesSQLite = new TriggerDeviceSQLite();
    RecyclerViewStateAdapter triggerAdapter;

    ListSensorAdapter sensorAdapter;
    Boolean isDevice = true;
    private CommandAdapter cmdAdapter;
    private StateEntity currentState;

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

//                    selectStateDiag.setAdapter(getStateAdapter(), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            btnSltState.setText(stats.get(which).getName());
//                            cmdAdapter.setScriptEntities(stats.get(which).getCommands());
//                            Log.d(TAG,stats.get(which).getEvents().size()+" s");
//                            eventAdapter.setScriptEntities(stats.get(which).getEvents());
//                            currentState = stats.get(which);
//                        }
                    triggerAdapter.setDevices(liststats);
                    triggerAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.edit_configuration);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);

        liststats = new ArrayList<>();
        configApi = RetroFitSingleton.getInstance().getCloudApi();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Tải dữ liệu");
        waitDialog.setIndeterminate(true);

        RecyclerView lstCmd = (RecyclerView) findViewById(R.id.listItemCenter);
        lstCmd .setHasFixedSize(true);
        LinearLayoutManager verticalLayout23 = new LinearLayoutManager(this);
        verticalLayout23.setOrientation(LinearLayoutManager.VERTICAL);
        lstCmd .setLayoutManager(verticalLayout23);

        cmdAdapter = new CommandAdapter(new ArrayList<CommandEntity>());
        lstCmd .setAdapter(cmdAdapter);


        RecyclerView lstEvent = (RecyclerView) findViewById(R.id.listItemRight);
        lstEvent.setHasFixedSize(true);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        lstEvent.setLayoutManager(verticalLayout2);

        eventAdapter = new EventAdapter(new ArrayList<EventEntity>(),this);
        lstEvent.setAdapter(eventAdapter);

        ImageButton lnBack = (ImageButton) findViewById(R.id.lnBack);
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // RecyclerView for List State
//        final List<TriggerEntity> listTrigger = triggerSQLite.getAll();


        RecyclerView rwTrigger = (RecyclerView) findViewById(R.id.listItemLeft);
        rwTrigger.setHasFixedSize(true);

        rwTrigger.setLayoutManager(verticalLayout);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);

        triggerAdapter = new RecyclerViewStateAdapter(liststats, this);
        rwTrigger.setAdapter(triggerAdapter);

        // RecyclerView for List Device Trigger

        house = SmartHouse.getInstance();

        //----------------Dialog for add new device
        selectAreaDiag = new AlertDialog.Builder(MapDeviceTriggerActivity.this);
        selectAreaDiag.setIcon(R.drawable.add);
        selectAreaDiag.setTitle("Chọn không gian:");

        selectDeviceDiag = new AlertDialog.Builder(MapDeviceTriggerActivity.this);
        selectDeviceDiag.setIcon(R.drawable.add);
        selectDeviceDiag.setTitle("Chọn thiết bị:");
        selectDeviceDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        selectAreaDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //--------------Adapter for select area
        selectAreaDiag.setAdapter(house.getAreaNameAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int areaId = house.getAreas().get(which).getId();

//                if (isDevice) {
//                    final List<DeviceEntity> listDevice = house.getDeviceswithoutTrigger(areaId,triggerId);
//                    ArrayAdapter<String> devNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);
//
//                    for (DeviceEntity device : listDevice){
//                        devNames.add(device.getName());
//                        Log.d("---------", "Trigger Id: " + device.getTriggerId());
//                    }
//                    selectDeviceDiag.setAdapter(devNames,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    listDevice.get(which).setTriggerId(triggerId);
//                                    TriggerDeviceEntity entity = new TriggerDeviceEntity();
//                                    entity.setName(listDevice.get(which).getName());
//                                    entity.setDeviceId(listDevice.get(which).getId());
//                                    entity.setTriggerId(triggerId);
//                                    entity.setType(1);
//                                    entity.setValue("mở");
//                                    triggerdevicesSQLite.insert(entity);
//
//                                    List<TriggerDeviceEntity> list = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,1);
//                                    loadListDevicebyTriggerId(triggerId,list);
//
//                                    Log.d("----------", "onClick: " + listDevice.get(which).getName() + "---" + listDevice.get(which).getTriggerId());
//
//                                    dialog.dismiss();
//                                }
//                            });
//                    selectDeviceDiag.show();
                    final List<CommandEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                    ArrayAdapter<String> devNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                    for (CommandEntity sde : newScript){
                        if (sde.getDeviceName() != null)
                            devNames.add(sde.getDeviceName());
                    }
                    selectDeviceDiag = new AlertDialog.Builder(MapDeviceTriggerActivity.this);
                    selectDeviceDiag.setIcon(R.drawable.more);
                    selectDeviceDiag.setTitle("Chọn thiết bị:");
                    selectDeviceDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    selectDeviceDiag.setAdapter(devNames,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cmdAdapter.addScripDev(newScript.get(which));
                                    dialog.dismiss();
                                }
                            });
                    selectDeviceDiag.show();

//                } else {
//                    final List<SensorEntity> listSensor = house.getSensorWithoutTriggerByAreaId(areaId,triggerId);
//                    ArrayAdapter<String> senNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);
//
//                    for (SensorEntity sensor : listSensor){
//                        senNames.add(sensor.getName());
//                    }
//                    selectDeviceDiag.setAdapter(senNames,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    listSensor.get(which).setTriggerId(triggerId);
//                                    TriggerDeviceEntity entity = new TriggerDeviceEntity();
//                                    entity.setName(listSensor.get(which).getName());
//                                    entity.setDeviceId(listSensor.get(which).getId());
//                                    entity.setTriggerId(triggerId);
//                                    entity.setType(2);
//                                    entity.setValue("nóng hơn 20 độ");
//                                    triggerdevicesSQLite.insert(entity);
//                                Log.d("----------", "onClick: " + listSensor.get(which).getName() + "---" + listSensor.get(which).getTriggerId());
////
//
//                                    List<TriggerDeviceEntity> list = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,2);
//                                    loadListSensorbyTriggerId(triggerId,list);
//
//                                    dialog.dismiss();
//                                }
//                            });
//                    selectDeviceDiag.show();
//                }

            }
        });

        //--------------Adapter for select area

        Log.d("-------------------", "onClick: " + "-----------" );
    }




    public void addDevicetoTrigger(View view) {

        selectAreaDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final SmartHouse house = SmartHouse.getInstance();
        selectAreaDiag.setAdapter(house.getAreaNameAdapter(MapDeviceTriggerActivity.this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int areaId = house.getAreas().get(which).getId();
                final List<CommandEntity> newScript = house.getDeviceScriptByAreaId(areaId);
                ArrayAdapter<String> devNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                for (CommandEntity sde : newScript){
                    if (sde.getDeviceName() != null)
                        devNames.add(sde.getDeviceName());
                }
                selectDeviceDiag = new AlertDialog.Builder(MapDeviceTriggerActivity.this);
                selectDeviceDiag.setIcon(R.drawable.more);
                selectDeviceDiag.setTitle("Chọn thiết bị:");
                selectDeviceDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                selectDeviceDiag.setAdapter(devNames,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cmdAdapter.addScripDev(newScript.get(which));
                                dialog.dismiss();
                            }
                        });
                selectDeviceDiag.show();
            }
        });
        selectAreaDiag.show();

    }

    public void addSensortoTrigger(View view) {
        isDevice = false;
        selectAreaDiag.show();
    }

    public void back(View view) {
        finish();
    }

    public void loadListDevicebyTriggerId(int triggerId, List<TriggerDeviceEntity> list) {
        final ListView lstDevices = (ListView) findViewById(R.id.listItemCenter);

        ListDevicesTriggerAdapter deviceAdapter = new ListDevicesTriggerAdapter(MapDeviceTriggerActivity.this, list);
        lstDevices.setAdapter(deviceAdapter);
    }

    public void loadListSensorbyTriggerId(int triggerId, List<TriggerDeviceEntity> list) {

        final ListView lstDevices = (ListView) findViewById(R.id.listItemRight);

        ListDevicesTriggerAdapter deviceAdapter = new ListDevicesTriggerAdapter(MapDeviceTriggerActivity.this, list);
        lstDevices.setAdapter(deviceAdapter);
    }


    @Override
    public void onStateClick(StateEntity stats) {
        if (currentState != null){
            List<CommandEntity> listCommmand = cmdAdapter.getScriptDeviceEntities();
            Log.d(TAG,listCommmand.size()+" ");
            ScriptSQLite.insertStateCommand(currentState.getId(),listCommmand);
            for (EventEntity event : eventAdapter.getEventEntities()){
                StateConfigurationSQL.updateEventById(event.getId(),event);
            }
            SmartHouse.getInstance().updateStateById(currentState.getId(),listCommmand, eventAdapter.getEventEntities());
        }
        cmdAdapter.setScriptEntities(stats.getCommands());
        Log.d(TAG,stats.getEvents().size()+" s");
        eventAdapter.setScriptEntities(stats.getEvents());
        currentState = stats;
    }

    @Override
    public void onClick(final int id) {
        final SmartHouse house = SmartHouse.getInstance();
        selectAreaDiag.setAdapter(house.getAreaNameAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventAdapter.updateAreaId(id,house.getAreas().get(which).getId());
            }
        });
        selectAreaDiag.show();
    }
}
