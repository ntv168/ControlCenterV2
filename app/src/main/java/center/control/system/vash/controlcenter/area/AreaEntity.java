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
    public  static final String[] attrivutes = {"An ninh","Ánh sáng","Nhiệt độ","Âm thanh", "Thiết bị sử dụng điện"};
    public  static final String[] attrivutesValues = {"security","light","temperature","sound","runningDevice"};
    public  static final int[] attributeIcon = {R.drawable.shield,R.drawable.light,
            R.drawable.temp,R.drawable.music,R.drawable.electric};

    @SerializedName("temperature")
    private String temperature;
    @SerializedName("light")
    private String light;
    @SerializedName("safety")
    private String safety;
    @SerializedName("electricUsing")
    private String electricUsing;
    @SerializedName("sound")
    private String sound;
    @SerializedName("connectAddress")
    private String connectAddress;
    private String personDetect;
    private Bitmap imageBitmap;
    private boolean hasCamera = true;

    public String getPersonDetect() {
        return personDetect;
    }

    public void setPersonDetect(String personDetect) {
        this.personDetect = personDetect;
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

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
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
                this.getSound(),
                this.getElectricUsing()
        };
        return result;
    }
    public String generateAttributeForApi(){
        String result = new String(
                this.getSafety()+";"+
                this.getTemperature()+";"+
                this.getLight()+";"+
                this.getSound()+";"+
                this.getElectricUsing());
        return result;
    }

}
