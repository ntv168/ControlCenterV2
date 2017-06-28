package center.control.system.vash.controlcenter.panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
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
        SmartHouse.getInstance().setBotOwnerNameRole(botName,botRole,ownerName,ownerRole);
        btnSend = (ImageButton) findViewById(R.id.btnChatSend);

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
                        saveDeviceTFIDFTerm(house.getDevices());
                        saveAreaTFIDFTerm(house.getAreas());
                        saveScriptTFIDFTerm(house.getScripts());
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
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectedFunction(functionIntent);
        showReply("Mục đích "+functionIntent.getFunctionName());
        List<TargetTernEntity> termTargets = TermSQLite.getTargetInSentence(humanSay);
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(functionIntent.getFunctionName())){
            ScriptEntity mode = BotUtils.findBestScript(termTargets);
            if (mode != null) {
                current.setScript(mode);
                showReply("Tim thấy chế độ "+mode.getName());
            } else {
                Log.d(TAG, "Khong tim thay mode " + humanSay);
            }
        } else {
            AreaEntity area = BotUtils.findBestArea(termTargets);
            if (area != null) {
                current.setArea(area);
                showReply("Tim thấy không gian "+area.getName());
            } else {
                Log.d(TAG, "Khong tim thay area " + humanSay);
            }
            DeviceEntity device = BotUtils.findBestDevice(termTargets);
            if (device != null) {
                current.setDevice(device);
                showReply("Tim thấy thiết bị "+device.getName());
            } else {
                Log.d(TAG, "Khong tim thay device " + humanSay);
            }
        }
    }
    private void processSocial(String humanSay, DetectSocialEntity socialIntent){
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectSocial(socialIntent);
        showReply("Mục đích "+socialIntent.getName());
        String replyComplete = "Câu này <bot-role> chưa được học. <owner-name> vui lòng đóng tiền";
        if (socialIntent.getReplyPattern() != null) {
            replyComplete = BotUtils.completeSentence(socialIntent.getReplyPattern(), "", "");
        } else {
            replyComplete = BotUtils.completeSentence(replyComplete, "", "");
        }
        showReply(replyComplete);
    }

    private void showReply(String replyComplete) {
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
            String replyComplete;
            if (notUnderReply.getReplyPattern() != null) {
                replyComplete  = BotUtils.completeSentence(notUnderReply.getReplyPattern(), "", "");
            }else {
                replyComplete = BotUtils.completeSentence("Câu này <bot-role> chưa được học. <owner-name> vui lòng đóng tiền", "", "");
            }
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
            if (device.getNickName().length()<2){
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
    private void saveDeviceTFIDFTerm(List<DeviceEntity> listDevices) {
        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listDevices);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int deviceId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                if (listDevices.size() >1) {
                    termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, deviceId);
                    Log.d(TAG, termTfidf + "   " + term + "   " + deviceId);
                }

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectDeviceId(deviceId);
                targetTerm.setDetectAreaId(-1);
                targetTerm.setDetectScriptId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
            }
        }
    }
    private void saveAreaTFIDFTerm(List<AreaEntity> listArea) {
        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listArea);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int areaId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                if (listArea.size() >1) {
                    termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, areaId);
                    Log.d(TAG,termTfidf+"   "+term+"   "+areaId);
                }

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectAreaId(areaId);
                targetTerm.setDetectDeviceId(-1);
                targetTerm.setDetectScriptId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
            }
        }
    }

    private void saveScriptTFIDFTerm(List<ScriptEntity> listScript) {
        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listScript);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int scriptId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                if (listScript.size() >1) {
                    termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, scriptId);
                    Log.d(TAG,termTfidf+" ");
                }

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectScriptId(scriptId);
                targetTerm.setDetectAreaId(-1);
                targetTerm.setDetectDeviceId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
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
}
