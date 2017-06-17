package center.control.system.vash.controlcenter.script;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 6/8/2017.
 */

public class ScriptDeviceEntity {
    @SerializedName("deviceId")
    private int deviceId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("deviceState")
    private String deviceState;
    public ScriptDeviceEntity(int deviceId, String deviceState){
        this.deviceId = deviceId;
        this.deviceState = deviceState;
    }
    public ScriptDeviceEntity(){}
    private String deviceName;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
