package center.control.system.vash.controlcenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.configuration.EventEntity;
import center.control.system.vash.controlcenter.configuration.StateConfigurationSQL;
import center.control.system.vash.controlcenter.configuration.StateEntity;
import center.control.system.vash.controlcenter.recognition.Facedetect;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.database.SqLiteHelper;
import center.control.system.vash.controlcenter.nlp.VoiceUtils;
import center.control.system.vash.controlcenter.utils.ConstManager;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Thuans on 4/27/2017.
 */

public class App extends Application {
    private static Context context;
    private static SqLiteHelper dbHelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        VoiceUtils.initializeInstance(context);
        dbHelper = new SqLiteHelper();
        SQLiteManager.initializeInstance(dbHelper);
        initStateMachine();
        sFaceServiceClient = new FaceServiceRestClient(context.getString(R.string.endpoint), context.getString(R.string.subscription_key));
        SmartHouse.getInstance().setContractId(getSharedPreferences(ConstManager.SHARED_PREF_NAME,MODE_PRIVATE).getString(ConstManager.CONTRACT_ID,""));

        Log.d("App context","initiating .....");

    }

    private void initStateMachine() {
        StateConfigurationSQL.removeAll();

        EventEntity ev1 = new EventEntity();
        ev1.setId(1);
        ev1.setNextStateId(3);
        ev1.setPriority(ConstManager.PRIORITY_MAX);
        ev1.setSenName(AreaEntity.attrivutesValues[3]);
        ev1.setSenValue(AreaEntity.DETECT_STRANGE);
        StateConfigurationSQL.insertEvent(ev1);

        EventEntity ev2 = new EventEntity();
        ev2.setId(2);
        ev2.setNextStateId(2);
        ev2.setPriority(3);
        ev2.setSenName(AreaEntity.attrivutesValues[0]);
        ev2.setSenValue(AreaEntity.DOOR_OPEN);
        StateConfigurationSQL.insertEvent(ev2);

        EventEntity ev3 = new EventEntity();
        ev3.setId(3);
        ev3.setNextStateId(4);
        ev3.setPriority(0);
        ev3.setSenName(AreaEntity.attrivutesValues[3]);
        ev3.setSenValue(AreaEntity.DETECT_AQUAINTANCE);
        StateConfigurationSQL.insertEvent(ev3);

        EventEntity ev4 = new EventEntity();
        ev4.setId(4);
        ev4.setNextStateId(5);
        ev3.setPriority(0);
        ev4.setSenName(AreaEntity.attrivutesValues[0]);
        ev4.setSenValue(AreaEntity.DOOR_OPEN);
        StateConfigurationSQL.insertEvent(ev4);

        EventEntity ev5 = new EventEntity();
        ev5.setId(5);
        ev5.setNextStateId(1);
        ev3.setPriority(0);
        ev5.setSenName(AreaEntity.attrivutesValues[0]);
        ev5.setSenValue(AreaEntity.DOOR_CLOSE);
        StateConfigurationSQL.insertEvent(ev5);

        EventEntity ev6 = new EventEntity();
        ev6.setId(6);
        ev6.setNextStateId(6);
        ev6.setPriority(2);
        ev6.setSenName(AreaEntity.attrivutesValues[2]);
        ev6.setSenValue(AreaEntity.TEMP_WARM);
        StateConfigurationSQL.insertEvent(ev6);

        EventEntity ev7 = new EventEntity();
        ev7.setId(7);
        ev7.setNextStateId(1);
        ev7.setPriority(2);
        ev7.setSenName(AreaEntity.attrivutesValues[2]);
        ev7.setSenValue(AreaEntity.TEMP_COLD);
        StateConfigurationSQL.insertEvent(ev7);

        StateEntity stat = new StateEntity();
        stat.setName("Home Safe");
        stat.setId(1);
        stat.setNoticePattern("An toàn");
        stat.setDelaySec(0);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("2");
        StateConfigurationSQL.insertState(stat);
        SmartHouse.getInstance().setCurrentState(stat);

        stat = new StateEntity();
        stat.setName("Incomin");
        stat.setId(2);
        stat.setNoticePattern("Mở cửa vui lòng nhìn vào camera");
        stat.setDelaySec(0);
        stat.setDuringSec(5); //choose event highest priority
        stat.setNextEvIds("1,3");
        StateConfigurationSQL.insertState(stat); 

        stat = new StateEntity();
        stat.setName("Intrusion");
        stat.setId(3);
        stat.setNoticePattern("Có người lạ vào nhà");
        stat.setDelaySec(5);
        stat.setDuringSec(8);
        stat.setNextEvIds("3");
         StateConfigurationSQL.insertState(stat); 

        stat = new StateEntity();
        stat.setName("Aquain Come");
        stat.setId(4);
        stat.setNoticePattern("Xin chào <result-value>");
        stat.setDelaySec(0);
        stat.setDuringSec(10);
        stat.setNextEvIds("4");
         StateConfigurationSQL.insertState(stat); 

        stat = new StateEntity();
        stat.setName("Forget close door");
        stat.setId(5);
        stat.setNoticePattern("<owner-role> quên đóng cửa kìa");
        stat.setDelaySec(5);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("5");
         StateConfigurationSQL.insertState(stat);

        stat = new StateEntity();
        stat.setName("Room warm");
        stat.setId(6);
        stat.setNoticePattern("Phòng đang nóng <owner-role> muốn bật máy lạnh không");
        stat.setDelaySec(10);
        stat.setDuringSec(ConstManager.DURING_MAX);
        stat.setNextEvIds("7");
        StateConfigurationSQL.insertState(stat);
//        stat = new StateEntity();
//        stat.setName("Forget close door");
//        stat.setAvailable(true);
//        stat.setId(6);
//        stat.setConfigurationId(-1);
//        stat.setSocialIntentId(2060);
//        stat.setDelaySec(15);
//        StateConfigurationSQL.insertState(stat);
        SmartHouse.getInstance().setStates(StateConfigurationSQL.getAll());
    }

    public static FaceServiceClient getFaceServiceClient(){
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;

    public static Context getContext(){
        return context;
    }
}
