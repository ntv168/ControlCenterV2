package center.control.system.vash.controlcenter.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Thuans on 6/6/2017.
 */

public class ConstManager {
    public static final String SERVER_HOST ="http://54.255.183.91:8080/";
    public static final String STAFF_CODE =  "staff.code";
    public static final String SHARED_PREF_NAME = "Sharef preference controlcenter";
    public static final String USERNAME = "key_username";
    public static final String BOT_TYPE = "bot type";
    public static final String BOT_NAME = "bot name";
    public static final String PASSWORD = "password";
    public static final String STATIC_ADDRESS = "staticAddress";
    public static final String CONTRACT_CODE = "contractCode";
    public static final String OWNER_NAME = "ownerName";
    public static final String OWNER_ADD = "ownerAddress";
    public static final String OWNER_TEL = "ownerTel";
    public static final String OWNER_CMND = "ownerCmnd";
    public static final String CONTRACT_ID = "contractId";
    public static final String ACTIVE_DAY = "activeDay";
    public static final String BOT_TYPE_QUAN_GIA_GIA = "Quản gia già";
    public static final String[] QUAN_GIA_GIA_BOT_ROLE_ARR = {"em", "tôi","cháu"};
    public static final String[] QUAN_GIA_GIA_OWNER_ROLE_ARR = {"ông","bà","ông chủ","bà chủ"};
    public static final String BOT_TYPE_ID = "bot.type.Id";
    public static final String BOT_ROLE = "bot.role";
    public static final String OWNER_ROLE = "owner.role";
    public static final String NOT_UNDERSTD = "notUnderstand";
    public static final String SOCIAL_AGREE = "agree";
    public static final String SOCIAL_DENY = "deny";
    public static final String SOCIAL_WHAT_TIME = "whatTime";
    public static final String SOCIAL_WHAT_DAY = "whatDay";
    public static final String SOCIAL_ASK_DEVICEAREA = "askAreaDevice";
    public static final String SOCIAL_ASK_DEVICEONLY = "askWhichDevice";
    public static final String SOCIAL_ASK_MODE = "askMode";
    public static final String SOCIAL_WHAT_SEX = "whatSex";
    public static final String FUNCTION_FOR_SCRIPT = "startMode,stopMode";
    public static final String FUNCTION_FOR_DEVICE = "turnObjectOn,turnObjectOff,increaseTemperature,decreaseTemperature";
    public static final String NOT_LEARN_YET = "notLearnYet";
    public static final String FUNCTION_TURN_ON = "turnObjectOn";
    public static final String FUNCTION_TURN_OFF = "turnObjectOff";
    public static final String FUNCTION_INC_TEMP = "increaseTemperature";
    public static final String FUNCTION_DEC_TEMP = "decreaseTemperature";
    public static final String FUNCTION_START_MODE = "startMode";
    public static final String FUNCTION_STOP_MODE = "stopMode";
    public static final int SERVICE_PERIOD = 3500;
    public static final int PRIORITY_MAX = 100;
    public static final int DURING_MAX = 1000;
    public static final int DEFAULT_STATE_ID = 1;

    public static String getTime(){
        Date time = new Date();
        String result = "";
        result += time.getHours()+" giờ "+time.getMinutes()+" phút ";
        return result;
    }
    public static String getDay(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String result = "";
        result += " ngày "+day+" tháng "+month+" ";
        return result;
    }
    public static String getVerbByIntent(String functionName) {
        switch (functionName){
            case FUNCTION_TURN_ON:
                return "bật";
            case FUNCTION_START_MODE:
                return "bật";
            case FUNCTION_TURN_OFF:
                return "tắt";
            case FUNCTION_STOP_MODE:
                return "tắt";
            case FUNCTION_INC_TEMP:
                return "tăng nhiệt độ";
            case FUNCTION_DEC_TEMP:
                return "giảm nhiệt độ";
        }
        return "";
    }
}
