package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.script.ScriptDeviceEntity;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.server.WebServer;
import center.control.system.vash.controlcenter.service.ControlMonitorService;
import center.control.system.vash.controlcenter.service.WebServerService;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

public class ControlPanel extends Activity implements AreaAttributeAdapter.AttributeClickListener,
        AreaAdapter.AreaClickListener,DeviceAdapter.DeviceItemClickListener{
    private static final String TAG = "Control Panel";
    public static final String CONTROL_FILTER_RECEIVER = "control filter receiver";
    public static final String ACTION_TYPE = "control action type receiver";
    public static final String AREA_ID = "service.area.check.id";

    private SharedPreferences sharedPreferences;
    private String systemId;
    private RecyclerView lstDevice;
    private RecyclerView lstAreaAttribute;
    private Dialog remoteDialog;
    private Dialog cameraDialog;
    private BroadcastReceiver receiver;
    private AreaEntity currentArea;
    private Notification.Builder noticBuilder;
    private AreaAttributeAdapter areaAttributeAdapter;
    private DeviceAdapter deviceAdapter;
    private DeviceEntity currentDevice;
    private String currentAttrib;

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        systemId = sharedPreferences.getString(ConstManager.SYSTEM_ID,"");
        SmartHouse house = SmartHouse.getInstance();
        if (house.getAreas().size()>0) {
            currentArea = house.getAreas().get(0);
            currentAttrib = AreaEntity.attrivutesValues[0];
        } else {
            startActivity(new Intent(this,ManageDeviceActivity.class));
        }
        startService(new Intent(this, ControlMonitorService.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        final ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setImageResource(R.drawable.tab_home_active);
        currentTab.setBackgroundResource(R.drawable.background_tab_home_active);

        Intent webService = new Intent(ControlPanel.this, WebServerService.class);
        startService(webService);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultType = intent.getStringExtra(ACTION_TYPE);
                Toast.makeText(ControlPanel.this, resultType, Toast.LENGTH_SHORT).show();
                if (resultType.equals(WebServerService.SERVER_SUCCESS)) {
                    noticBuilder.setContentText(resultType);
                    noticBuilder.setContentTitle("Server stated port "+8080);
                    NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    man.notify(0,noticBuilder.build());

                }  else if (resultType.equals(ControlMonitorService.CONTROL)){
                        SmartHouse house = SmartHouse.getInstance();
                        deviceAdapter.updateHouseDevice(house.getDevicesInAreaAttribute(currentArea.getId(),currentAttrib));
                } else if (resultType.equals(ControlMonitorService.MONITOR)) {
                    int areaId = intent.getIntExtra(AREA_ID, -1);
                    if ( currentArea!=null && areaId == currentArea.getId()) {
                        areaAttributeAdapter.updateAttribute(SmartHouse.getAreaById(areaId).generateValueArr(),areaId);
                    }
                } else if (resultType.equals(ControlMonitorService.CAMERA)){
                    int areaId = intent.getIntExtra(AREA_ID,-1);
                    if ( currentArea!=null && areaId == currentArea.getId()) {
                        SmartHouse house = SmartHouse.getInstance();
                        Bitmap bmImg = house.getBitmapByAreaId(areaId);
                        ImageView imgFace = (ImageView) cameraDialog.findViewById(R.id.imgFace);
                        TextView txtResult = (TextView) cameraDialog.findViewById(R.id.txtFaceResult);
                        txtResult.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cameraDialog.dismiss();
                            }
                        });
                        if (bmImg!=null){
                            imgFace.setImageBitmap(bmImg);
                            Facedetect singleFace = Facedetect.getInstance(context);

                            Frame frame = new Frame.Builder().setBitmap(bmImg).build();
                            SparseArray<Face> faces = singleFace.getSafeDetector().detect(frame);
                            txtResult.setText("Có "+ faces.size()+" khách đến");
                        } else {
                            imgFace.setImageResource(R.drawable.close);
                        }
                        cameraDialog.show();
                    }
                }

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(CONTROL_FILTER_RECEIVER));

        noticBuilder = new Notification.Builder(this);
        noticBuilder.setSmallIcon(R.drawable.icon);

        remoteDialog = new Dialog(this);
        remoteDialog.setTitle("Điều khiển");
        remoteDialog.setContentView(R.layout.remote_device_dialog);

        cameraDialog = new Dialog(this);
        cameraDialog.setTitle("Hình ảnh từ camera");
        cameraDialog.setContentView(R.layout.camera_dialog);

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

        deviceAdapter = new DeviceAdapter(new ArrayList<DeviceEntity>(),this);

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
        stopService(new Intent(this,ControlMonitorService.class));
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
        currentAttrib = areaAttribute.getName();
        deviceAdapter.updateHouseDevice(house.getDevicesInAreaAttribute(areaId,areaAttribute.getName()));
        lstDevice.setAdapter(deviceAdapter);
    }

    @Override
    public void onDeviceClick(final DeviceEntity device) {
        currentDevice = device;
        ((TextView) remoteDialog.findViewById(R.id.txtRemoteName)).setText(device.getName()+" ở "+currentArea.getName()+ " ");
        if (DeviceEntity.remoteTypes.contains(device.getType())){
            ImageButton btnOn = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteOn);
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"on"));
                    waitDialog(2000);
                }
            });
            ImageButton btnOff = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteOff);
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"off"));
                    waitDialog(2000);
                }
            });
            ImageButton btnInc = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteInc);
            btnInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"inc"));
                    waitDialog(2000);
                }
            });
            ImageButton btnDec = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteDec);
            btnDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"dec"));
                    waitDialog(2000);
                }
            });
            remoteDialog.show();
        } else {
            if (device.getState().equals("on")){
                SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"off"));
            } else {
                SmartHouse.getInstance().addCommand(new ScriptDeviceEntity(device.getId(),"on"));
            }
        }
    }
    private void waitDialog(int sec){
        final ProgressDialog waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        waitDialog.show();

        long delayInMillis = sec;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                waitDialog.dismiss();
            }
        }, delayInMillis);
    }

}
