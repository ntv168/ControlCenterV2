package center.control.system.vash.controlcenter.trigger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.TriggerDeviceEntity;
import center.control.system.vash.controlcenter.device.TriggerDeviceSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ListDevicesTriggerAdapter;
import center.control.system.vash.controlcenter.sensor.ListSensorAdapter;
import center.control.system.vash.controlcenter.sensor.SensorEntity;
import center.control.system.vash.controlcenter.sensor.SensorSQLite;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class MapDeviceTriggerActivity extends AppCompatActivity implements
        RecyclerViewTriggerAdapter.OnAdapterItemClickListener {

    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private int triggerId = 1;
    List<DeviceEntity> listDevices;
    List<SensorEntity> listSensors;
    DeviceSQLite deviceSQLite = new DeviceSQLite();
    SmartHouse house;
    TriggerSQLite triggerSQLite = new TriggerSQLite();
    SensorSQLite sensorSQLite = new SensorSQLite();
    TriggerDeviceSQLite triggerdevicesSQLite = new TriggerDeviceSQLite();

    ListSensorAdapter sensorAdapter;
    Boolean isDevice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_configuration);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);

        LinearLayout lnBack = (LinearLayout) findViewById(R.id.lnBack);
        lnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // RecyclerView for List Trigger
        final List<TriggerEntity> listTrigger = triggerSQLite.getAll();

        RecyclerView lstAreaManage = (RecyclerView) findViewById(R.id.listItemLeft);
        lstAreaManage.setHasFixedSize(true);

        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstAreaManage.setLayoutManager(verticalLayout);

        final RecyclerViewTriggerAdapter triggerAdapter = new RecyclerViewTriggerAdapter(listTrigger, this);
        RecyclerView rwTrigger = (RecyclerView) findViewById(R.id.listItemLeft);
        rwTrigger.setAdapter(triggerAdapter);

        // RecyclerView for List Device Trigger



        List<TriggerDeviceEntity> listDevice = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,1);
        loadListDevicebyTriggerId(triggerId,listDevice);
        List<TriggerDeviceEntity> listSensor = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,2);
        loadListSensorbyTriggerId(triggerId,listSensor);

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

                if (isDevice) {
                    final List<DeviceEntity> listDevice = house.getDeviceswithoutTrigger(areaId,triggerId);
                    ArrayAdapter<String> devNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                    for (DeviceEntity device : listDevice){
                        devNames.add(device.getName());
                        Log.d("---------", "Trigger Id: " + device.getTriggerId());
                    }
                    selectDeviceDiag.setAdapter(devNames,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    listDevice.get(which).setTriggerId(triggerId);
                                    TriggerDeviceEntity entity = new TriggerDeviceEntity();
                                    entity.setName(listDevice.get(which).getName());
                                    entity.setDeviceId(listDevice.get(which).getId());
                                    entity.setTriggerId(triggerId);
                                    entity.setType(1);
                                    entity.setValue("mở");
                                    triggerdevicesSQLite.insert(entity);

                                    List<TriggerDeviceEntity> list = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,1);
                                    loadListDevicebyTriggerId(triggerId,list);

                                    Log.d("----------", "onClick: " + listDevice.get(which).getName() + "---" + listDevice.get(which).getTriggerId());

                                    dialog.dismiss();
                                }
                            });
                    selectDeviceDiag.show();

                } else {
                    final List<SensorEntity> listSensor = house.getSensorWithoutTriggerByAreaId(areaId,triggerId);
                    ArrayAdapter<String> senNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                    for (SensorEntity sensor : listSensor){
                        senNames.add(sensor.getName());
                    }
                    selectDeviceDiag.setAdapter(senNames,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    listSensor.get(which).setTriggerId(triggerId);
                                    TriggerDeviceEntity entity = new TriggerDeviceEntity();
                                    entity.setName(listSensor.get(which).getName());
                                    entity.setDeviceId(listSensor.get(which).getId());
                                    entity.setTriggerId(triggerId);
                                    entity.setType(2);
                                    entity.setValue("nóng hơn 20 độ");
                                    triggerdevicesSQLite.insert(entity);
                                Log.d("----------", "onClick: " + listSensor.get(which).getName() + "---" + listSensor.get(which).getTriggerId());
//

                                    List<TriggerDeviceEntity> list = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,2);
                                    loadListSensorbyTriggerId(triggerId,list);

                                    dialog.dismiss();
                                }
                            });
                    selectDeviceDiag.show();
                }

            }
        });

        //--------------Adapter for select area

        Log.d("-------------------", "onClick: " + "-----------" );
    }

    @Override
    public void onTriggerClick(TriggerEntity triggerEntity) {

        this.triggerId = triggerEntity.getId();

        List<TriggerDeviceEntity> listDevice = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,1);
        loadListDevicebyTriggerId(triggerId,listDevice);
        List<TriggerDeviceEntity> listSensor = triggerdevicesSQLite.getDevicesByTriggerandTypeId(triggerId,2);
        loadListSensorbyTriggerId(triggerId,listSensor);
        Toast.makeText(this, "" + triggerEntity.getId(), Toast.LENGTH_SHORT).show();
    }



    public void addDevicetoTrigger(View view) {
        isDevice = true;
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

}
