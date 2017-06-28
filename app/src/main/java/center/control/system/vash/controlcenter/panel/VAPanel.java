package center.control.system.vash.controlcenter.panel;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.nlp.ChatAdapter;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;

public class VAPanel extends AppCompatActivity {
    private static final String TAG = "VAPanel em đây";
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private ListView chatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapanel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnVA);
        currentTab.setImageResource(R.drawable.tab_voice_active);
        currentTab.setBackgroundColor(Color.WHITE);

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
        List<TargetTernEntity> termTargets = TermSQLite.getTargetInSentence(humanSay);
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(functionIntent.getFunctionName())){

            ScriptEntity mode = BotUtils.findBestScript(termTargets);
            if (mode != null) {
                current.setScript(mode);
            } else {
                Log.d(TAG, "Khong tim thay mode " + humanSay);
            }
        } else {
            AreaEntity area = BotUtils.findBestArea(termTargets);
            if (area != null) {
                current.setArea(area);
            } else {
                Log.d(TAG, "Khong tim thay area " + humanSay);
            }
            DeviceEntity device = BotUtils.findBestDevice(termTargets);
            if (area != null) {
                current.setDevice(device);
            } else {
                Log.d(TAG, "Khong tim thay device " + humanSay);
            }
        }
//        RetroArduinoSigleton retroArduinoSigleton = RetroArduinoSigleton.getInstance();
//        HouseConfig house = HouseConfig.getInstance();
//        ConnectedDevice currDevice = house.getDeviceByPort(device.getPort());
//        current.setDeviceTarget(currDevice );
//        Log.d(TAG, result.getFunctionName() + "  : function tim thay  " + currDevice.getName()+" : " +currDevice.getState());
//        switch (result.getFunctionName()) {
//            case IntentConstant.TURN_OBJECT_ON:
//                retroArduinoSigleton.turnObjectOn(device, MainActivity.this);
//                break;
//            case IntentConstant.TURN_OBJECT_OFF:
//                if (device.getPort().equals(DeviceConstant.ALERT_BELL_PORT)){
//                    house.setGuestIsAquaintance(true);
//                    house.setDetectThiefMoment(null);
//                }
//                retroArduinoSigleton.turnObjectOff(device, MainActivity.this);
//                break;
//        }
    }
    private void processSocial(String humanSay, DetectSocialEntity socialIntent){
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectSocial(socialIntent);

        String replyComplete = BotUtils.completeSentence(socialIntent.getReplyPattern(), "", "");
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
            String replyComplete = BotUtils.completeSentence(notUnderReply.getReplyPattern(), "", "");
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
        startActivity(new Intent(this, VAPanel.class));
    }
}
