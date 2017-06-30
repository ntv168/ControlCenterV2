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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ListDevicesTriggerAdapter;
import center.control.system.vash.controlcenter.sensor.SensorEntity;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class MapDeviceTriggerActivity extends AppCompatActivity implements
        RecyclerViewTriggerAdapter.OnAdapterItemClickListener {

    private AlertDialog.Builder selectDeviceDiag;
    private AlertDialog.Builder selectAreaDiag;
    private AlertDialog.Builder selectSensorDiag;
    private int triggerId = 1;
    List<DeviceEntity> listDevices;
    DeviceSQLite deviceSQLite = new DeviceSQLite();
    SmartHouse house;
    TriggerSQLite triggerSQLite = new TriggerSQLite();
    ListDevicesTriggerAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_configuration);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);

        // RecyclerView for List Trigger
        List<TriggerEntity> listTrigger = triggerSQLite.getAll();

        RecyclerView lstAreaManage = (RecyclerView) findViewById(R.id.listItemLeft);
        lstAreaManage.setHasFixedSize(true);

        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        lstAreaManage.setLayoutManager(verticalLayout);

        RecyclerViewTriggerAdapter triggerAdapter = new RecyclerViewTriggerAdapter(listTrigger, this);
        RecyclerView rwTrigger = (RecyclerView) findViewById(R.id.listItemLeft);
        rwTrigger.setAdapter(triggerAdapter);

        // RecyclerView for List Device Trigger

        listDevices = deviceSQLite.getDevicesByTriggerId(triggerId);
        Toast.makeText(MapDeviceTriggerActivity.this, listDevices.size() + "", Toast.LENGTH_SHORT).show();

        final ListView lstDevices = (ListView) findViewById(R.id.listItemCenter);

        deviceAdapter = new ListDevicesTriggerAdapter(MapDeviceTriggerActivity.this, listDevices);
        lstDevices.setAdapter(deviceAdapter);


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
                final List<DeviceEntity> listDevice = house.getDevicesByAreaId(areaId);
                ArrayAdapter<String> devNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                for (DeviceEntity device : listDevice){
                        devNames.add(device.getName());
                }
                selectDeviceDiag.setAdapter(devNames,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                listDevice.get(which).setTriggerId(triggerId);
                                deviceSQLite.upById(listDevice.get(which).getId(), listDevice.get(which));
                                Log.d("----------", "onClick: " + listDevice.get(which).getName() + "---" + listDevice.get(which).getTriggerId());

                                listDevices = deviceSQLite.getAll();
                                deviceAdapter = new ListDevicesTriggerAdapter(MapDeviceTriggerActivity.this, listDevices);
                                lstDevices.setAdapter(deviceAdapter);
                                Toast.makeText(MapDeviceTriggerActivity.this, listDevices.get(0).getTriggerId() + "", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                selectDeviceDiag.show();
            }
        });

        //----------------Dialog for add new sensor
        selectSensorDiag = new AlertDialog.Builder(MapDeviceTriggerActivity.this);
        selectSensorDiag.setIcon(R.drawable.add);
        selectSensorDiag.setTitle("Chọn cảm biến:");
        selectSensorDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        selectSensorDiag.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
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
                final List<SensorEntity> listSensor = house.getSensorByAreaId(areaId);
                ArrayAdapter<String> senNames = new ArrayAdapter<String>(MapDeviceTriggerActivity.this,android.R.layout.select_dialog_singlechoice);

                for (SensorEntity sensor : listSensor){
                    senNames.add(sensor.getName());
                }
                selectDeviceDiag.setAdapter(senNames,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                listSensor.get(which).setTriggerId(triggerId);
//                                .upById(listSensor.get(which).getId(), listDevice.get(which));
//                                Log.d("----------", "onClick: " + listDevice.get(which).getName() + "---" + listDevice.get(which).getTriggerId());
//
//                                listDevices = deviceSQLite.getAll();
//                                deviceAdapter = new ListDevicesTriggerAdapter(MapDeviceTriggerActivity.this, listDevices);
//                                lstDevices.setAdapter(deviceAdapter);
//                                Toast.makeText(MapDeviceTriggerActivity.this, listDevices.get(0).getTriggerId() + "", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                selectDeviceDiag.show();
            }
        });


        Log.d("-------------------", "onClick: " + "-----------" );
    }

    @Override
    public void onTriggerClick(TriggerEntity triggerEntity) {
        Toast.makeText(this, "" + triggerEntity.getId(), Toast.LENGTH_SHORT).show();
        triggerId = triggerEntity.getId();

    }



    public void onClicktoArea(View view) {
        selectAreaDiag.show();
    }
}
