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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.PersonalInfoActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
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

public class ControlPanel extends ListeningActivity implements AreaAttributeAdapter.AttributeClickListener,
        AreaAdapter.AreaClickListener,DeviceAdapter.DeviceItemClickListener, VoiceUtils.OnSpeakFinish {
    private static final String TAG = "Control Panel";
    public static final String CONTROL_FILTER_RECEIVER = "control filter receiver";
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
    private FaceServiceClient fsClient;
    private boolean detecting;
    private String mPersonGroupId;
    private ProgressDialog waitDialog;
    private AlertDialog stateDialog;

    @Override
    protected void onResume() {
        super.onResume();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);

        if (DetectIntentSQLite.findSocialById(ConstManager.NOT_UNDERSTD) == null){
            startActivity(new Intent(this, PersonalInfoActivity.class));
        }
        SmartHouse house = SmartHouse.getInstance();
        String botRole = sharedPreferences.getString(ConstManager.BOT_ROLE,"");
        String botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
        String ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");
        String ownerRole = sharedPreferences.getString(ConstManager.OWNER_ROLE,"");
        if (DetectIntentSQLite.findSocialById(ConstManager.NOT_UNDERSTD) == null){
            startActivity(new Intent(this, PersonalInfoActivity.class));
            finish();
        }
        SmartHouse.getInstance().setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
        ((TextView) findViewById(R.id.txtOwnerName)).setText(ownerName);
        if (house.getAreas().size()>0) {
            currentArea = house.getAreas().get(0);
            areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
            deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(currentArea.getId()));
            startService(new Intent(this, ControlMonitorService.class));
        } else {
            Toast.makeText(this,"Nhân viên chưa cấu hình thiết bị",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        if(VoiceUtils.getInstance() == null) {
            VoiceUtils.initializeInstance(this, this);
            waitDialog.show();
        }

        Intent webService = new Intent(ControlPanel.this, WebServerService.class);
        startService(webService);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start voice running continuously
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        contractId = sharedPreferences.getString(ConstManager.CONTRACT_ID,"");
        Log.d(TAG,"contractId Id " + contractId);
        if (contractId.length()<2){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        context = this.getApplicationContext();
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening
        CurrentContext.getInstance().stopWaitOwner();

        setContentView(R.layout.activity_control_panel);

        final ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setImageResource(R.drawable.tab_home_active);
        currentTab.setBackgroundResource(R.drawable.background_tab_home_active);


        fsClient = Facedetect.getInstance(ControlPanel.this);


        mPersonGroupId = "29f1ccf6-16a3-4e09-95c7-24e5e31a2acf";
        StorageHelper.setPersonGroupId(mPersonGroupId, "nguoinha", ControlPanel.this);
        detecting= false;

        if (StorageHelper.getAllPersonIds(mPersonGroupId, ControlPanel.this).isEmpty()) {
            StorageHelper.setPersonName("4f50dd8f-0af7-4831-bf7a-9e94b8625950","Thuận", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("8fbd8bcc-f90f-4dde-9e76-8cbd9032a962","Tùng", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("cf784479-a176-4868-af86-7447715357f7","Mỹ", mPersonGroupId, ControlPanel.this);
            StorageHelper.setPersonName("6d639f61-0df1-44b6-b0a3-1c2d1024edf2","Văn", mPersonGroupId, ControlPanel.this);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultType = intent.getStringExtra(ACTION_TYPE);
//                Toast.makeText(ControlPanel.this, resultType, Toast.LENGTH_SHORT).show();
                Log.d(TAG,resultType+" ");
                if (resultType.equals(WebServerService.SERVER_SUCCESS)) {
                    noticBuilder.setContentText(resultType);
                    noticBuilder.setContentTitle("Server stated port "+8080);
                    NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    man.notify(0,noticBuilder.build());

                } else if (resultType.equals(ControlMonitorService.DEACTIVATE)){
                    SharedPreferences sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString(ConstManager.CONTRACT_ID,"");
                    edit.commit();
                    Log.d(TAG,"Deeactive sent");
                    startActivity(new Intent(ControlPanel.this, MainActivity.class));
                } else if (resultType.equals(ControlMonitorService.WAIT)){
                    Log.d(TAG,waitDialog.isShowing()+" stae diaalog");
                    if (!waitDialog.isShowing()) {
                        waitDialog = new ProgressDialog(ControlPanel.this);
                        waitDialog.setTitle("Vui lòng đợi");
                        waitDialog.setIndeterminate(true);
                        waitDialog.setCancelable(true);
                        waitDialog.show();
                    }
                } else if (resultType.equals(ControlMonitorService.CHANGE_STATE)){
//                    stopService(new Intent(ControlPanel.this,ControlMonitorService.class));
                    showAlertState(SmartHouse.getInstance().getCurrentState());
                    showReply(BotUtils.completeSentence(SmartHouse.getInstance().getCurrentState().getNoticePattern(),"",""));
                    stopListening();
                    Log.d(TAG,"state changed stop");
                    CurrentContext.getInstance().stopWaitOwner();
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
                    if ( currentArea!=null && areaId == currentArea.getId()) {
                        areaAttributeAdapter.updateAttribute(currentArea.generateValueArr());
                        SmartHouse house = SmartHouse.getInstance();
                        deviceAdapter.updateHouseDevice(house.getDevicesByAreaId(currentArea.getId()));
                    }
                    if (SmartHouse.getInstance().getCurrentState()!=null) {
                        checkConfiguration(SmartHouse.getAreaById(areaId));
                    }
                } else if (resultType.equals(ControlMonitorService.CAMERA)){
                    int areaId = intent.getIntExtra(AREA_ID,-1);
                    Log.d(TAG,areaId+ " areadID");
                    checkConfiguration(SmartHouse.getAreaById(areaId));
                    if ( currentArea!=null && areaId == currentArea.getId()) {
                        SmartHouse house = SmartHouse.getInstance();
                        Bitmap bmImg = house.getBitmapByAreaId(areaId);
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
                            detect(mBitmap);
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

    private void checkConfiguration(AreaEntity area) {
        Log.d(TAG, "Chck config");
        SmartHouse house = SmartHouse.getInstance();
        for (EventEntity ev : SmartHouse.getInstance().getCurrentState().getEvents()){
            Log.d(TAG, ev.getAreaId()+" "+ev.getSenName()+" "+ev.getSenValue() );
            if (ev.getAreaId() == area.getId()){
                if (ev.getSenName().equals(AreaEntity.attrivutesValues[0]) &&
                        ev.getSenValue().equals(area.getSafety())){
                    Log.d(TAG, ev.getNextStateId()+" " );
                    changeState(ev);
                } else if (ev.getSenName().equals(AreaEntity.attrivutesValues[1]) &&
                        ev.getSenValue().equals(area.getLight())){
                    Log.d(TAG, ev.getNextStateId()+" " );
                    changeState(ev);
                }else if (ev.getSenName().equals(AreaEntity.attrivutesValues[2]) &&
                        ev.getSenValue().equals(area.getTemperature())){
                    Log.d(TAG, ev.getNextStateId()+" " );
                    changeState(ev);
                } else if (ev.getSenName().equals(AreaEntity.attrivutesValues[3]) &&
                        area.getRawDetect().contains(ev.getSenValue())){
                    Log.d(TAG, ev.getNextStateId()+" " );
                    changeState(ev);
                }
            }
        }
    }
    private void changeState(EventEntity ev){
        SmartHouse house = SmartHouse.getInstance();
        house.setCurrentState(SmartHouse.getInstance().getStateById(ev.getNextStateId()));
        house.setStateChangedTime((new Date()).getTime());
        showAlertState(house.getCurrentState());
        showReply(BotUtils.completeSentence(SmartHouse.getInstance().getCurrentState().getNoticePattern(),"",""));

    }

    private void showAlertState(StateEntity state) {
        if (stateDialog == null) {

            AlertDialog.Builder builer = new AlertDialog.Builder(this);

            builer.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SmartHouse.getInstance().resetStateToDefault();
                    startListening();
                    dialog.dismiss();
                }
            });
            builer.setPositiveButton("Kích hoạt NGAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SmartHouse.getInstance().startConfigCmds();
                    startListening();
                    dialog.dismiss();
                }
            });
            stateDialog = builer.create();
        }
        if (stateDialog.isShowing()){
            stateDialog.dismiss();
        }
        stateDialog.setTitle(state.getName());
        stateDialog.setMessage(state.getNoticePattern()+"\n"+"Tự động kích hoat trong "+state.getDelaySec()+" giây");
        stateDialog.setCancelable(false);
        stateDialog.show();
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, UserSettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
        promptSpeechInput();
    }

    @Override
    public void onInitFinish() {
        waitDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    CurrentContext.getInstance().setDevice(device);
                    CurrentContext.getInstance().setScript(null);
                    if (device.getState().equals("on")) {
                        CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_OFF));
                        SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(), "off"));
                        waitDialog.show();
                    } else if (device.getState().equals("off")) {
                        CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_ON));
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
            CurrentContext.getInstance().setDevice(device);
            CurrentContext.getInstance().setScript(null);
            if (device.getState().equals("on")){
                CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_OFF));
                SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"off"));
                waitDialog.show();
            } else {
                CurrentContext.getInstance().setDetectedFunction(DetectIntentSQLite.findFunctionById(ConstManager.FUNCTION_TURN_ON));
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
            promptSpeechInput();
        }
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
                        setDetectMessage(AreaEntity.DETECT_NOT_AVAILABLE);
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
                Boolean hasAqua = false;
                int stranger = 0;
                for (IdentifyResult identifyResult: result) {
                    if (identifyResult.candidates.size() > 0) {
                        if (identifyResult.candidates.get(0).confidence > 0.65) {
                            String personId = identifyResult.candidates.get(0).personId.toString();
                            String personName = StorageHelper.getPersonName(
                                    personId, mPersonGroupId, ControlPanel.this);

                            message += personName+" ";
                            hasAqua = true;
                        } else {
                            stranger++;
                        }
                    } else {
                        stranger++;
                    }
                }
