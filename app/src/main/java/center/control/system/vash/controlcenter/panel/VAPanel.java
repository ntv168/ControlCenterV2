package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.configuration.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.ChatAdapter;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.nlp.VoiceUtils;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.server.AssistantTypeDTO;
import center.control.system.vash.controlcenter.server.BotDataCentralDTO;
import center.control.system.vash.controlcenter.server.CloudApi;
import center.control.system.vash.controlcenter.server.FunctionIntentDTO;
import center.control.system.vash.controlcenter.server.RetroFitSingleton;
import center.control.system.vash.controlcenter.server.SocialIntentDTO;
import center.control.system.vash.controlcenter.server.TermDTO;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;
import center.control.system.vash.controlcenter.utils.TFIDF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VAPanel extends AppCompatActivity {
    private static final String TAG = "VAPanel em đây";
    public static final int REQ_CODE_SPEECH_INPUT = 111;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private ListView chatList;
    private List<AssistantTypeDTO> typeDTOs;
    private AlertDialog.Builder editNickNameDiag;
    private SharedPreferences sharedPreferences;
    private int botTypeId;
    private String botType;
    private String botName;
    private String ownerName;
    private String botRole;
    private String ownerRole;
    private ArrayAdapter<String> botRoleAdapter;
    private EditText editOwnerName;
    private ArrayAdapter<String> ownerRoleAdapter;
    private AlertDialog.Builder botTypeDiag;
    private ArrayAdapter<String> botTypeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapanel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnVA);
        currentTab.setImageResource(R.drawable.tab_voice_active);
        currentTab.setBackgroundColor(Color.WHITE);

        final CloudApi botApi = RetroFitSingleton.getInstance().getCloudApi();
        sharedPreferences = getSharedPreferences(ConstManager.SHARED_PREF_NAME,MODE_PRIVATE);
        botType = sharedPreferences.getString(ConstManager.BOT_TYPE,"");
        botRole = sharedPreferences.getString(ConstManager.BOT_ROLE,"");
        if (botType.length() < 2){
            Log.d(TAG,botType + "  loai");
            Toast.makeText(this,"Chưa chọn loại quản gia",Toast.LENGTH_SHORT);
        }
        botName = sharedPreferences.getString(ConstManager.BOT_NAME,"");
        if (botName.equals("")){
            Log.d(TAG,botName + "  ten");
            Toast.makeText(this,"Chưa Tên quản gia",Toast.LENGTH_SHORT);
        }
        botTypeId = sharedPreferences.getInt(ConstManager.BOT_TYPE_ID,-1);
        ownerName = sharedPreferences.getString(ConstManager.OWNER_NAME,"");
        ownerRole = sharedPreferences.getString(ConstManager.OWNER_ROLE,"ông");
        SmartHouse.getInstance().setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
        btnSend = (ImageButton) findViewById(R.id.btnChatSend);
        Button btnSpeak = (Button) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        chatList = (ListView) findViewById(R.id.lstMsgChat);

        chatAdapter = new ChatAdapter(this, R.layout.msg_right);
        chatList.setAdapter(chatAdapter);

        final EditText chatText = (EditText) findViewById(R.id.txtHumanChat);

        chatText.setText("");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                chatAdapter.add(new ChatAdapter.ViewHolder(false, chatText.getText().toString()));
                botReplyToSentence(chatText.getText().toString());
                chatText.setText("");
            }
        });

        chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.setAdapter(chatAdapter);

        //to scroll the list view to bottom on data change
        chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatList.setSelection(chatAdapter.getCount() - 1);
            }
        });
        final ProgressDialog waitDialog = new ProgressDialog(VAPanel.this);
        waitDialog.setTitle("Vui lòng đợi");
        waitDialog.setIndeterminate(true);
