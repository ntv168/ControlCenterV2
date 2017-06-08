package center.control.system.vash.controlcenter.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;

/**
 * Created by Thuans on 5/27/2017.
 */

public class SmartHouse {
    private static final String TAG = "Smart house singleton";
    private static volatile SmartHouse houseInstance = null;

    private List<AreaEntity> areas;
    private List<DeviceEntity> devices;

    private SmartHouse() { }

    public static SmartHouse getInstance() {
        if(houseInstance == null) {
            synchronized(SmartHouse.class) {
                if(houseInstance == null) {
                    houseInstance= new SmartHouse();
                    houseInstance.setAreas(AreaSQLite.getAll());
                    houseInstance.setDevices(DeviceSQLite.getAll());
                }
            }

        }
        return houseInstance;
    }

    public void setAreas(List<AreaEntity> areas) {
        this.areas = areas;
    }

    public List<AreaEntity> getAreas() {
        return areas;
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
}
