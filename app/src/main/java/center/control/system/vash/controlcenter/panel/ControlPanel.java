package center.control.system.vash.controlcenter.panel;

import android.app.Activity;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import center.control.system.vash.controlcenter.App;
import center.control.system.vash.controlcenter.MainActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaAdapter;
import center.control.system.vash.controlcenter.area.AreaAttribute;
import center.control.system.vash.controlcenter.area.AreaAttributeAdapter;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.configuration.CommandEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.DeviceAdapter;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.ManageDeviceActivity;
import center.control.system.vash.controlcenter.helper.StorageHelper;
import center.control.system.vash.controlcenter.nlp.ChatAdapter;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.nlp.VoiceUtils;
import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.recognition.ImageHelper;

import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.VolleySingleton;
import center.control.system.vash.controlcenter.service.ControlMonitorService;
import center.control.system.vash.controlcenter.service.WebServerService;
import center.control.system.vash.controlcenter.utils.BotUtils;
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
    private FaceServiceClient fsClient;
    private boolean detecting;
    private String mPersonGroupId;
    private ProgressDialog waitDialog;

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
        systemId = sharedPreferences.getString(ConstManager.SYSTEM_ID,"");
        Log.i(TAG,"system Id " + systemId);
        if (systemId.equals("")){
            startActivity(new Intent(this,MainActivity.class));
        }
        SmartHouse house = SmartHouse.getInstance();
        if (house.getAreas().size()>0) {
            currentArea = house.getAreas().get(0);
            currentAttrib = AreaEntity.attrivutesValues[0];
            startService(new Intent(this, ControlMonitorService.class));
            String botRole = sharedPreferences.getString(ConstManager.BOT_ROLE,"");
            String botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
            String ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");
            String ownerRole = sharedPreferences.getString(ConstManager.OWNER_ROLE,"ông");
            SmartHouse.getInstance().setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
//            showReply(BotUtils.completeSentence("Xin chào <owner-name>, quán cà phê thông minh xin được phục vụ","",""));

        } else {
            startActivity(new Intent(this,ManageDeviceActivity.class));
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);

        final ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnHome);
        currentTab.setImageResource(R.drawable.tab_home_active);
        currentTab.setBackgroundResource(R.drawable.background_tab_home_active);

        Intent webService = new Intent(ControlPanel.this, WebServerService.class);
        startService(webService);

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
                Toast.makeText(ControlPanel.this, resultType, Toast.LENGTH_SHORT).show();
                if (resultType.equals(WebServerService.SERVER_SUCCESS)) {
                    noticBuilder.setContentText(resultType);
                    noticBuilder.setContentTitle("Server stated port "+8080);
                    NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    man.notify(0,noticBuilder.build());

                } else if (resultType.equals(ControlMonitorService.DEACTIVATE)){
                    SharedPreferences sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    SmartHouse.getInstance().removeAllAreaAndItsDevice();
                    edit.putString(ConstManager.SYSTEM_ID,"");
                    edit.commit();
                    SQLiteManager.getInstance().clearAllData();
                    startActivity(new Intent(ControlPanel.this, MainActivity.class));
                } else if (resultType.equals(ControlMonitorService.WAIT)){
                    waitDialog.show();
                } else if (resultType.equals(ControlMonitorService.CONTROL)){
                    waitDialog.dismiss();
                    int result = intent.getIntExtra(AREA_ID, -1);
                    CurrentContext current = CurrentContext.getInstance();
                    if (result == ControlMonitorService.SUCCESS){
                        showReply(BotUtils.completeSentence(
                                current.getDetectedFunction().getSuccessPattern(),"",current.getDevice().getName()));
                        SmartHouse house = SmartHouse.getInstance();
                        deviceAdapter.updateHouseDevice(house.getDevicesInAreaAttribute(currentArea.getId(),currentAttrib));
                    } else if (result == ControlMonitorService.FAIL){
                        showReply(BotUtils.completeSentence(
                                current.getDetectedFunction().getFailPattern(),"",current.getDevice().getName()));
                    }
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

                        if (bmImg!=null){
                            imgFace.setImageBitmap(bmImg);

                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir =  Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES);
                            myDir.mkdirs();
                            String nameFile = "testSelf.jpg";
                            File file = new File(myDir, nameFile);
                            if (file.exists ()) file.delete();
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                Log.d(TAG,file.getAbsolutePath());
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
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(),WebServerService.class));
        Log.d(TAG," destroy ");
    }


    @Override
    public void onAreaClick(AreaEntity area) {
        currentArea = area;
        areaAttributeAdapter.updateAttribute(area.generateValueArr(),area.getId());
        Log.d(TAG," click id "+ area.getId());
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

            final ImageButton btnOnOff = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteOnOff);
            final TextView txtOnOff= (TextView) remoteDialog.findViewById(R.id.txtRemoteOnOff);
            btnOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtOnOff.getText().toString().toUpperCase().equals("TẮT")) {
                        SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(), "off"));
                        waitDialog.show();
                        txtOnOff.setText("BẬT");
                        txtOnOff.setTextColor(getResources().getColor(R.color.nGreen1));
                        btnOnOff.setColorFilter(getResources().getColor(R.color.nGreen1));
                    } else {
                        SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(), "on"));
                        waitDialog.show();
                        txtOnOff.setText("TẮT");
                        txtOnOff.setTextColor(Color.parseColor("#D52B2B"));
                        btnOnOff.setColorFilter(Color.TRANSPARENT);
                    }
                }
            });
            ImageButton btnInc = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteInc);
            btnInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"inc"));
                    waitDialog.show();
                }
            });
            ImageButton btnDec = (ImageButton) remoteDialog.findViewById(R.id.btnRemoteDec);
            btnDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartHouse.getInstance().addCommand(new CommandEntity(device.getId(),"dec"));
                    waitDialog.show();
                }
            });
            remoteDialog.show();
        } else {
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
                    setInfo("No faces detected!");
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
                        setInfo("Please select an image and create a person group first.");
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

                String message = "Dù Vàng xin chào ";
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
                if (hasAqua) {
                    message += " trở lại quán ạ";
                } if (!hasAqua) {
                    message = "Dù Vàng xin chào anh chị. Có " + stranger + " khách đến quán ạ";
                }

                showReply(message);
                setInfo(message);

            }
        }

    }


    private void setInfo(String info) {
        TextView txtResult = (TextView) cameraDialog.findViewById(R.id.txtFaceResult);
        txtResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
            }
        });
        txtResult.setText(info);
    }

    private  void showReply(String sentenceReply){
        VoiceUtils.speak(sentenceReply);
        Log.d(TAG, "showReply: ----------------" + sentenceReply);
    }
    private void processFunction(String humanSay, DetectFunctionEntity functionIntent){
        List<TargetTernEntity> termTargets = TermSQLite.getTargetInSentence(humanSay);
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(functionIntent.getFunctionName())){
            ScriptEntity mode = BotUtils.findBestScript(termTargets);
            if (mode != null) {
                BotUtils.implementCommand(functionIntent,null,mode);
            } else {
                DetectSocialEntity askWhichMode = BotUtils.getSocialByName(ConstManager.SOCIAL_ASK_MODE);

                CurrentContext current = CurrentContext.getInstance();
                current.setDetectedFunction(functionIntent);

                String replyComplete = BotUtils.completeSentence(askWhichMode.getQuestionPattern(),
                        BotUtils.getVerbByIntent(functionIntent.getFunctionName()), "");
                showReply(replyComplete);
            }
        } else if (ConstManager.FUNCTION_FOR_DEVICE.contains(functionIntent.getFunctionName())){
            AreaEntity area = BotUtils.findBestArea(termTargets);
            String target;
            if (area != null) {
                DeviceEntity device = BotUtils.findBestDevice(termTargets,area.getId());
                if (device == null) {
                    Log.d(TAG, "Tim thấy không gian " + area.getName() + " mà không tìm thấy thiết bị");
                    DetectSocialEntity askWhichDevice = BotUtils.getSocialByName(ConstManager.SOCIAL_ASK_DEVICEAREA);
                    CurrentContext current = CurrentContext.getInstance();
                    current.setDetectedFunction(functionIntent);
                    current.setArea(area);

                    target = area.getName();
                    String replyComplete = BotUtils.completeSentence(askWhichDevice.getQuestionPattern(),
                            BotUtils.getVerbByIntent(functionIntent.getFunctionName()), target);
                    showReply(replyComplete);
                } else {
                    Log.d(TAG, "Tìm thấy cả hai" + device.getName()+area.getName());
                    BotUtils.implementCommand(functionIntent,device,null);
                }
            } else {
                DeviceEntity deviceOnly = BotUtils.findBestDevice(termTargets, -1);
                if (deviceOnly != null) {
                    Log.d(TAG, "Tim thấy thiet bị ,mà không co không gian " + deviceOnly.getName());

                    CurrentContext current = CurrentContext.getInstance();
                    current.setDetectedFunction(functionIntent);
                    current.setDevice(deviceOnly);
                    current.setSentence(humanSay);

                    area = AreaSQLite.findById(deviceOnly.getAreaId());
                    target = deviceOnly.getName()+" trong "+area.getName();
                    String replyComplete = BotUtils.completeSentence(functionIntent.getRemindPattern(), "", target);
                    showReply(replyComplete);
                } else {
                    CurrentContext current = CurrentContext.getInstance();
                    if (current.getDevice() != null){
                        BotUtils.implementCommand(functionIntent,current.getDevice(),null);
                    } else {
                        String verb = BotUtils.getVerbByIntent(functionIntent.getFunctionName());
                        DetectSocialEntity askDevice = BotUtils.getSocialByName(ConstManager.SOCIAL_ASK_DEVICEONLY);
                        String replyComplete = BotUtils.completeSentence(askDevice.getQuestionPattern(), verb, "");
                        showReply(replyComplete);
                    }
                }
            }
        } else if (functionIntent.getFunctionName().contains("check")){
            Log.d(TAG,"check trang thai phong");
            AreaEntity area = BotUtils.findBestArea(termTargets);
            if (area != null) {
                Log.d(TAG,"Thay phong "+area.getName());
                String resultVal = BotUtils.getAttributeByFunction(functionIntent.getFunctionName(),area);
                String replyComplete ="";
                if (resultVal == null){
                    replyComplete=BotUtils.completeSentence(functionIntent.getFailPattern(), resultVal, area.getName());
                } else {
                    replyComplete=BotUtils.completeSentence(functionIntent.getSuccessPattern(), resultVal, area.getName());
                }
                showReply(replyComplete);
                //
            } else {
                Log.d(TAG,"Khong tim thay phong");
                CurrentContext current = CurrentContext.getInstance();
                current.setDetectedFunction(functionIntent);
                current.setSentence(humanSay);

                String replyComplete = BotUtils.completeSentence(functionIntent.getRemindPattern(),"","");
                showReply(replyComplete);
            }
        }
    }
    private void processSocial(String humanSay, DetectSocialEntity socialIntent){
        CurrentContext current = CurrentContext.getInstance();
//        current.setDetectSocial(socialIntent);
//        showReply("Mục đích "+socialIntent.getName());
        String replyComplete;
        String result = "";
        if (socialIntent.getName().equals(ConstManager.SOCIAL_WHAT_TIME)){
            result = BotUtils.getTime();
        } else if (socialIntent.getName().equals(ConstManager.SOCIAL_WHAT_DAY)){
            result = BotUtils.getDay();
        } else if (socialIntent.getName().equals(ConstManager.SOCIAL_WHAT_SEX)){
            result = "gái";
        } else if (socialIntent.getName().equals(ConstManager.SOCIAL_AGREE)){
            if (current.getDetectedFunction() != null){
                processFunction(current.getSentence(),current.getDetectedFunction());
            }
        }
        replyComplete = BotUtils.completeSentence(socialIntent.getReplyPattern(), result, "");
        showReply(replyComplete);
    }

    private void botReplyToSentence(String humanSay){

        TermSQLite termSQLite = new TermSQLite();
        List<TermEntity> terms = termSQLite.getHumanIntentInSentence(humanSay);
        DetectFunctionEntity functFound = BotUtils.findBestFunctDetected(terms);
        DetectSocialEntity socialFound = BotUtils.findBestSocialDetected(terms);
        if (functFound != null && socialFound != null) {
            if (functFound.getDetectScore()> socialFound.getDetectScore()){
                processFunction(humanSay,functFound);
            } else {
                processSocial(humanSay,socialFound);
            }
        } else if (functFound == null && socialFound != null) {
            processSocial(humanSay, socialFound);
        }else if (functFound != null && socialFound == null){
            processFunction(humanSay, functFound);
        } else {
            DetectSocialEntity notUnderReply = BotUtils.getSocialByName(ConstManager.NOT_UNDERSTD);
            String replyComplete = BotUtils.completeSentence(notUnderReply.getQuestionPattern(), "", "");
            showReply(replyComplete);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this,ControlMonitorService.class));

    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        try {
            startActivityForResult(intent, VAPanel.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this,"Fail to nhận diện giọng nói",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG,requestCode+"--" );
        switch (requestCode) {
            case VAPanel.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1 && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d(TAG,result.get(0));
                    botReplyToSentence(result.get(0));
                }
                break;
            }

        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    promptSpeechInput();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


}
