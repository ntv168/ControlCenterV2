package center.control.system.vash.controlcenter.area;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.utils.SmartHouse;

/**
 * Created by Thuans on 5/26/2017.
 */

public class AreaEntity extends TargetObject{
    public  static final String[] attrivutes = {"An ninh","Ánh sáng","Nhiệt độ","Camera", "Thiết bị sử dụng điện"};
    public  static final String[] attrivutesValues = {"sec","lig","tem","cam","elec"};
    public  static final int[] attributeIcon = {R.drawable.shield,R.drawable.light,
            R.drawable.temp,R.drawable.music,R.drawable.electric};
    public static final String DETECT_STRANGE = "str";
    public static final String DETECT_NOT_AVAILABLE = "noAvai";
    public static final String NOBODY = "nob";
    public static final String DETECT_AQUAINTANCE = "aqa";
    public static final String DOOR_OPEN = "do";
    public static final String DOOR_CLOSE = "dc";
    public static final String TEMP_BURN = "bu";
    public static final String TEMP_WARM = "wm";
    public static final String TEMP_COLD = "co";
    private static final double BURN_TEMP_RANGE = 60.0;
    private static final double HOT_TEMP_RANGE = 38.0;
    private static final double FRESH_TEMP_RANGE = 27.0;
    private static final String TEMP_FRESH = "fr";
    private static final double COLD_TEMP_RANGE = 16.0;
    private static final String TEMP_FREEZE = "fz";
    private static final String LIGHT_BRIGHT = "br";
    private static final String LIGHT_DARK = "dk";
    public static final long HOLD_PERSON = 11000;

    @SerializedName("temperature")
    private String temperature;
    private double tempAmout;
    @SerializedName("light")
    private String light;
    @SerializedName("safety")
    private String safety;
    @SerializedName("electricUsing")
    private String electricUsing;
    @SerializedName("detect")
    private String detect;
    @SerializedName("connectAddress")
    private String connectAddress;
    private Bitmap imageBitmap;
    private boolean hasCamera = true;
    private double detectScore;
    private long updatePerson = -1;

    public void setUpdatePerson(long updatePerson) {
        this.updatePerson = updatePerson;
    }

    public long getUpdatePerson() {
        return updatePerson;
    }

    public String getDetect() {
        if (detect == null || detect.equals(NOBODY)){
            return "Không có ai";
        }
        String result = detect;
        result = result.replace(DETECT_STRANGE,"Phát hiện người lạ");
        result = result.replace(DETECT_AQUAINTANCE,"Phát hiện người nhà");
        return result;
    }

    public void setDetect(String detect) {
        this.detect = detect;
    }

    public double getDetectScore() {
        return detectScore;
    }

    public void setDetectScore(double detectScore) {
        this.detectScore = detectScore;
    }

    public boolean isHasCamera() {
        return hasCamera;
    }

