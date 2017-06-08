package center.control.system.vash.controlcenter.area;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/26/2017.
 */

public class AreaEntity {
    public  static final String[] attrivutes = {"An ninh","Ánh sáng","Nhiệt độ","Âm thanh", "Thiết bị sử dụng điện"};
    @SerializedName("id")
    private int id;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("light")
    private String light;
    @SerializedName("safety")
    private String safety;
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("electricUsing")
    private String electricUsing;
    @SerializedName("sound")
    private String sound;
    @SerializedName("connectAddress")
    private String connectAddress;
    @SerializedName("name")
    private String name;
    private Bitmap imageBitmap;

    public static String[] getAttrivutes() {
        return attrivutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getElectricUsing() {
        return electricUsing;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
