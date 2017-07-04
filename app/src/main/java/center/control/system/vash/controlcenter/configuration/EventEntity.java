package center.control.system.vash.controlcenter.configuration;

/**
 * Created by Thuans on 7/2/2017.
 */

public class EventEntity {
    private int id;
    private String senName;
    private String senValue;
    private int priority;
    private int areaId;
    private int nextStateId;

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenName() {
        return senName;
    }

    public void setSenName(String senName) {
        this.senName = senName;
    }

    public String getSenValue() {
        return senValue;
    }

    public void setSenValue(String senValue) {
        this.senValue = senValue;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getNextStateId() {
        return nextStateId;
    }

    public void setNextStateId(int nextStateId) {
        this.nextStateId = nextStateId;
    }
}
