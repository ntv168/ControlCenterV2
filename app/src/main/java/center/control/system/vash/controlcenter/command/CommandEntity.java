package center.control.system.vash.controlcenter.command;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 6/8/2017.
 */

public class CommandEntity {
    @SerializedName("deviceId")
    private int deviceId;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("configurationId")
    private int configurationId;
    @SerializedName("deviceState")
    private String deviceState;
    public CommandEntity(int deviceId, String deviceState){
        this.deviceId = deviceId;
        this.deviceState = deviceState;
    }

    public CommandEntity(int deviceId, String deviceState, int groupId){
        this.deviceId = deviceId;
        this.deviceState = deviceState;
        this.groupId = groupId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public CommandEntity(){}
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

    public void inverseDeviceState() {
        if (deviceState.equals("on")){
            deviceState.equals("off");
        } else if (deviceState.equals("off")){
            deviceState.equals("on");
        }
    }
}
