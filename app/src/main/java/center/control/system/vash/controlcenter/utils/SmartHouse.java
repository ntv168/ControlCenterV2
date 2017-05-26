package center.control.system.vash.controlcenter.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;

/**
 * Created by Thuans on 5/27/2017.
 */

public class SmartHouse {
    private static final String TAG = "Smart house singleton";
    private static volatile SmartHouse houseInstance = null;

    private List<AreaEntity> areas;

    private SmartHouse() { }

    public static SmartHouse getInstance() {
        if(houseInstance == null) {
            synchronized(SmartHouse.class) {
                if(houseInstance == null) {
                    houseInstance= new SmartHouse();
                    houseInstance.areas = new ArrayList<>();
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
}
