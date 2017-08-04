package center.control.system.vash.controlcenter.server;

/**
 * Created by MYNVTSE61526 on 08/07/2017.
 */
public class EventDTO {
    private int id;
    private String sensorName;
    private String sensorValue;
    private int priority;
    private int nextState;
    private int previousState;
    private int configurationId;
    private String nextStateName;
    private String previousStateName;

    public String getNextStateName() {
        return nextStateName;
    }

    public void setNextStateName(String nextStateName) {
        this.nextStateName = nextStateName;
    }

    public void setPreviousStateName(String previousStateName) {
        this.previousStateName = previousStateName;
    }

    public String getPreviousStateName() {
        return previousStateName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getNextState() {
        return nextState;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }

    public int getPreviousState() {
        return previousState;
    }

    public void setPreviousState(int previousState) {
        this.previousState = previousState;
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }
}
