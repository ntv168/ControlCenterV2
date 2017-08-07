package center.control.system.vash.controlcenter.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Thuans on 6/6/2017.
 */

public class ConstManager {
    public static final String HOUSE_ID = "b1deb6fc-4ea9-4aac-9290-534044faa0ad";
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
    public static final int NOT_UNDERSTD = 31;
    public static final int SOCIAL_AGREE = 29;
    public static final int SOCIAL_DENY = 11;
    public static final int SOCIAL_WHAT_TIME = 23;
    public static final int SOCIAL_WHAT_DAY = 14;
    public static final int SOCIAL_ASK_DEVICEAREA = 32;
    public static final int SOCIAL_ASK_DEVICEONLY = 33;
    public static final int SOCIAL_ASK_MODE = 34;
    public static final int SOCIAL_WHAT_SEX = 12;
    public static final String FUNCTION_FOR_SCRIPT = "startMode,stopMode";
    public static final String FUNCTION_FOR_DEVICE = "turnObjectOn,turnObjectOff,increaseTemperature,decreaseTemperature";
    public static final int NOT_LEARN_YET = 35;
    public static final int FUNCTION_TURN_ON = 1034;
    public static final int FUNCTION_TURN_OFF = 1036;
    public static final int FUNCTION_INC_TEMP = 1032;
    public static final int FUNCTION_DEC_TEMP = 1035;
    public static final int FUNCTION_START_MODE = 1027;
    public static final int FUNCTION_STOP_MODE = 1033;
    public static final int SERVICE_PERIOD = 3500;
    public static final int PRIORITY_MAX = 100;
    public static final int DURING_MAX = 1000;
    public static final int SAY_BYE = 24;
    public static final int CHECK_PERSON = 1029;
    public static final int CHECK_TEMPERATUR = 1031;
    public static final int SHOW_CAMERA = 1026;
    public static final int CHECK_SECURITTY = 1030;
    public static final int CHECK_ELECTRIC = 1038;
    public static final int CHECK_DEV_STATE= 1028;
    public static final int CHECK_LIGHT= 1037;
    public static final int SOCIAL_FEEL_HOT = 10;
    public static final int SOCIAL_FEEL_COLD = 22;
    public static final int SOCIAL_APPEL = 26;
    public static final int UPDATE_BRAIN = 36;
    public static final int UPDATE_CONFIG = 37;
    public static final int SOCIAL_THANK = 8;
    public static final int OWNER_LEAVE = 30;
    public static final int NO_BODY_HOME_STATE= 1;
    public static final int OWNER_IN_HOUSE_STATE = 10;

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
    public static String getVerbByIntent(int functId) {
        switch (functId){
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
    public static String getVietnameseName(int functId) {
        switch (functId){
            case FUNCTION_TURN_ON:
                return "bật thiết bị";
            case FUNCTION_START_MODE:
                return "bật chế độ";
            case FUNCTION_TURN_OFF:
                return "tắt thiết bị";
            case FUNCTION_STOP_MODE:
                return "dừng chế độ";
            case FUNCTION_INC_TEMP:
                return "tăng nhiệt độ";
            case FUNCTION_DEC_TEMP:
                return "giảm nhiệt độ";
            case SHOW_CAMERA:
                return "xem hình camera";
            case CHECK_DEV_STATE:
                return "kiểm tra trạng thái thiết bị";
            case CHECK_ELECTRIC:
                return "kiểm tra thiết bị sử dụng điện";
            case CHECK_LIGHT:
                return "kiểm tra thiết bị chiếu sáng";
            case CHECK_PERSON:
                return "kiểm tra người";
            case CHECK_SECURITTY:
                return "kiểm tra an ninh";
            case CHECK_TEMPERATUR:
                return "kiểm tra nhiệt độ";
        }
        return "";
    }
}
