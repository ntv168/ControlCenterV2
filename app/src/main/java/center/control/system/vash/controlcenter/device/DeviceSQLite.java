package center.control.system.vash.controlcenter.device;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;

/**
 * Created by Thuans on 4/27/2017.
 */

public class DeviceSQLite {

    public static final String TABLE_DEVICE = "device";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PORT = "controlAddress";
    private static final String KEY_NAME = "name";
    private static final String KEY_ATTR_TYPE = "attributeType";
    private static final String KEY_STATE = "state";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ICON = "icon";
    private static final String KEY_AREA = "areaId";
    private static final String KEY_NICKNAME = "nickName";
    private static final String TAG = "Device sql lite";

    public static String createTable(){
        return "CREATE TABLE " + TABLE_DEVICE  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT ," +
                KEY_PORT+ "  TEXT  ,"+
                KEY_ATTR_TYPE + "  TEXT  ,"+
                KEY_STATE + "  TEXT  ,"+
                KEY_TYPE + "  TEXT  ,"+
                KEY_ICON + "  TEXT  ,"+
                KEY_NICKNAME + "  TEXT  ,"+
                KEY_AREA+ "  INTEGER  "+ ")";
    }


    public int insert(DeviceEntity device) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();

        int newId  = (int) db.insert(TABLE_DEVICE, null, createContent(device));
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<DeviceEntity> getAll(){
        List<DeviceEntity> result = new ArrayList<DeviceEntity>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                ;

        Log.d(TABLE_DEVICE, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeviceEntity deviceEntity = getByCursor(cursor);
                if (deviceEntity.getName() == null) deviceEntity.setName("...");
                Log.d(TAG,deviceEntity.getName()+ " --- "+deviceEntity.getAreaId());
                result.add(deviceEntity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }
    public DeviceEntity findById(String id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            DeviceEntity deviceEntity = getByCursor(cursor);
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return deviceEntity;
        } else return null;

    }

    public void delete( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_DEVICE,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
    private ContentValues createContent(DeviceEntity device){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getName());
        values.put(KEY_PORT, device.getPort());
        values.put(KEY_TYPE, device.getType());
        values.put(KEY_ATTR_TYPE, device.getAttributeType());
        values.put(KEY_STATE, device.getState());
        values.put(KEY_ICON, device.getIconId());
        values.put(KEY_AREA, device.getAreaId());
        values.put(KEY_NICKNAME, device.getNickName());
        return values;
    }
    private static DeviceEntity getByCursor(Cursor cursor){
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        deviceEntity.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        deviceEntity.setPort(cursor.getString(cursor.getColumnIndex(KEY_PORT)));
        deviceEntity.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
        deviceEntity.setAttributeType(cursor.getString(cursor.getColumnIndex(KEY_ATTR_TYPE)));
        deviceEntity.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA)));
        deviceEntity.setIconId(cursor.getString(cursor.getColumnIndex(KEY_ICON)));
        deviceEntity.setState(cursor.getString(cursor.getColumnIndex(KEY_STATE)));
        deviceEntity.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
        return deviceEntity;
    }

    public void upById(int id, DeviceEntity device) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.update(TABLE_DEVICE, createContent(device), KEY_ID + " = "+id, null);
        SQLiteManager.getInstance().closeDatabase();
    }

    public static void deleteByAreaId(int id) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_DEVICE, KEY_AREA+"=?", new String[]{Integer.toString(id)});
        SQLiteManager.getInstance().closeDatabase();
    }
}