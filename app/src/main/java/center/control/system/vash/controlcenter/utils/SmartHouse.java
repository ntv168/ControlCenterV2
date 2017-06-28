package center.control.system.vash.controlcenter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.configuration.CommandEntity;
import center.control.system.vash.controlcenter.configuration.ConfigurationEntity;
import center.control.system.vash.controlcenter.configuration.ConfigurationSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;

import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Thuans on 5/27/2017.
 */

public class SmartHouse {
    private static final String TAG = "Smart house singleton";
    private static volatile SmartHouse houseInstance = null;
    private BlockingQueue<CommandEntity> ownerCommand;
    private List<AreaEntity> areas;
    private List<DeviceEntity> devices;
    private List<ScriptEntity> scripts;
    private String botName;
    private String botRole;
    private String ownerName;
    private String ownerRole;

    private List<ConfigurationEntity> configurations;

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
                    houseInstance.setConfigurations(ConfigurationSQLite.getAll());
                }
            }

        }
        return houseInstance;
    }
    public void addCommand(CommandEntity command){
        try {
            this.ownerCommand.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<CommandEntity> getOwnerCommand() {
        return ownerCommand;
    }

    public void setAreas(List<AreaEntity> areas) {
        this.areas = areas;
    }

    public List<AreaEntity> getAreas() {
        return areas;
    }
    public static AreaEntity getAreaById(int areaId) {
        for (AreaEntity area: houseInstance.getAreas()){
            if (area.getId() == areaId){
                return area;
            }
        }
        return null;
    }

    public void updateSensorArea(int areaId, String response) {
        for (AreaEntity area: this.getAreas()){
            if (area.getId() == areaId){
                String[] ele = response.split(",");
                for (int i = 0; i< ele.length; i++){
                     if (ele[i].length()>1){
                        String[] val = ele[i].split(":");
                         if (val[0].equals(AreaEntity.attrivutesValues[0])){
                            area.setSafety(val[1]);
                        } else if (val[0].equals(AreaEntity.attrivutesValues[1])){
                            area.setLight(val[1]);
                        } else if (val[0].equals(AreaEntity.attrivutesValues[2])){
                            area.setTemperature(val[1]);
                        }else if (val[0].equals(AreaEntity.attrivutesValues[3])){
                            area.setSound(val[1]);
                        }else if (val[0].equals(AreaEntity.attrivutesValues[4])){
                            area.setElectricUsing(val[1]);
                        }
                    }
                }
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
    }

    public void setScripts(List<ScriptEntity> scripts) {
        this.scripts = scripts;
    }

    public void setConfigurations(List<ConfigurationEntity> configurations) {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        this.configurations = configurations;
    }

    public List<ScriptEntity> getScripts() {
        return scripts;
    }

    public List<DeviceEntity> getDevices() {
        return devices;
    }

    public List<ConfigurationEntity> getConfigurations() {
        return configurations;
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
            Log.d(TAG,device.getAreaId()+"  id");
            if (device.getAreaId() == id){
                result.add(device);
            }
        }
        return result;
    }

    public String generateDeviceByAreaForApi(int areaId){
        List<DeviceEntity> devices=  getDevicesByAreaId(areaId);
        Log.d(TAG,devices.size()+"  s");
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

    public List<CommandEntity> getDeviceScriptByAreaId(int areaId) {
        List<CommandEntity> areaNameAdapter = new ArrayList<>();
        for (DeviceEntity deviceEntity: devices){
            if (deviceEntity.getAreaId() == areaId) {
                CommandEntity sde = new CommandEntity();
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
            if (device.getAreaId() == areaId
                    && device.getAttributeType()!=null
                    && device.getAttributeType().contains(attrbute)){
                result.add(device);
            }
        }
        return result;
    }

    public void updateDeviceStateById(int id, String status) {
       for (DeviceEntity dev : this.getDevices()){
           if (dev.getId() == id){
               dev.setState(status);
               return;
           }
       }
    }

    public void updatePictureArea(int id, Bitmap decodedByte) {
        for (AreaEntity area: this.areas){
            if (area.getId() == id){
                area.setImageBitmap(decodedByte);
                return;
            }
        }
    }

    public Bitmap getBitmapByAreaId(int areaId) {
        for (AreaEntity area: this.areas){
            if (area.getId() == areaId){

                return area.getImageBitmap();
            }
        }
        return null;
    }

    public void updateModeById(int id, ScriptEntity mode) {
        for (int i=0; i<this.getScripts().size();i++){
            if (this.getScripts().get(i).getId() == id){
                this.getScripts().set(i,mode);
            }
        }
    }

    public String getBotName() {
        return botName;
    }

    public String getBotRole() {
        return botRole;
    }

    public String getOwnerRole() {
        return ownerRole;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public void setBotOwnerNameRole(String bn,String br, String on, String or){
        this.botName = bn;
        this.botRole = br;
        this.ownerName = on;
        this.ownerRole = or;
    }
}
