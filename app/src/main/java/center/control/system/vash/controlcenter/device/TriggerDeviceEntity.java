package center.control.system.vash.controlcenter.device;

/**
 * Created by Sam on 7/1/2017.
 */

public class TriggerDeviceEntity {
    private int deviceId;
    private int triggerId;
    private String value;
    private int type;
    private String name;


    public TriggerDeviceEntity() {
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

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int triggerId) {
        this.triggerId = triggerId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
