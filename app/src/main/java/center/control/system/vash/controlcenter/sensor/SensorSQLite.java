package center.control.system.vash.controlcenter.sensor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Sam on 6/30/2017.
 */

public class SensorSQLite {
    public static final String TABLE_SENSOR = "sensor_condition";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AREA_ID = "areaId";
    private static final String KEY_TRIGGER_ID = ScriptSQLite.KEY_CONFIG_ID;
    private static final String TAG = "sensor SQLite";


    public static String createSensor(){
        return "CREATE TABLE " + TABLE_SENSOR  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME+ "  TEXT,"
                + KEY_TRIGGER_ID + " INTEGER,"
                + KEY_AREA_ID + " INTEGER"
                + ")";
    }

    public int insertSensor(String name, int areaId) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TRIGGER_ID, 0);
        values.put(KEY_AREA_ID, areaId);

        // Inserting Row
        int newId  = (int) db.insert(TABLE_SENSOR, null, values);
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

    public SensorEntity findById(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_SENSOR
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            SensorEntity sensor = new SensorEntity();
            sensor.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            sensor.setTriggerId(cursor.getInt(cursor.getColumnIndex(KEY_TRIGGER_ID)));
            sensor.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA_ID)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return sensor;
        } else return null;

    }

    public void cleardata() {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL(createSensor());
        SQLiteManager.getInstance().closeDatabase();

    }

    static SensorEntity cursorToEnt(Cursor cursor){
        SensorEntity sensor = new SensorEntity();
        sensor.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        sensor.setTriggerId(cursor.getInt(cursor.getColumnIndex(KEY_TRIGGER_ID)));
        sensor.setAreaId(cursor.getInt(cursor.getColumnIndex(KEY_AREA_ID)));

        return sensor;
    }

}
