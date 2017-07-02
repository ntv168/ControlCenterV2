package center.control.system.vash.controlcenter.sensor;

/**
 * Created by Sam on 6/30/2017.
 */

public class SensorEntity {
    private int id;
    private String name;
    private int areaId;
    private int triggerId;
    private String attribute;

    public SensorEntity() {

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

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int configId) {
        this.triggerId = configId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