//        waitDialog.setCancelable(false);
        editNickNameDiag = new AlertDialog.Builder(VAPanel.this);
        editOwnerName = (EditText) findViewById(R.id.txtOwnerName);
        editOwnerName.setText(ownerName);
        final Spinner spinOwnerRole = (Spinner) findViewById(R.id.spn_owner_role);
        final Spinner spinBotRole = (Spinner) findViewById(R.id.spn_bot_role);
        ((TextView)findViewById(R.id.txtBotName)).setText(botName);
        ((TextView)findViewById(R.id.txtBotType)).setText(botType);
        refineNickNameTarget();

        if (botType.equals(ConstManager.BOT_TYPE_QUAN_GIA_GIA)){
            botRoleAdapter = new ArrayAdapter<String>(VAPanel.this, android.R.layout.simple_spinner_dropdown_item,
                    ConstManager.QUAN_GIA_GIA_BOT_ROLE_ARR);
            spinBotRole.setAdapter(botRoleAdapter);
            ownerRoleAdapter = new ArrayAdapter<String>(VAPanel.this, android.R.layout.simple_spinner_dropdown_item,
                    ConstManager.QUAN_GIA_GIA_OWNER_ROLE_ARR);
            spinOwnerRole.setAdapter(ownerRoleAdapter);
            spinOwnerRole.setSelection(ownerRoleAdapter.getPosition("ông"));
            spinBotRole.setSelection(ownerRoleAdapter.getPosition("tôi"));
        } else {
            Log.d(TAG,"bot tye chua ho tro : "+ botType);
            //truong hop bot type khac
        }

        Button btnLoadBot = (Button)findViewById(R.id.btnLoadBot);
        btnLoadBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botRole = botRoleAdapter.getItem(spinBotRole.getSelectedItemPosition());
                ownerName = editOwnerName.getText().toString();
                ownerRole = ownerRoleAdapter.getItem(spinOwnerRole.getSelectedItemPosition());

                SmartHouse house  = SmartHouse.getInstance();
                house.setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
                botApi.getDataVA(botTypeId).enqueue(new Callback<BotDataCentralDTO>() {
                    @Override
                    public void onResponse(Call<BotDataCentralDTO> call, Response<BotDataCentralDTO> response) {
                        Log.d(TAG,call.request().url()+"");
                        TermSQLite sqLite = new TermSQLite();
                        DetectIntentSQLite sqlDect = new DetectIntentSQLite();
                        sqLite.clearAll();
                        sqlDect.clearAll();
                        Log.d(TAG,"term ;"+response.body().getTermDTOS().size());
                        Log.d(TAG,"funct ;"+response.body().getFunctions().size());
                        Log.d(TAG,"soc ;"+response.body().getSocials().size());
                        for (TermDTO term : response.body().getTermDTOS()){

                                sqLite.insertHumanTerm(new TermEntity(term.getVal()
                                        , term.getTfidf(), term.getSocialId(), term.getFunctId()));
                        }
                        for (SocialIntentDTO soc : response.body().getSocials()){
                            sqlDect.insertSocial(new DetectSocialEntity(soc.getId(),
                                    soc.getName(),soc.getQuestion(),soc.getReply()));
                        }
                        for (FunctionIntentDTO funct : response.body().getFunctions()){
                            sqlDect.insertFunction(new DetectFunctionEntity(funct.getId(),
                                    funct.getName(),funct.getSuccess(),funct.getFail(),funct.getRemind()));
                        }
                        SmartHouse house = SmartHouse.getInstance();
                        BotUtils bot = new BotUtils();
                        bot.saveDeviceTFIDFTerm(house.getDevices());
                        bot.saveAreaTFIDFTerm(house.getAreas());
                        bot.saveScriptTFIDFTerm(house.getScripts());
                        waitDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<BotDataCentralDTO> call, Throwable t) {
                        Log.d(TAG,"down load bot data failed");
                        waitDialog.dismiss();
                    }
                });
                waitDialog.show();
            }
        });
        if (botTypeId == -1){
            btnLoadBot.setEnabled(false);
        }
    }


    //    private void botReplyCommandByResult(String commandResult, String resultValue){