    public void setHasCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public static String[] getAttrivutes() {
        return attrivutes;
    }


    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        if (temperature!=null) {
            this.tempAmout = Double.parseDouble(temperature);
            this.tempAmout -= 5; //hard core
            if (tempAmout >= BURN_TEMP_RANGE) {
                this.temperature = TEMP_BURN;
            } else if (tempAmout >= HOT_TEMP_RANGE) {
                this.temperature = TEMP_WARM;
            } else if (tempAmout >= FRESH_TEMP_RANGE) {
                this.temperature = TEMP_FRESH;
            } else if (tempAmout >= COLD_TEMP_RANGE) {
                this.temperature = TEMP_COLD;
            } else {
                this.temperature = TEMP_FREEZE;
            }
        }
    }

    public String getLight() {
        int lightin = 0;
        for (DeviceEntity dev : SmartHouse.getInstance().getDevicesByAreaId(getId())){
            if (dev.getAttributeType()!= null && dev.getAttributeType().contains(attrivutesValues[1])) {
                if (dev.getState().equals("on") ||
                        dev.getState().equals("open")) {
                    lightin++;
                }
            }
        }
        return  "Có "+ lightin + " đèn bật";
    }
    public String getBright() {
        int lightin = 0;
        for (DeviceEntity dev : SmartHouse.getInstance().getDevicesByAreaId(getId())){
            if (dev.getAttributeType()!= null && dev.getAttributeType().contains(attrivutesValues[1])) {
                if (dev.getState().equals("on") ||
                        dev.getState().equals("open")) {
                    lightin++;
                }
            }
        }
        if (lightin>0) return LIGHT_BRIGHT;
        return  LIGHT_DARK;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }


    public String getElectricUsing() {
        int usingDev = 0;
        for (DeviceEntity dev : SmartHouse.getInstance().getDevicesByAreaId(getId())){
            if (dev.getAttributeType()!= null && dev.getAttributeType().contains(attrivutesValues[4])) {
                if (dev.getState().equals("on") ||
                        dev.getState().equals("open")) {
                    usingDev++;
                }
            }
        }
        return usingDev + " thiết bị sử dụng điện";
    }

    public void setElectricUsing(String electricUsing) {
        this.electricUsing = electricUsing;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String connectAddress) {
        this.connectAddress = connectAddress;
    }


    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String[] generateValueArr(){
        String safetyRes = "Không khả dụng";
        if (this.getSafety()!= null) {
            switch (this.getSafety()) {
                case DOOR_CLOSE:
                    safetyRes = "Đã đóng cửa";
                    break;
                case DOOR_OPEN:
                    safetyRes = "Có cửa mở";
                    break;
            }
        }
        String temperat = "Không khả dụng";
        if (this.getTemperature()!=null) {
            switch (this.getTemperature()) {
                case TEMP_BURN:
                    temperat = "Cảnh báo có cháy";
                    break;
                case TEMP_COLD:
                    temperat = "Lạnh";
                    break;
                case TEMP_FREEZE:
                    temperat = "Đóng băng";
                    break;
                case TEMP_WARM:
                    temperat = "Ấm áp";
                    break;
                case TEMP_FRESH:
                    temperat = "Mát mẻ";
                    break;
            }
        }
        temperat += " ("+this.tempAmout+")";
        int lightin = 0;
        for (DeviceEntity dev : SmartHouse.getInstance().getDevicesByAreaId(getId())){
            if (dev.getAttributeType()!= null && dev.getAttributeType().contains(attrivutesValues[1])) {
                if (dev.getState().equals("on") ||
                        dev.getState().equals("open")) {
                    lightin++;
                }
            }
        }
        String lighting = "Có "+ lightin + " đèn bật";

        String[] result = new String[]{
                safetyRes,
                lighting,
                temperat,
                this.getDetect(),
                this.getElectricUsing()
        };
        return result;
    }
    public String generateAttributeForApi(){
        String safetyRes = "Không khả dụng";
        if (this.getSafety()!= null) {
            switch (this.getSafety()) {
                case DOOR_CLOSE:
                    safetyRes = "Đã đóng cửa";
                    break;
                case DOOR_OPEN:
                    safetyRes = "Có cửa mở";
                    break;
            }
        }
        String temperat = "Không khả dụng";
        if (this.getTemperature()!=null) {
            switch (this.getTemperature()) {
                case TEMP_BURN:
                    temperat = "Cảnh báo có cháy";
                    break;
                case TEMP_COLD:
                    temperat = "Lạnh";
                    break;
                case TEMP_FREEZE:
                    temperat = "Đóng băng";
                    break;
                case TEMP_WARM:
                    temperat = "Ấm áp";
                    break;
                case TEMP_FRESH:
                    temperat = "Mát mẻ";
                    break;
            }
        }
        temperat += " ("+this.tempAmout+")";
        int lightin = 0;
        for (DeviceEntity dev : SmartHouse.getInstance().getDevicesByAreaId(getId())){
            if (dev.getAttributeType()!= null && dev.getAttributeType().contains(attrivutesValues[1])) {
                if (dev.getState().equals("on") ||
                        dev.getState().equals("open")) {
                    lightin++;
                }
            }
        }
        String lighting = "Có "+ lightin + " đèn bật";


        String result = new String(
                safetyRes+";"+
                        lighting+";"+
                        temperat+";"+
                this.getDetect()+";"+
                this.getElectricUsing());
        return result;
    }

    public String getRawDetect() {
        return detect;
    }
}
