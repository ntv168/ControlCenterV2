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
    public static final String TEMP_WARM = "w";
    public static final String TEMP_COLD = "c";

    @SerializedName("temperature")
    private String temperature;
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

    public String getDetect() {
        return detect;
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
        this.temperature = temperature;
    }

    public String getLight() {
        return light;
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
        String[] result = new String[]{
                this.getSafety(),
                this.getLight(),
                this.getTemperature(),
                this.getDetect(),
                this.getElectricUsing()
        };
        return result;
    }
    public String generateAttributeForApi(){
        String result = new String(
                this.getSafety()+";"+
                this.getTemperature()+";"+
                this.getLight()+";"+
                this.getDetect()+";"+
                this.getElectricUsing());
        return result;
    }

}
