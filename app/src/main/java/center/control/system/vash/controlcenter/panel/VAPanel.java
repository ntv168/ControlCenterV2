package center.control.system.vash.controlcenter.panel;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import center.control.system.vash.controlcenter.PersonalInfoActivity;
import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.nlp.ChatAdapter;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.voice.VoiceUtils;
import center.control.system.vash.controlcenter.utils.BotUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;

public class VAPanel extends AppCompatActivity {
    private static final String TAG = "VAPanel em đây";
    public static final int REQ_CODE_SPEECH_INPUT = 111;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private ListView chatList;
    private String ownerRole;

    @Override
    protected void onResume() {
        super.onResume();
        if (DetectIntentSQLite.findSocialByName(ConstManager.NOT_UNDERSTD) == null){
            startActivity(new Intent(this, PersonalInfoActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapanel);
        ImageButton currentTab = (ImageButton) findViewById(R.id.tabBtnVA);
        currentTab.setImageResource(R.drawable.tab_voice_active);
        currentTab.setBackgroundColor(Color.WHITE);

        btnSend = (ImageButton) findViewById(R.id.btnChatSend);
        ImageButton btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                chatAdapter.add(new ChatAdapter.ViewHolder(false, chatText.getText().toString()));
                showReply(BotUtils.botReplyToSentence(chatText.getText().toString()));
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


    private void showReply(String replyComplete) {
        VoiceUtils.getInstance().speak(replyComplete);
        chatAdapter.add(new ChatAdapter.ViewHolder(true, replyComplete));
    }


    public void clicktoControlPanel(View view) {
        startActivity(new Intent(this, ControlPanel.class));
    }

    public void clicktoModePanel(View view) {
        startActivity(new Intent(this, ModePanel.class));
    }

    public void clicktoSettingPanel(View view) {
        startActivity(new Intent(this, UserSettingPanel.class));
    }

    public void clicktoVAPanel(View view) {
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
                    showReply(BotUtils.botReplyToSentence(result.get(0)));
                }
                break;
            }

        }
    }
}
