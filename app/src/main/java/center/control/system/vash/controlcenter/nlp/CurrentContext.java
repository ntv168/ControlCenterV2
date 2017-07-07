package center.control.system.vash.controlcenter.nlp;

import android.util.Log;

import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;
import center.control.system.vash.controlcenter.utils.ConstManager;

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
    private long lastConnect;
    private boolean waitingOwnerSpeak;

    public boolean isWaitingOwnerSpeak() {
        return this.waitingOwnerSpeak;
    }
    public void stopWaitOwner(){
        this.waitingOwnerSpeak = false;
    }
    public void waitOwner(){
        this.waitingOwnerSpeak = true;
    }

    public long getLastConnect() {
        return lastConnect;
    }

    public static void setInstance(CurrentContext instance) {
        CurrentContext.instance = instance;
    }

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

    public boolean finishCurrentScript(int devId) {
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(detectedFunction.getFunctionName())){
            List<CommandEntity> cmds  = ScriptSQLite.getCommandByScriptId(script.getId());
            if (cmds.size()>0) {
                Log.d("Context", cmds.get(cmds.size() - 1).getDeviceName() + "");
                if (cmds.get(cmds.size() - 1).getDeviceId() == devId) return true;
                else return false;
            } else return true;
        } else return true;
    }
}