//        CurrentContext current = CurrentContext.getInstance();
//        DetectIntent currentDetect = current.getDetected();
//        ConnectedDevice currentTarget = current.getDeviceTarget();
//        String replyComplete;
//        IntentLearned reply = null;
//        Log.d(TAG, commandResult + " ---  " + resultValue);
//        if (commandResult.equals(IntentConstant.SUCCESS_REPLY)) {
//            HouseConfig houseConfig = HouseConfig.getInstance();
//            if (currentTarget.getPort().equals(DeviceConstant.ALERT_BELL_PORT)){
//                houseConfig.setGuestIsAquaintance(true);
//                houseConfig.setDetectThiefMoment(null);
//            }
//
//            reply = BotUtils.getIntentById(currentDetect.getSuccessReplyId());
//            if (currentTarget.getType().equals(DeviceConstant.DEVICE_TYPE)) {
//                houseConfig.changeStateByPort(currentTarget.getPort(),resultValue);
//            } else if (currentTarget.getType().equals(DeviceConstant.SENSOR_TYPE)){
//                houseConfig.changeValueByPort(currentTarget.getPort(),resultValue);
//            }
//
//            ConnectedDevice device = houseConfig.getDeviceByPort(currentTarget.getPort());
//            Log.d(TAG, "  : xong tim thay  " + device.getName()+" : " +device.getState());
//            sensorAdapter.notifyDataSetChanged();
//        } else if  (commandResult.equals(IntentConstant.FAIL_REPLY)) {
//            reply= BotUtils.getIntentById(currentDetect.getFailReplyId());
//        }
//        replyComplete = BotUtils.completeSentence(reply.getSentence(), resultValue, currentTarget.getName());
//        showReply(replyComplete);
////        launchCheckSensorService();
//    }
    private void processFunction(String humanSay, DetectFunctionEntity functionIntent){
        List<TargetTernEntity> termTargets = TermSQLite.getTargetInSentence(humanSay);
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(functionIntent.getFunctionName())){
            ScriptEntity mode = BotUtils.findBestScript(termTargets);
            if (mode != null) {
//                BotUtils.implementCommand(functionIntent,null,mode);
                CurrentContext.getInstance().renew();
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
//                    BotUtils.implementCommand(functionIntent,device,null);
                    CurrentContext.getInstance().renew();
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
//                        BotUtils.implementCommand(functionIntent,current.getDevice(),null);
                        current.renew();
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

    private void showReply(String replyComplete) {
        VoiceUtils.getInstance().speak(replyComplete);
        chatAdapter.add(new ChatAdapter.ViewHolder(true, replyComplete));
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
    }
    private void refineNickNameTarget(){
        final SmartHouse house = SmartHouse.getInstance();
        for (final DeviceEntity device: house.getDevices()){
            if (device.getNickName() == null || device.getNickName().equals("")){
                editNickNameDiag.setTitle("Tên gọi khác cho thiết bị"+device.getName());
                final EditText input = new EditText(VAPanel.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                editNickNameDiag.setView(input);
                editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        device.setNickName(input.getText().toString());
                        DeviceSQLite.upById(device.getId(),device);
                        house.updateDeviceById(device.getId(),device);
                        dialog.dismiss();
                    }
                });
                editNickNameDiag.show();
            }
        }
        for (final AreaEntity area: house.getAreas()){
            if (area.getNickName().length()<2){
                editNickNameDiag.setTitle("Tên gọi khác cho không gian "+area.getName());
                final EditText input = new EditText(VAPanel.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                editNickNameDiag.setView(input);
                editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        area.setNickName(input.getText().toString());
                        AreaSQLite.upAddressAndNickById(area.getId(),area);
                        house.updateAreaById(area.getId(),area);
                        dialog.dismiss();
                    }
                });
                editNickNameDiag.show();
            }
        }
        for (final ScriptEntity mode: house.getScripts()){
            if (mode.getNickName().length()<2){
                editNickNameDiag.setTitle("Tên gọi khác cho chế độ "+mode.getName());
                final EditText input = new EditText(VAPanel.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                editNickNameDiag.setView(input);
                editNickNameDiag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mode.setNickName(input.getText().toString());
                        ScriptSQLite.upModeOnly(mode.getId(),mode);
                        house.updateModeById(mode.getId(),mode);
                        dialog.dismiss();
                    }
                });
                editNickNameDiag.show();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(ConstManager.BOT_ROLE,botRole);
        edit.putString(ConstManager.OWNER_ROLE,ownerRole);
        edit.putString(ConstManager.OWNER_NAME,ownerName);
        edit.putInt(ConstManager.BOT_TYPE_ID,botTypeId);
        edit.putString(ConstManager.BOT_TYPE,botType);
        edit.putString(ConstManager.BOT_NAME,botName);
        edit.commit();
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this,"Fail to nhận diện giọng nói",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG,requestCode+"--" );
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1 && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d(TAG,result.get(0));
                    chatAdapter.add(new ChatAdapter.ViewHolder(false, result.get(0)));
                    botReplyToSentence(result.get(0));
                }
                break;
            }

        }
    }
}