//                if (hasAqua) {
//                    message += " trở lại quán ạ";
//                }
                if (!hasAqua) {
                    message = AreaEntity.DETECT_STRANGE;
                }

                showReply(message);
                setDetectMessage(message);

            }
        }

    }

    public void clicktoControlPanel(View v){
    }

    private void setDetectMessage(String info) {
        currentArea = SmartHouse.getAreaById(currentArea.getId());
        currentArea.setDetect(info);
        SmartHouse.getInstance().updateAreaById(currentArea.getId(),currentArea);
        TextView txtResult = (TextView) cameraDialog.findViewById(R.id.txtFaceResult);
        txtResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
            }
        });
        txtResult.setText(currentArea.getDetect());
    }

    private void showReply(String sentenceReply){

        if (sentenceReply.contains("ác nhậ")){
            CurrentContext.getInstance().stopWaitOwner();
            stopListening();
            Log.d(TAG,"có xác nhận");
        } else if (CurrentContext.getInstance().getDetectSocial()!= null &&
                (CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.SAY_BYE ||
                CurrentContext.getInstance().getDetectSocial().getId() == ConstManager.NOT_UNDERSTD)) {
            startListening();
            CurrentContext.getInstance().stopWaitOwner();
            Log.d(TAG,"say bye");
        }
        VoiceUtils.speak(sentenceReply);
        Toast.makeText(this, sentenceReply + CurrentContext.getInstance().isWaitingOwnerSpeak(), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
//        waitDialog.show();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
//            waitDialog.dismiss();
        }
        stopService(new Intent(this,ControlMonitorService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopListening();
        Log.d(TAG,"on páuse");
    }

    @Override
    protected void onStart() {
        super.onStart();
        restartListeningService();
        Log.d(TAG,"on start");
    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        try {
            startActivityForResult(intent, VAPanel.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this,"Không hiểu được nói gì",Toast.LENGTH_SHORT).show();
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
                    Log.d(TAG,result.get(0));
                    showReply(BotUtils.botReplyToSentence(result.get(0)));
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

        Log.d(TAG, "processVoiceCommands: "+ voiceCommands[0].toString());
        if (voiceCommands[0].contains(" ơi")) {
            stopListening();
            CurrentContext.getInstance().waitOwner();
            CurrentContext.getInstance().setDetectSocial(null);
            showReply(BotUtils.completeSentence("Dạ "+BotUtils.BOT_ROLE+" nghe","",""));
        }else {
            restartListeningService();
        }
        Toast.makeText(this, voiceCommands[0], Toast.LENGTH_SHORT).show();
    }

}
