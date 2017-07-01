package center.control.system.vash.controlcenter.sensor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Sam on 6/30/2017.
 */

public class SensorSQLite {
    public static final String TABLE_SENSOR = "sensor_condition";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AREA_ID = "areaId";
    private static final String KEY_ATTRIBUTE = "attribute";
    private static final String TAG = "sensor SQLite";


    public static String createSensor(){
        return "CREATE TABLE " + TABLE_SENSOR  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME+ "  TEXT,"
                + KEY_AREA_ID + " INTEGER,"
                + KEY_ATTRIBUTE + " TEXT"
                + ")";
    }

    public int insertSensor(SensorEntity sensor) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        // Inserting Row
        int newId  = (int) db.insert(TABLE_SENSOR, null, createContent(sensor));
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<SensorEntity> getAll(){
        List<SensorEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SENSOR;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SensorEntity SensorEntity = cursorToEnt(cursor);
                Log.d(TAG, SensorEntity.getId()+"");
                result.add(SensorEntity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }

    public static SensorEntity findByAttribute(int areaid, String attribute){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SENSOR
                + " WHERE "+KEY_AREA_ID+" = ? AND" + KEY_ATTRIBUTE + "= ?";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(areaid),attribute});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            SensorEntity sensor = new SensorEntity();
            sensor.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            sensor.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA_ID)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return sensor;
        } else return null;

    }

    public static void upById(int id, SensorEntity sensor) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_AREA_ID, sensor.getAreaId());

        db.update(TABLE_SENSOR, values, KEY_ID + " = "+id, null);
        Log.d(TAG, "upById: ----------------- " );
        SQLiteManager.getInstance().closeDatabase();
    }

    public void cleardata() {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL(createSensor());
        SQLiteManager.getInstance().closeDatabase();

    }

    private static ContentValues createContent(SensorEntity sensor){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_AREA_ID, sensor.getAreaId());
        return values;
    }

    static SensorEntity cursorToEnt(Cursor cursor){
        SensorEntity sensor = new SensorEntity();
        sensor.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        sensor.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA_ID)));
        sensor.setAttribute(cursor.getString(cursor.getColumnIndex(KEY_ATTRIBUTE)));

        return sensor;
    }

}
