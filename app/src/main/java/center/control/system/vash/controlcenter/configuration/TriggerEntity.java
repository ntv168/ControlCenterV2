package center.control.system.vash.controlcenter.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 6/23/2017.
 */

public class TriggerEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("deviceId")
    private int deviceId;
    @SerializedName("configurationId")
    private int configurationId;
    @SerializedName("deviceState")
    private String deviceState;

    public TriggerEntity() {}

    public TriggerEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }
}
