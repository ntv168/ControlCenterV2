package center.control.system.vash.controlcenter.device;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;


/**
 * Created by Sam on 7/1/2017.
 */

public class TriggerDeviceSQLite {
    public static final String TABLE_DEVICE = "trigger_device";
    // Contacts Table Columns names
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_TRIGGER_ID = "triggerId";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VALUE = "value";
    private static final String KEY_NAME = "name";

    private static final String TAG = "Device sql lite";

    public static String createTable(){
        return "CREATE TABLE " + TABLE_DEVICE  + "("
                +KEY_DEVICE_ID + " INTEGER,"
                +KEY_TRIGGER_ID + " INTEGER,"
                +KEY_NAME + " TEXT,"
                +KEY_VALUE + " TEXT,"
                +KEY_TYPE + " INTEGER"
                + ")";
    }

    public int insert(TriggerDeviceEntity triggerDevice) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();

        int newId  = (int) db.insert(TABLE_DEVICE, null, createContent(triggerDevice));
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<TriggerDeviceEntity> getAll(){
        List<TriggerDeviceEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                ;

//        Log.d(TABLE_DEVICE, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TriggerDeviceEntity TriggerDeviceEntity = getByCursor(cursor);
//                Log.d(TAG,TriggerDeviceEntity.getName()+ " --- "+TriggerDeviceEntity.getAreaId());
                result.add(TriggerDeviceEntity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();
        Log.d(TAG, "getAll: --------" + result.size());
        return result;
    }

    public static List<TriggerDeviceEntity> getDevicesByTriggerandTypeId(int id, int type){
        List<TriggerDeviceEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                + " WHERE "+KEY_TRIGGER_ID+" = ? AND " + KEY_TYPE + "= ?";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id),String.valueOf(type)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TriggerDeviceEntity TriggerDeviceEntity = getByCursor(cursor);
                result.add(TriggerDeviceEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        SQLiteManager.getInstance().closeDatabase();
        Log.d(TAG, "getDevicesByTriggerId: --------" + result.size());
        return result;

    }

    public static List<TriggerDeviceEntity> getDevicesByTriggerId(int id){
        List<TriggerDeviceEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                + " WHERE "+KEY_TRIGGER_ID+" = ?";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TriggerDeviceEntity TriggerDeviceEntity = getByCursor(cursor);
                result.add(TriggerDeviceEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        SQLiteManager.getInstance().closeDatabase();
        Log.d(TAG, "getDevicesByTriggerId: --------" + result.size());
        return result;

    }

    public static TriggerDeviceEntity findBySensorId(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_DEVICE
                + " WHERE "+KEY_DEVICE_ID+" = ? AND" + KEY_TYPE + "= 2";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            TriggerDeviceEntity deviceEntity = getByCursor(cursor);
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
    private static ContentValues createContent(TriggerDeviceEntity triggerDevice){
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_ID, triggerDevice.getDeviceId());
        values.put(KEY_TRIGGER_ID, triggerDevice.getTriggerId());
        values.put(KEY_NAME, triggerDevice.getName());
        values.put(KEY_VALUE, triggerDevice.getValue());
        values.put(KEY_TYPE, triggerDevice.getType());
        return values;
    }
    private static TriggerDeviceEntity getByCursor(Cursor cursor){
        TriggerDeviceEntity TriggerDeviceEntity = new TriggerDeviceEntity();
        TriggerDeviceEntity.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        TriggerDeviceEntity.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
        TriggerDeviceEntity.setTriggerId(cursor.getInt(cursor.getColumnIndex(KEY_NAME)));
        TriggerDeviceEntity.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
        TriggerDeviceEntity.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
        return TriggerDeviceEntity;
    }

//    public static void upById(int id, TriggerDeviceEntity device) {
//        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
//        db.update(TABLE_DEVICE, createContent(device), KEY_ID + " = "+id, null);
//        Log.d(TAG, "upById: ----------------- " + device.getTriggerId());
//        SQLiteManager.getInstance().closeDatabase();
//    }

    public static void delete(int deviceId, int triggerId) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_DEVICE, KEY_DEVICE_ID +"=?, " + KEY_TRIGGER_ID + "=?",
                new String[]{Integer.toString(deviceId),Integer.toString(triggerId)});
        SQLiteManager.getInstance().closeDatabase();
    }

}
