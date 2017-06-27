package center.control.system.vash.controlcenter.area;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.database.SQLiteManager;

/**
 * Created by Thuans on 5/29/2017.
 */

public class AreaSQLite {
    public static final String TABLE_AREA = "area";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TEMP = "temperature";
    private static final String KEY_LIGHT = "light";
    private static final String KEY_SAFE = "safety";
    private static final String KEY_NICKNAME = "nickName";
    private static final String KEY_ELECT_USING = "electricUsing";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_ADDRESS = "connectAddress";
    private static final String KEY_NAME = "name";
    private static final String TAG = "Area Sqlite";

    public static String createTable(){
        return "CREATE TABLE " + TABLE_AREA  + " ( "
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT ," +
                KEY_ADDRESS+ "  TEXT  ,"+
                KEY_TEMP + "  TEXT  ,"+
                KEY_LIGHT + "  TEXT  ,"+
                KEY_SAFE + "  TEXT  ,"+
                KEY_NICKNAME + "  TEXT  ,"+
                KEY_ELECT_USING + "  TEXT  ,"+
                KEY_SOUND + "  TEXT  "+ ")";
    }


    public int insert(AreaEntity area) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, area.getName());
        values.put(KEY_ADDRESS, area.getConnectAddress());
        values.put(KEY_TEMP, area.getTemperature());
        values.put(KEY_LIGHT, area.getLight());
        values.put(KEY_SAFE, area.getSafety());
        values.put(KEY_NICKNAME, area.getNickName());
        values.put(KEY_ELECT_USING, area.getElectricUsing());
        values.put(KEY_SOUND, area.getSound());

        // Inserting Row
        int newId  = (int) db.insert(TABLE_AREA, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<AreaEntity> getAll(){
        List<AreaEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_AREA;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AreaEntity area = cursorToEnt(cursor);
               Log.d(TAG,area.getId()+"");
                result.add(area);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();

        return result;
    }

    public static void upAddressAndNickById(int id, AreaEntity area) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ADDRESS,area.getConnectAddress());
        cv.put(KEY_NICKNAME,area.getNickName());
        db.update(TABLE_AREA, cv, KEY_ID + " = "+id, null);
        SQLiteManager.getInstance().closeDatabase();
    }

    public AreaEntity findById(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_AREA
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            AreaEntity area = new AreaEntity();
            area.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            area.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            area.setConnectAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
            area.setElectricUsing(cursor.getString(cursor.getColumnIndex(KEY_ELECT_USING)));
            area.setSafety(cursor.getString(cursor.getColumnIndex(KEY_SAFE)));
            area.setLight(cursor.getString(cursor.getColumnIndex(KEY_LIGHT)));
            area.setTemperature(cursor.getString(cursor.getColumnIndex(KEY_TEMP)));
            area.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
            area.setSound(cursor.getString(cursor.getColumnIndex(KEY_SOUND)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return area;
        } else return null;

    }

    public void delete( ) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_AREA,null,null);
        SQLiteManager.getInstance().closeDatabase();
    }
    public static void deleteById(int id){
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.delete(TABLE_AREA, KEY_ID+"=?", new String[]{Integer.toString(id)});
        SQLiteManager.getInstance().closeDatabase();
    }
    static AreaEntity cursorToEnt(Cursor cursor){
        AreaEntity area = new AreaEntity();
        area.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        area.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        area.setConnectAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
        area.setElectricUsing(cursor.getString(cursor.getColumnIndex(KEY_ELECT_USING)));
        area.setSafety(cursor.getString(cursor.getColumnIndex(KEY_SAFE)));
        area.setLight(cursor.getString(cursor.getColumnIndex(KEY_LIGHT)));
        area.setTemperature(cursor.getString(cursor.getColumnIndex(KEY_TEMP)));
        area.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
        area.setSound(cursor.getString(cursor.getColumnIndex(KEY_SOUND)));
        return area;
    }
}
