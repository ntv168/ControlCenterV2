package center.control.system.vash.controlcenter.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.script.ScriptDeviceEntity;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Thuans on 5/27/2017.
 */

public class SmartHouse {
    private static final String TAG = "Smart house singleton";
    private static volatile SmartHouse houseInstance = null;
    private BlockingQueue<ScriptDeviceEntity> ownerCommand;
    private List<AreaEntity> areas;
    private List<DeviceEntity> devices;
    private List<ScriptEntity> scripts;

    private SmartHouse() { }

    public static SmartHouse getInstance() {
        if(houseInstance == null) {
            synchronized(SmartHouse.class) {
                if(houseInstance == null) {
                    houseInstance= new SmartHouse();
                    houseInstance.ownerCommand = new LinkedBlockingDeque<>();
                    houseInstance.setAreas(AreaSQLite.getAll());
                    houseInstance.setDevices(DeviceSQLite.getAll());
                    houseInstance.setScripts(ScriptSQLite.getAll());
                }
            }

        }
        return houseInstance;
    }
    public void addCommand(ScriptDeviceEntity command){
        try {
            this.ownerCommand.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<ScriptDeviceEntity> getOwnerCommand() {
        return ownerCommand;
    }

    public void setAreas(List<AreaEntity> areas) {
        this.areas = areas;
    }

    public List<AreaEntity> getAreas() {
        return areas;
    }
    public AreaEntity getAreaById(int areaId) {
        for (AreaEntity area: this.getAreas()){
            if (area.getId() == areaId){
                return area;
            }
        }
        return null;
    }

    public void updateSensorArea(int areaId, String response) {
        for (AreaEntity area: this.getAreas()){
            if (area.getId() == areaId){

                Log.d(TAG,areaId+ "   "+ response);
                break;
            }
        }
    }

    public void updateCameraArea(int areaId, Bitmap tmp) {
        for (AreaEntity area: this.getAreas()){
            if (area.getId() == areaId){
                area.setImageBitmap(tmp);
                break;
            }
        }
        Log.d(TAG,areaId+ "   "+ areaId);
    }

    public void setScripts(List<ScriptEntity> scripts) {
        this.scripts = scripts;
    }

    public List<ScriptEntity> getScripts() {
        return scripts;
    }

    public List<DeviceEntity> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceEntity> devices) {
        this.devices = devices;
    }

    public void updateDeviceById(int id, DeviceEntity device) {
        for (int i=0; i<this.getDevices().size();i++){
            if (this.getDevices().get(i).getId() == id){
                this.getDevices().set(i,device);
            }
        }
    }

    public List<DeviceEntity> getDevicesByAreaId(int id) {
        List<DeviceEntity> result = new ArrayList<>();
        for (DeviceEntity device : this.getDevices()){
            if (device.getAreaId() == id){
                result.add(device);
            }
        }
        return result;
    }

    public String generateDeviceByAreaForApi(int areaId){
        List<DeviceEntity> devices=  getDevicesByAreaId(areaId);
        String result = "";
        for (DeviceEntity deviceEntity: devices){
            result += deviceEntity.getName()+"="+
                    deviceEntity.getId()+"="+
                    deviceEntity.getType()+"="+
                    deviceEntity.getState()+"=["+
                    deviceEntity.getAttributeType()+"];";
        }

        return result;
    }

    public void removeAreaAndItsDevice(int id) {
        DeviceSQLite.deleteByAreaId(id);
        AreaSQLite.deleteById(id);
        for (int i = 0; i<this.getAreas().size(); i++) {
            if (this.getAreas().get(i).getId() == id){
                this.getAreas().remove(i);
            }
        }
    }
    public ArrayAdapter<String> getAreaNameAdapter(Context ctx){
        ArrayAdapter<String> areaNameAdapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_singlechoice);
        for (AreaEntity areaEntity : areas){
            areaNameAdapter.add(areaEntity.getName());
        }
        return areaNameAdapter;
    }

    public List<ScriptDeviceEntity> getDeviceScriptByAreaId(int areaId) {
        List<ScriptDeviceEntity> areaNameAdapter = new ArrayList<>();
        for (DeviceEntity deviceEntity: devices){
            if (deviceEntity.getAreaId() == areaId) {
                ScriptDeviceEntity sde = new ScriptDeviceEntity();
                sde.setDeviceName(deviceEntity.getName());
                sde.setGroupId(areaId);
                sde.setDeviceId(deviceEntity.getId());
                areaNameAdapter.add(sde);
            }
        }
        return areaNameAdapter;
    }

    public void updateAreaById(int id, AreaEntity area) {
        for (int i=0; i<this.getAreas().size();i++){
            if (this.getAreas().get(i).getId() == id){
                this.getAreas().set(i,area);
            }
        }
    }

    public void removeDeviceByArea(int id) {
        for (int i=0; i<this.getDevices().size();i++){
            if (this.getDevices().get(i).getAreaId() == id){
                this.getDevices().remove(i);
            }
        }
    }

    public DeviceEntity getDeviceById(int deviceId) {
        for (DeviceEntity device: this.getDevices()){
            if (device.getId() == deviceId){
                return  device;
            }
        }
        return null;
    }

    public List<DeviceEntity> getDevicesInAreaAttribute(int areaId, String attrbute) {
        List<DeviceEntity> result = new ArrayList<>();
        for (DeviceEntity device : this.getDevices()){
            Log.d(TAG,device.getAttributeType()+" -- "+device.getName()+" --- "+ device.getAreaId());
            if (device.getAreaId() == areaId
                    && device.getAttributeType()!=null
                    && device.getAttributeType().contains(attrbute)){
                result.add(device);
            }
        }
        return result;
    }
}
