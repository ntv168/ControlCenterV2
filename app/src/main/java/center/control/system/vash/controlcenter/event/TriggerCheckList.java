package center.control.system.vash.controlcenter.event;

import center.control.system.vash.controlcenter.device.TriggerDeviceSQLite;
import center.control.system.vash.controlcenter.sensor.SensorEntity;
import center.control.system.vash.controlcenter.sensor.SensorSQLite;

/**
 * Created by Sam on 7/1/2017.
 */

public class TriggerCheckList {
    private static volatile TriggerCheckList instance = null;
    private SensorEntity sensor;
    private int triggerId;
    private int numberDeviceinTrigger;
    private int configId;
    private int numberTriggerinConfig;
    private String deviceValue;

    public TriggerCheckList getInstance(int areaId, String attribute, String value) {

        if(instance == null) {
            synchronized(TriggerCheckList.class) {
                if(instance == null) {
                    instance= new TriggerCheckList();
                    instance.setSensor(SensorSQLite.findByAttribute(areaId,attribute));
                    instance.setTriggerId(TriggerDeviceSQLite.findBySensorId(sensor.getId()).getDeviceId());
                    instance.setNumberDeviceinTrigger(
                            TriggerDeviceSQLite.getDevicesByTriggerId(triggerId).size());
                    instance.setConfigId(TriggerSQLite.findById(triggerId).getConfigurationId());
                    instance.setNumberTriggerinConfig(
                            TriggerSQLite.getTriggerByConfigId(configId).size());
                    instance.setDeviceValue(value);

                }
            }

        }
        return instance;
    }

    public boolean checkTrigger() {
        switch (sensor.getAttribute()) {
            case "temperature":

        }
        return false;
    }

    public SensorEntity getSensor() {
        return sensor;
    }

    public void setSensor(SensorEntity sensor) {
        this.sensor = sensor;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int triggerId) {
        this.triggerId = triggerId;
    }


    public int getNumberDeviceinTrigger() {
        return numberDeviceinTrigger;
    }

    public void setNumberDeviceinTrigger(int numberDeviceinTrigger) {
        this.numberDeviceinTrigger = numberDeviceinTrigger;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getNumberTriggerinConfig() {
        return numberTriggerinConfig;
    }

    public void setNumberTriggerinConfig(int numberTriggerinConfig) {
        this.numberTriggerinConfig = numberTriggerinConfig;
    }

    public String getDeviceValue() {
        return deviceValue;
    }

    public void setDeviceValue(String deviceValue) {
        this.deviceValue = deviceValue;
    }
}
