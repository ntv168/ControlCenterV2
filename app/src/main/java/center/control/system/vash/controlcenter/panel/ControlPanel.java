package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.service.ReadSensorService;
import center.control.system.vash.controlcenter.service.WebServerService;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ControlPanel extends Activity implements AreaAttributeAdapter.AttributeClickListener,
        AreaAdapter.AreaClickListener,DeviceAdapter.DeviceItemClickListener{
    private static final String TAG = "Control Panel";
    public static final String CONTROL_FILTER_RECEIVER = "control filter receiver";
    private SharedPreferences sharedPreferences;
    private String systemId;
    RecyclerView lstDevice;
    RecyclerView lstAreaAttribute;
    private Dialog remoteDialog;
    private BroadcastReceiver receiver;
    private AreaEntity currentArea;

    AreaAttributeAdapter areaAttributeAdapter;
    DeviceAdapter deviceAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        systemId = sharedPreferences.getString(ConstManager.SYSTEM_ID,"");
        startService(new Intent(this, ReadSensorService.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setBackgroundColor(Color.WHITE);
        Intent webService = new Intent(ControlPanel.this, WebServerService.class);
        startService(webService);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(ControlPanel.this,intent.getStringExtra(WebServerService.RESULT),Toast.LENGTH_SHORT).show();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(CONTROL_FILTER_RECEIVER));

        Log.d(TAG,"start broadcast");

        remoteDialog = new Dialog(this);
        remoteDialog.setTitle("Điều khiển");
        remoteDialog.setContentView(R.layout.remote_device_dialog);

        RecyclerView lstAreaName = (RecyclerView) findViewById(R.id.lstAreaName);
        lstAreaName.setHasFixedSize(true);
        LinearLayoutManager horizonLayout = new LinearLayoutManager(this);
        horizonLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        lstAreaName.setLayoutManager(horizonLayout);

        LinearLayoutManager horizonLayout2 = new LinearLayoutManager(this);
        horizonLayout2.setOrientation(LinearLayoutManager.HORIZONTAL);
        lstAreaAttribute = (RecyclerView) findViewById(R.id.lstAreaAttribute);
        lstAreaName.setHasFixedSize(true);
        lstAreaAttribute.setLayoutManager(horizonLayout2);


        lstDevice = (RecyclerView) findViewById(R.id.lstDevice);
        lstDevice.setHasFixedSize(true);
        GridLayoutManager horizonLayout3 = new GridLayoutManager(getApplicationContext(),2);
        horizonLayout3.setOrientation(GridLayoutManager.HORIZONTAL);
        lstDevice.setLayoutManager(horizonLayout3);

        SmartHouse house = SmartHouse.getInstance();
        AreaAdapter areaAdapter = new AreaAdapter(house.getAreas(),this);
        lstAreaName.setAdapter(areaAdapter);
        List<AreaAttribute> lstAttribute  = new ArrayList<>();
        for (int i=0; i<  AreaEntity.attrivutesValues.length; i++){
            AreaAttribute atttrr = new AreaAttribute();
            atttrr.setName(AreaEntity.attrivutesValues[i]);
            lstAttribute.add(atttrr);
        }
        areaAttributeAdapter = new AreaAttributeAdapter(lstAttribute,this,house.getAreas().size() >0? house.getAreas().get(0).getId(): -1);
        lstAreaAttribute.setAdapter(areaAttributeAdapter);
    }
    public void clicktoControlPanel(View view) {
        startActivity(new Intent(this, ControlPanel.class));
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, SettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
        startActivity(new Intent(this, VAPanel.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this,ReadSensorService.class));
    }

    @Override
    public void onAreaClick(AreaEntity area) {
        currentArea = area;
        areaAttributeAdapter.updateAttribute(area.generateValueArr(),area.getId());
        Log.d(TAG," click ip");
    }

    @Override
    public void onAttributeClick(AreaAttribute areaAttribute, int areaId) {
        SmartHouse house = SmartHouse.getInstance();
        Log.d(TAG,areaAttribute.getName()+" --- "+ areaId);
        deviceAdapter = new DeviceAdapter(house.getDevicesInAreaAttribute(areaId,areaAttribute.getName()),this);
        lstDevice.setAdapter(deviceAdapter);
    }

    @Override
    public void onDeviceClick(DeviceEntity device) {
        ((TextView) remoteDialog.findViewById(R.id.txtRemoteName)).setText("http://"+currentArea.getConnectAddress()+"/"+device.getPort());
        if (DeviceEntity.remoteTypes.contains(device.getType())){
            remoteDialog.show();
        } else {
            Log.d(TAG, "http://"+currentArea.getConnectAddress()+"/"+device.getPort());
        }
    }
}
