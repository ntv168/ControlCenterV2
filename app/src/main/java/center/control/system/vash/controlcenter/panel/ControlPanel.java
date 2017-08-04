package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.sonyericsson.extras.liveware.aef.control.Control;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.PersonalInfoActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.SettingPanel;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.OwnerTrainEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.BotDataCentralDTO;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.FunctionIntentDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SmartHouseRequestDTO;
import center.control.system.vash.controlcenter.server.SocialIntentDTO;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.utils.MessageUtils;
import center.control.system.vash.controlcenter.voice.VoiceUtils;
import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.recognition.ImageHelper;

import center.control.system.vash.controlcenter.service.ControlMonitorService;
import center.control.system.vash.controlcenter.service.WebServerService;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.voice.ListeningActivity;
import center.control.system.vash.controlcenter.voice.VoiceRecognitionListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControlPanel extends ListeningActivity implements AreaAttributeAdapter.AttributeClickListener,
        AreaAdapter.AreaClickListener,DeviceAdapter.DeviceItemClickListener, VoiceUtils.OnSpeakFinish {
    private static final String TAG = "Control Panel";
    public static final String CONTROL_FILTER_RECEIVER = "smart.house.watch.SONY_ACTIVITY";
    public static final String ACTION_TYPE = "control action type receiver";
    public static final String AREA_ID = "service.area.check.id";
    public static final String WATCH_STT_CONTENT = "receiving_speech_to_text_from_sony";

    private SharedPreferences sharedPreferences;
    private String contractId;
    private RecyclerView lstDevice;
    private RecyclerView lstAreaAttribute;
    private Dialog remoteDialog;
    private Dialog cameraDialog;
    private BroadcastReceiver receiver;
    private AreaEntity currentArea;
    private Notification.Builder noticBuilder;
    private AreaAttributeAdapter areaAttributeAdapter;
    private DeviceAdapter deviceAdapter;
    private  String aquaintance;
    private boolean detecting;
    private String mPersonGroupId;
    private ProgressDialog waitDialog;
    private AlertDialog stateDialog;
    private AlertDialog lockDialog;
    private int cameraAreaId;
    private AlertDialog configUpdateDialog;
    private AlertDialog.Builder selectMode;
    private String sentenceReply;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"on resume");
        if (DetectIntentSQLite.findSocialById(ConstManager.NOT_UNDERSTD) == null){
            startActivity(new Intent(this, PersonalInfoActivity.class));
        }
        Intent webService = new Intent(ControlPanel.this, WebServerService.class);
        startService(webService);

        SmartHouse house = SmartHouse.getInstance();
        if (house.getAreas().size()>0) {
//            currentArea = house.getAreas().get(0);
//            areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
//            deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(currentArea.getId()));
            startService(new Intent(this, ControlMonitorService.class));
        } else {
            MessageUtils.makeText(this,"Nhân viên chưa cấu hình thiết bị").show();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        Bundle bun = getIntent().getBundleExtra("bundle");
        if (bun!= null) {
            Log.d(TAG, bun.getString("watch") + "");
            if (bun.getString("watch") != null &&
                    bun.getString("watch").equals("voice")) {
                stopListening();
                CurrentContext.getInstance().waitOwner();
                CurrentContext.getInstance().setDetectSocial(BotUtils.getSocialById(ConstManager.SOCIAL_APPEL));
//            CurrentContext.getInstance().setDetectSocial(social);
                showReply(BotUtils.completeSentence(CurrentContext.getInstance().getDetectSocial().getReplyPattern(), "", ""));
                setIntent(new Intent());
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void initControl(){
        SmartHouse.getInstance().setCurrentState(SmartHouse.getInstance().getStateById(ConstManager.OWNER_IN_HOUSE_STATE));
//        showReply(BotUtils.completeSentence(SmartHouse.getInstance().getCurrentState().getNoticePattern(),"",""));

        startListening(); // starts listening
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start voice running continuously
        setContentView(R.layout.activity_control_panel);
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        String botRole = sharedPreferences.getString(ConstManager.BOT_ROLE,"");
        String botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
        String ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");
        String ownerRole = sharedPreferences.getString(ConstManager.OWNER_ROLE,"");
        Log.d(TAG,botRole+"  "+botName+"  "+ownerName+"  "+ownerRole);
        SmartHouse.getInstance().setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
        ((TextView) findViewById(R.id.txtOwnerName)).setText(ownerName);
        contractId = sharedPreferences.getString(ConstManager.CONTRACT_ID,"");
        Log.d(TAG,"contractId Id " + contractId);
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton("Xin chào", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initControl();

            currentArea = SmartHouse.getInstance().getAreas().get(0);
            areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
            deviceAdapter.updateHouseDevice(SmartHouse.getInstance().getDevicesByAreaId(currentArea.getId()));
                dialog.dismiss();
            }
        });
        lockDialog = builder.create();

//        lockDialog.show();

        String Id = StorageHelper.getPersonGroupId("nguoinha",ControlPanel.this);
        if (!StorageHelper.getAllPersonIds(Id, ControlPanel.this).isEmpty()) {
            StorageHelper.clearPersonIds(Id,ControlPanel.this);
        }
        new GetPersonIdsTask().execute(Id);

        builder.setNegativeButton("Để sau", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CurrentContext.getInstance().renew();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateBot();
                dialog.dismiss();
            }
        });
        configUpdateDialog = builder.create();
        configUpdateDialog.setTitle("Cập nhật mới");
        configUpdateDialog.setCancelable(false);

        selectMode = new AlertDialog.Builder(ControlPanel.this);
        selectMode.setIcon(R.drawable.icn_mode);
        selectMode.setTitle("Chọn chế độ");

        ((ImageButton) findViewById(R.id.selectMode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> modeName = new ArrayAdapter<String>(ControlPanel.this,android.R.layout.select_dialog_singlechoice);

                for (ScriptEntity sde : SmartHouse.getInstance().getScripts()){
                    if (sde.getName() != null)
                        modeName.add(sde.getName());
                }

                selectMode.setAdapter(modeName,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ScriptEntity mode= SmartHouse.getInstance().getScripts().get(which);
                                DetectFunctionEntity funct = DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_START_MODE);
                                CurrentContext.getInstance().setDetectedFunction(funct);
                                CurrentContext.getInstance().setScript(mode);
                                for (CommandEntity cmd : ScriptSQLite.getCommandByScriptId(mode.getId())){
                                    SmartHouse.getInstance().addCommand(cmd);
                                }
                                dialog.dismiss();
                            }
                        });
                selectMode.show();
            }
        });

        if (contractId.length()<2){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        context = this.getApplicationContext();
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener

        final ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setImageResource(R.drawable.tab_home_active);
        currentTab.setBackgroundResource(R.drawable.background_tab_home_active);

        mPersonGroupId = "29f1ccf6-16a3-4e09-95c7-24e5e31a2acf";
        StorageHelper.setPersonGroupId(mPersonGroupId, "nguoinha", ControlPanel.this);
        detecting= false;

        if (StorageHelper.getAllPersonIds(mPersonGroupId, ControlPanel.this).isEmpty()) {

            StorageHelper.setPersonName("0d6b5e21-6404-482c-af20-ea239881fcf0","Thuận", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("b84c17d0-1c02-48b8-abc5-1ec270db3f2a","Tùng", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("64efd2f3-6429-4775-bc87-a56db12d6dde","Mỹ", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("7f0fa924-cc34-4a6b-92d5-a2328a1cc0b3","Văn", mPersonGroupId, ControlPanel.this);
        }


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
        lstDevice.setAdapter(deviceAdapter);

        SmartHouse house = SmartHouse.getInstance();
        AreaAdapter areaAdapter = new AreaAdapter(house.getAreas(),this);
        lstAreaName.setAdapter(areaAdapter);
        List<AreaAttribute> lstAttribute  = new ArrayList<>();
        for (int i=0; i<  AreaEntity.attrivutesValues.length; i++){
            AreaAttribute atttrr = new AreaAttribute();
            atttrr.setName(AreaEntity.attrivutesValues[i]);
            lstAttribute.add(atttrr);
        }
        areaAttributeAdapter = new AreaAttributeAdapter(lstAttribute,this);
        lstAreaAttribute.setAdapter(areaAttributeAdapter);

    }

    private void updateBot() {
        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        botApi.getDataVA(sharedPreferences.getInt(ConstManager.BOT_TYPE_ID,-1)
        ).enqueue(new Callback<BotDataCentralDTO>() {
            @Override
            public void onResponse(Call<BotDataCentralDTO> call, Response<BotDataCentralDTO> response) {
                Log.d(TAG,call.request().url()+"");
                if (response.body() != null) {
                    TermSQLite sqLite = new TermSQLite();
                    List<OwnerTrainEntity> trained = sqLite.getOwnerTrain();
                    Map<String, Map<String, Integer>> updatedFunct = BotUtils.updateFuncts(trained, response.body().getFunctionMap());
                    DetectIntentSQLite sqlDect = new DetectIntentSQLite();
                    sqLite.clearAll();
                    sqlDect.clearAll();

                    for (SocialIntentDTO soc : response.body().getSocials()) {
                        sqlDect.insertSocial(new DetectSocialEntity(soc.getId(),
                                soc.getName(), soc.getQuestion(), soc.getReply()));
                    }
                    for (FunctionIntentDTO funct : response.body().getFunctions()) {
                        sqlDect.insertFunction(new DetectFunctionEntity(funct.getId(),
                                funct.getName(), funct.getSuccess(), funct.getFail(), funct.getRemind()));
                    }
                    SmartHouse house = SmartHouse.getInstance();
                    BotUtils bot = new BotUtils();
                    bot.saveFunctionTFIDFTerm(updatedFunct);
                    bot.saveSocialTFIDFTerm(response.body().getSocialMap());
                    bot.saveDeviceTFIDFTerm(house.getDevices());
                    bot.saveAreaTFIDFTerm(house.getAreas());
                    bot.saveScriptTFIDFTerm(house.getScripts());
                    showReply(BotUtils.completeSentence(CurrentContext.getInstance().getDetectSocial().getReplyPattern(),"",""));
                } else {
                    showReply(BotUtils.completeSentence("Không kết nối được <owner-role> vui lòng tự cập nhật","",""));
                }
                waitDialog.dismiss();
                restartListeningService();

            }

            @Override
            public void onFailure(Call<BotDataCentralDTO> call, Throwable t) {
                Log.d(TAG,"down load bot data failed");
                showReply(BotUtils.completeSentence("Không kết nối được <owner-role> vui lòng tự cập nhật","",""));
                waitDialog.dismiss();
                restartListeningService();
            }
        });
        waitDialog.show();
    }
    private void updateConfig() {
        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        SmartHouseRequestDTO request = new SmartHouseRequestDTO();
        request.setContractId(contractId);
        request.setRequestContent("Yêu cầu nhân viên cập nhật cấu hình");
        request.setRequestDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        botApi.sendRequest(request).enqueue(new Callback<SmartHouseRequestDTO>() {
            @Override
            public void onResponse(Call<SmartHouseRequestDTO> call, Response<SmartHouseRequestDTO> response) {
                waitDialog.dismiss();
                Log.d(TAG,call.request().url()+"");
                showReply(BotUtils.completeSentence(CurrentContext.getInstance().getDetectSocial().getReplyPattern(),"",""));
                restartListeningService();
            }

            @Override
            public void onFailure(Call<SmartHouseRequestDTO> call, Throwable t) {
                waitDialog.dismiss();
                Log.d(TAG,call.request().url()+"");
                showReply(BotUtils.completeSentence("Không kết nối được <owner-role> vui lòng tự cập nhật","",""));
                restartListeningService();
            }
        });
        waitDialog.show();
    }

    private void checkConfiguration(AreaEntity area) {
        EventEntity nextEv = null;
        int maxPrio = -2;
        String result = "";
        for (EventEntity ev : SmartHouse.getInstance().getCurrentState().getEvents()){
            Log.d(TAG, ev.getAreaId()+" "+ev.getSenName()+" "+ev.getSenValue() );
            if (ev.getAreaId() == area.getId()){
                if (ev.getSenName().equals(AreaEntity.attrivutesValues[0]) &&
                        ev.getSenValue().equals(area.getSafety())){
                    if (ev.getPriority() >maxPrio){
                        Log.d(TAG, ev.getNextStateId()+" " );
                        maxPrio = ev.getPriority();
                        nextEv = ev;
                        if (aquaintance == null)
                            result = area.getName();
                        else result = aquaintance;
                    }
                } else if (ev.getSenName().equals(AreaEntity.attrivutesValues[1]) &&
                        ev.getSenValue().equals(area.getBright())){
                    if (ev.getPriority() >maxPrio){
                        Log.d(TAG, ev.getNextStateId()+" " );
                        maxPrio = ev.getPriority();
                        result = area.getName();
                        nextEv = ev;
                    }
                }else if (ev.getSenName().equals(AreaEntity.attrivutesValues[2]) &&
                        ev.getSenValue().equals(area.getTemperature())){
                    if (ev.getPriority() >maxPrio){
                        Log.d(TAG, ev.getNextStateId()+" " );
                        maxPrio = ev.getPriority();
                        result = area.getName();
                        nextEv = ev;
                    }
                } else if (ev.getSenName().equals(AreaEntity.attrivutesValues[3]) &&
                        area.getRawDetect().contains(ev.getSenValue())){
                    if (ev.getPriority() >maxPrio){
                        Log.d(TAG, ev.getNextStateId()+" " );
                        maxPrio = ev.getPriority();
                        result = aquaintance;
                        nextEv = ev;
                    }
                }
            }
        }
        if (nextEv != null)
        changeState(nextEv,result);
    }
    private void changeState(EventEntity ev,String resultValue){
        SmartHouse house = SmartHouse.getInstance();
        house.setCurrentState(SmartHouse.getInstance().getStateById(ev.getNextStateId()));
        Log.d(TAG,house.getCurrentState().getName())
        ;
        house.setStateChangedTime((new Date()).getTime());

        if (!house.isDefaultState()) {
            showAlertState(house.getCurrentState(), resultValue);

            showReply(BotUtils.completeSentence(SmartHouse.getInstance().getCurrentState().getNoticePattern(), resultValue, ""));
        }  else
        if (stateDialog!= null &&stateDialog.isShowing()){
            stateDialog.dismiss();
        }
    }

    private void showAlertState(StateEntity state, String resultValue) {
        if (state.getCommands().size()>0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (SmartHouse.getInstance().getCurrentState().isActivated()){
//                        SmartHouse.getInstance().revertCmdState();
                    }
                    SmartHouse.getInstance().resetStateToDefault();
                    startListening();
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Kích hoạt NGAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SmartHouse.getInstance().startConfigCmds();
                    CurrentContext.getInstance().setScript(null);
                    SmartHouse.getInstance().setStateChangedTime(
                            (new Date().getTime()) - SmartHouse.getInstance().getCurrentState().getDelaySec()*1000);
                    startListening();
                    dialog.dismiss();
                }
            });
            stateDialog = builder.create();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SmartHouse.getInstance().resetStateToDefault();
                    startListening();
                    dialog.dismiss();
                }
            });
            stateDialog = builder.create();
        }
        if (stateDialog.isShowing()){
            stateDialog.dismiss();
        }
        stateDialog.setTitle(state.getName());
        String command  = "";
        for (CommandEntity cmd : state.getCommands()){
            command += (cmd.getDeviceState().equals("on")?"bật":"tắt")+" "+
                    SmartHouse.getInstance().getDeviceById(cmd.getDeviceId()).getName()+" trong "
                    + SmartHouse.getAreaById(SmartHouse.getInstance().getDeviceById(cmd.getDeviceId()).getAreaId()).getName() +"\n";
        }
        stateDialog.setMessage(BotUtils.completeSentence(state.getNoticePattern(),resultValue,"")+
                "\n"+ (command.length()>2?command:"Thông báo sẽ tự động tắt"));
        stateDialog.setCancelable(false);
        stateDialog.show();
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
        finish();
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, UserSettingPanel.class));
        finish();
    }

    public void clicktoVAPanel(View view) {
        promptSpeechInput("");
    }

    @Override
    public void onInitFinish() {
        waitDialog.dismiss();
        lockDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceUtils.stopSpeakApi();
        stopService(new Intent(getApplicationContext(),WebServerService.class));
        Log.d(TAG," destroy ");
    }


    @Override
    public void onAreaClick(AreaEntity area) {
        currentArea = area;
        areaAttributeAdapter.updateAttribute(area.generateValueArr());
        SmartHouse house = SmartHouse.getInstance();
        deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(area.getId()));
    }
    @Override
    public void onAttributeClick(AreaAttribute areaAttribute, int areaId) {

    }

    @Override
    public void onDeviceClick(final DeviceEntity device) {
        CurrentContext.getInstance().stopWaitOwner();
        ((TextView) remoteDialog.findViewById(R.id.txtRemoteName)).setText(device.getName()+" ở "+currentArea.getName()+ " ");
        if (DeviceEntity.remoteTypes.contains(device.getType())){

            final ImageButton btnOnOff = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteOnOff);
            btnOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrentContext.getInstance().setScript(null);
                    if (device.getState().equals("on")) {
                         SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(), "off"));
                        waitDialog.show();
                    } else if (device.getState().equals("off")) {
                         SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(), "on"));
                        waitDialog.show();
                    }
                }
            });
            ImageButton btnInc = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteInc);
            btnInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrentContext.getInstance().setDevice(device);
                    CurrentContext.getInstance().setScript(null);
                    CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_DEC_TEMP));
                    SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"inc"));
                    waitDialog.show();
                }
            });
            ImageButton btnDec = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteDec);
            btnDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrentContext.getInstance().setDevice(device);
                    CurrentContext.getInstance().setScript(null);
                    CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_DEC_TEMP));
                    SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"dec"));
                    waitDialog.show();
                }
            });
            remoteDialog.show();
        } else {
            CurrentContext.getInstance().setScript(null);
            if (device.getState().equals("on")){
                SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"off"));
                waitDialog.show();
            } else {
                SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"on"));
                waitDialog.show();
            }
        }
    }



    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());


        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }


    @Override
    public void onFinish() {
        Log.d(TAG, CurrentContext.getInstance().isWaitingOwnerSpeak()+ "  ");
        if (CurrentContext.getInstance().isWaitingOwnerSpeak()) {
            promptSpeechInput(sentenceReply);
        }

        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }

    private class DetectionTask extends AsyncTask<InputStream, String, com.microsoft.projectoxford.face.contract.Face[]> {
        long startdetect = System.currentTimeMillis();
        @Override
        protected com.microsoft.projectoxford.face.contract.Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.

            FaceServiceClient faceServiceClient = App.getFaceServiceClient();
            try{
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.

        }

        @Override
        protected void onPostExecute(com.microsoft.projectoxford.face.contract.Face[] result) {
            long enddetect= System.currentTimeMillis();
            Log.d("--------------", "time detect --------------- " + result.length);

            if (result != null) {
                // Set the adapter of the ListView which contains the details of detectingfaces.
                List<com.microsoft.projectoxford.face.contract.Face> faces = Arrays.asList(result);


                if (result.length == 0) {
                    detecting= false;
                    setDetectMessage(AreaEntity.NOBODY);
                } else {
                    detecting= true;

                    // Called identify after detection.
                    if (detecting&& mPersonGroupId != null) {
                        // Start a background task to identify faces in the image.
                        List<UUID> faceIds = new ArrayList<>();
                        for (com.microsoft.projectoxford.face.contract.Face face:  faces) {
                            faceIds.add(face.faceId);

                            Log.d(TAG, "------------------------: " + face.faceId.toString());
                        }


                        new IdentificationTask(mPersonGroupId).execute(
                                faceIds.toArray(new UUID[faceIds.size()]));
                        Log.d("-------", "identify: facezise" + faceIds.size());
                    } else {
                        // Not detectingor person group exists.
                        Log.d(TAG,AreaEntity.DETECT_NOT_AVAILABLE + " khong detect diuoc");
                    }
                }
            } else {
                detecting= false;
            }

        }

    }
    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {
        String mPersonGroupId;
        long startidentify = System.currentTimeMillis();
        IdentificationTask(String personGroupId) {
            this.mPersonGroupId = personGroupId;
            Log.d("--------", "IdentificationTask: " + personGroupId);
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            String logString = "Request: Identifying faces ";
            for (UUID faceId: params) {
                logString += faceId.toString() + ", ";
            }
            logString += " in group " + mPersonGroupId;
            Log.d("--------", "IdentificationTask: " + mPersonGroupId);
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = App.getFaceServiceClient();
            try{
                publishProgress("Getting person group status...");

                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(
                        this.mPersonGroupId);     /* personGroupId */

                Log.d("--------", "trainingStatus: " + trainingStatus);

                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    publishProgress("Person group training status is " + trainingStatus.status);
                    return null;
                }

                publishProgress("Identifying...");

                // Start identification.
                return faceServiceClient.identity(
                        this.mPersonGroupId,   /* personGroupId */
                        params,                  /* faceIds */
                        1);  /* maxNumOfCandidatesReturned */
            }  catch (Exception e) {

                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
//
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(IdentifyResult[] result) {
            long endidentify= System.currentTimeMillis();
            Log.d("----------------", "time identity: ------------- " + (endidentify - startidentify));
            // Show the result on screen when detection is done.
            // Set the information about the detection result.
            if (result != null) {

                String message = AreaEntity.DETECT_AQUAINTANCE +" ";
                aquaintance = "";
                Boolean hasAqua = false;
                int stranger = 0;
                for (IdentifyResult identifyResult: result) {
                    if (identifyResult.candidates.size() > 0) {
                        if (identifyResult.candidates.get(0).confidence > 0.65) {
                            String personId = identifyResult.candidates.get(0).personId.toString();
                            String personName = StorageHelper.getPersonName(
                                    personId, mPersonGroupId, ControlPanel.this);

                            message += personName+" ";
                            aquaintance += personName+" ";

                            hasAqua = true;
                        } else {
                            stranger++;
                        }
                    } else {
                        stranger++;
                    }
                }
                if (!hasAqua) {
                    message = AreaEntity.DETECT_STRANGE;
                }

                setDetectMessage(message);

            }
        }

    }

    public void clicktoControlPanel(View v){
    }

    private void setDetectMessage(String info) {
        startService(new Intent(this,ControlMonitorService.class));
        AreaEntity camArea = SmartHouse.getAreaById(cameraAreaId);
        camArea.setDetect(info);
        camArea.setUpdatePerson((new Date()).getTime());
        SmartHouse.getInstance().updateAreaById(cameraAreaId,camArea);
        Log.d(TAG,camArea.getName()+"  "+info+"  detect nguoi");

        checkConfiguration(camArea);

//        showReply(BotUtils.completeSentence(camArea.getDetect(),info.substring(4,info.length()-1),""));
        cameraDialog.dismiss();
        if (currentArea!= null && currentArea.getId() == cameraAreaId) {
            areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
        }
        restartListeningService();
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
                            StorageHelper.setPersonName(person.personId.toString(), name, groupid, ControlPanel.this);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
//                MessageUtils.makeText(ControlPanel.this, "Không kết nối được dữ liệu nhận diện hình ảnh").show();
            }
        }
    }
    private void showReply(String sentenceReply){
        this.sentenceReply = sentenceReply;
        if (sentenceReply.contains("ác nhậ")){
            CurrentContext.getInstance().stopWaitOwner();
            stopListening();
        }else if (sentenceReply.contains("Hình từ")){
            SmartHouse house = SmartHouse.getInstance();
            Bitmap bmImg = house.getBitmapByAreaId(currentArea.getId());
            ImageView imgFace = (ImageView) cameraDialog.findViewById(R.id.imgFace);

            if (bmImg!=null){
                stopListening();
                imgFace.setImageBitmap(bmImg);
                cameraDialog.show();
                sentenceReply = "";
            } else {
                 sentenceReply = BotUtils.completeSentence(
                         CurrentContext.getInstance().getDetectedFunction().getFailPattern(), "", currentArea.getName());
            }
        }else if (CurrentContext.getInstance().getDetectSocial()!= null &&
                (CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.SAY_BYE ||
                CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.NOT_UNDERSTD||
                        CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.SOCIAL_THANK)) {
            restartListeningService();
            CurrentContext.getInstance().stopWaitOwner();
        } else if (CurrentContext.getInstance().getDetectSocial()!= null &&(
                CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.SOCIAL_AGREE ||
                CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.SOCIAL_DENY ) &&
                SmartHouse.getInstance().getCurrentState().getId() == ConstManager.NO_BODY_HOME_STATE) {
            stopListening();
            lockDialog.show();
            CurrentContext.getInstance().stopWaitOwner();
        }

        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_RING, false);
        VoiceUtils.speak(sentenceReply);
        Log.d(TAG, sentenceReply + "  " +CurrentContext.getInstance().isWaitingOwnerSpeak());
    }
    @Override
    protected void onPause() {
        super.onPause();
//        waitDialog.show();

        Log.i(TAG, "on pause called");
        if(sr!=null){
            sr.stopListening();
            sr.cancel();
            sr.destroy();

        }
        sr = null;
        stopService(new Intent(this,ControlMonitorService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
//            waitDialog.dismiss();
        }
        stopListening();
        stopService(new Intent(ControlPanel.this,ControlMonitorService.class));
        Log.d(TAG,"on stop");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        restartListeningService();
        if(VoiceUtils.getInstance() == null) {
            VoiceUtils.initializeInstance(this, this);
            waitDialog.show();
            Log.d(TAG,"init voice");
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultType = intent.getStringExtra(ACTION_TYPE);
                Log.d(TAG,resultType);
                if (SmartHouse.getInstance().isDefaultState()) {
                    if (stateDialog!= null && stateDialog.isShowing()){
                        stateDialog.dismiss();
                    }
                }
                if (resultType.equals(ControlMonitorService.BOT_UPDATE)){
                    CurrentContext currentContext = CurrentContext.getInstance();
                    currentContext.setDetectSocial(BotUtils.getSocialById(ConstManager.UPDATE_BRAIN));
                    String sentence = BotUtils.completeSentence(
                            currentContext.getDetectSocial().getQuestionPattern(),"","");
                    configUpdateDialog.setMessage(sentence);
                    showReply(sentence);
                    SmartHouse.getInstance().setRequireBotUpdate(false);
                    configUpdateDialog.show();
                } else if (resultType.equals(ControlMonitorService.NEW_UPDATE)){
                    CurrentContext currentContext = CurrentContext.getInstance();
                    currentContext.setDetectSocial(BotUtils.getSocialById(ConstManager.UPDATE_CONFIG));
                    String sentence = BotUtils.completeSentence(
                            currentContext.getDetectSocial().getQuestionPattern(),"","");
                    configUpdateDialog.setMessage(sentence);
                    showReply(sentence);
                    configUpdateDialog.show();
                    SmartHouse.getInstance().setRequireUpdate(false);
                } else if (resultType.equals(ControlMonitorService.PERSON_UPDATE)){
                    String Id = StorageHelper.getPersonGroupId("nguoinha",ControlPanel.this);
                    if (!StorageHelper.getAllPersonIds(Id, ControlPanel.this).isEmpty()) {
                        StorageHelper.clearPersonIds(Id,ControlPanel.this);
                    }
                    new GetPersonIdsTask().execute(Id);
                    SmartHouse.getInstance().setRequirePersonUpdate(false);
                } else
                if (resultType.equals(WebServerService.SERVER_SUCCESS)) {
                    noticBuilder.setContentText(resultType);
                    noticBuilder.setContentTitle("Server stated port 8080");
                    NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    man.notify(0,noticBuilder.build());

                }  else if (resultType.equals(ControlMonitorService.SCHEDULER) && CurrentContext.getInstance().getDetectedFunction() != null) {
                    Log.d(TAG,"scheduler sent");
                    CurrentContext cont = CurrentContext.getInstance();
                    showReply(BotUtils.completeSentence(cont.getDetectedFunction().getRemindPattern(),"",cont.getScript().getName()));
//                    cont.waitOwner();

                }else if (resultType.equals(ControlMonitorService.DEACTIVATE)){
                    sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(ConstManager.CONTRACT_ID,"");
                    edit.commit();
                    Log.d(TAG,"Deeactive sent");
                    startActivity(new Intent(ControlPanel.this, MainActivity.class));
                } else if (resultType.equals(ControlMonitorService.WAIT)){
                    Log.d(TAG,waitDialog.isShowing()+" stae diaalog");
                    if (!waitDialog.isShowing()) {
//                        waitDialog = new ProgressDialog(ControlPanel.this);
//                        waitDialog.setTitle("Vui lòng đợi");
//                        waitDialog.setIndeterminate(true);
//                        waitDialog.setCancelable(true);
                        waitDialog.show();
                    }
                } else if (resultType.equals(ControlMonitorService.CHANGE_STATE)){
                    SmartHouse house =SmartHouse.getInstance();
                    CurrentContext.getInstance().stopWaitOwner();
                    if (!house.isDefaultState()) {
                        showAlertState(house.getCurrentState(), "");
                        showReply(BotUtils.completeSentence(SmartHouse.getInstance().getCurrentState().getNoticePattern(), "", ""));
                    }  else
                    if (stateDialog!= null &&stateDialog.isShowing()){
                        stateDialog.dismiss();
                    }
                }else if (resultType.equals(ControlMonitorService.CONTROL)){
                    waitDialog.dismiss();
                    CurrentContext.getInstance().stopWaitOwner();
                    int result = intent.getIntExtra(AREA_ID, -1);
                    restartListeningService();

                    CurrentContext current = CurrentContext.getInstance();
                    String target = current.getDevice()!=null?current.getDevice().getName():current.getScript().getName();
                    if (result == ControlMonitorService.SUCCESS){
                        showReply(BotUtils.completeSentence(
                                current.getDetectedFunction().getSuccessPattern(),"",target));
                        SmartHouse house = SmartHouse.getInstance();
                        Log.d(TAG,house.getDevices().size()+" succ");
                        deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(currentArea.getId()));
                    } else if (result == ControlMonitorService.FAIL){
                        showReply(BotUtils.completeSentence(
                                current.getDetectedFunction().getFailPattern(),"",target));

                    }
                } else if (resultType.equals(ControlMonitorService.MONITOR)) {
                    int areaId = intent.getIntExtra(AREA_ID, -1);
//                    Log.d(TAG,currentArea.getId()+" , "+areaId);
                    if ( currentArea!=null && areaId == currentArea.getId()) {
                        areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
                        SmartHouse house = SmartHouse.getInstance();
                        deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(currentArea.getId()));
                    }
                    if (SmartHouse.getInstance().getCurrentState()!=null) {
                        checkConfiguration(SmartHouse.getAreaById(areaId));
                    }
                } else if (resultType.equals(ControlMonitorService.CAMERA)){
                    cameraAreaId = intent.getIntExtra(AREA_ID,-1);
                    SmartHouse house = SmartHouse.getInstance();
                    Bitmap bmImg = house.getBitmapByAreaId(cameraAreaId);
                    ImageView imgFace = (ImageView) cameraDialog.findViewById(R.id.imgFace);

                    if (bmImg!=null){
                        imgFace.setImageBitmap(bmImg);
                        File myDir =  Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES);
                        myDir.mkdirs();
                        String nameFile = "testSelf.jpg";
                        File file = new File(myDir, nameFile);
                        if (file.exists ()) file.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
//                                Log.d(TAG,file.getAbsolutePath());
                            bmImg.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.close();
                        } catch (IOException e){
                            Log.d(TAG, e.getMessage());
                        }

                        Uri uri = Uri.fromFile(file);
                        Bitmap mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                                uri, getContentResolver());

                        stopService(new Intent(ControlPanel.this,ControlMonitorService.class));
                        detect(mBitmap);
                    } else {
                        imgFace.setImageResource(R.drawable.close);
                    }
                    ((TextView) cameraDialog.findViewById(R.id.txtFaceResult)).setText("Hình từ "+SmartHouse.getAreaById(cameraAreaId).getName());
                    cameraDialog.show();

                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(CONTROL_FILTER_RECEIVER));

        Log.d(TAG,"on start");
    }

    private void promptSpeechInput(String message) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,message);
        try {
            startActivityForResult(intent, VAPanel.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            MessageUtils.makeText(this,"Không kết nối được nhận diện giọng nói").show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onAct Result: " +requestCode+"--" );
        switch (requestCode) {
            case VAPanel.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1 && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String res = result.get(0).toLowerCase(new Locale("vi","VN"));
                    Log.d(TAG,res);
                    showReply(BotUtils.botReplyToSentence(res));
                } else {
                    restartListeningService();
                }
                break;
            }
            case Activity.RESULT_CANCELED:{

                Log.d(TAG,"onAct Result: nát nát nát--" );
            }
        }
    }

    //Progress for voice running continuously
    @Override
    public void processVoiceCommands(String... voiceCommands) {

        List<TermEntity> terms = (new TermSQLite()).getHumanIntentInSentence(" "+voiceCommands[0]+" ");
        DetectSocialEntity social = BotUtils.findBestSocialDetected(terms);
        SmartHouse house = SmartHouse.getInstance();
        if (social!=null) {
            Log.d(TAG, social.getName());
        }
        Log.d(TAG, "processVoiceCommands: "+ voiceCommands[0].toString()+ "    "+(social!=null?social.getName():"null"));
        if (social != null && social.getId() == ConstManager.SOCIAL_APPEL) {
            stopListening();
            CurrentContext.getInstance().waitOwner();
            CurrentContext.getInstance().setDetectSocial(social);
            showReply(BotUtils.completeSentence(social.getReplyPattern(),"",""));
        }else if (social!= null && social.getId() == ConstManager.SOCIAL_AGREE &&
                CurrentContext.getInstance().getDetectSocial() !=null) {
            if (configUpdateDialog.isShowing()){
                configUpdateDialog.dismiss();
            }
            if (CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.UPDATE_BRAIN) {
                stopListening();
                updateBot();
            }
            if (CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.UPDATE_CONFIG){
                stopListening();
                updateConfig();
            }
        }
        else if (social!= null && social.getId() == ConstManager.SOCIAL_AGREE &&
                !house.isDefaultState()) {
            Log.d(TAG,"kich hoat cau hinh "+house.getCurrentState().getName());
            CurrentContext.getInstance().setScript(null);
            house.startConfigCmds();
            house.setStateChangedTime(
                    (new Date().getTime()) - house.getCurrentState().getDelaySec()*1000);
            if (stateDialog.isShowing()){
                stateDialog.dismiss();
            }
        } else if (social!= null && social.getId() == ConstManager.SOCIAL_DENY) {
            if (configUpdateDialog != null &&configUpdateDialog.isShowing()){
                configUpdateDialog.dismiss();
            }
            if (stateDialog!= null && stateDialog.isShowing()){
                stateDialog.dismiss();
            }
            house.setRequireBotUpdate(false);
            house.setRequireUpdate(false);
            house.resetStateToDefault();
            CurrentContext.getInstance().renew();
            restartListeningService();
        } else {
            restartListeningService();
        }
//        Toast.makeText(this, voiceCommands[0], Toast.LENGTH_SHORT).show();
    }

}
