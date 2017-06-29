package center.control.system.vash.controlcenter.nlp;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.script.ScriptEntity;

/**
 * Created by Thuans on 5/2/2017.
 */

public class CurrentContext {
    private static volatile CurrentContext instance = null;
    private DetectFunctionEntity detectedFunction;
    private DetectSocialEntity detectSocial;
    private TargetObject targetObject;
    private AreaEntity area;
    private DeviceEntity device;
    private ScriptEntity script;
    private String sentence;

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public AreaEntity getArea() {
        return area;
    }

    public void setArea(AreaEntity area) {
        this.area = area;
    }

    public DeviceEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceEntity device) {
        this.device = device;
    }

    private CurrentContext() { }

    public static CurrentContext getInstance() {
        if(instance == null) {
            synchronized(CurrentContext.class) {
                if(instance == null) {
                    instance= new CurrentContext();
                }
            }

        }
        return instance;
    }

    public DetectFunctionEntity getDetectedFunction() {
        return detectedFunction;
    }

    public void setDetectedFunction(DetectFunctionEntity detectedFunction) {
        this.detectedFunction = detectedFunction;
    }

    public DetectSocialEntity getDetectSocial() {
        return detectSocial;
    }

    public void setDetectSocial(DetectSocialEntity detectSocial) {
        this.detectSocial = detectSocial;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
    }

    public void setScript(ScriptEntity script) {
        this.script = script;
    }

    public ScriptEntity getScript() {
        return script;
    }

    public void renew() {
        this.detectedFunction = null;
        this.detectSocial = null;
        this.area = null;
        this.device = null;
        this.script = null;
        this.sentence = null;
    }
}
