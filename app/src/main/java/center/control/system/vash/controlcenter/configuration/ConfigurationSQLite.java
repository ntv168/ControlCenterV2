package center.control.system.vash.controlcenter.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.database.SQLiteManager;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Thuans on 6/23/2017.
 */

public class ConfigurationSQLite {

    public static final String TABLE_CONFIGURATION = "configuration";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String TAG = "Configuration SQLite";


    public static String createConfiguration(){
        return "CREATE TABLE " + TABLE_CONFIGURATION  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME+ "  TEXT "
                +")";
    }


    public int insertConfiguration(String name) {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        // Inserting Row
        int newId  = (int) db.insert(TABLE_CONFIGURATION, null, values);
        SQLiteManager.getInstance().closeDatabase();

        return newId;
    }

    public static List<ConfigurationEntity> getAll(){
        List<ConfigurationEntity> result = new ArrayList<>();

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_CONFIGURATION;
        Cursor cursor = db.rawQuery(selectQuery,  new String[]{});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ConfigurationEntity configuration = cursorToEnt(cursor);
                Log.d(TAG, configuration.getId()+"");
                result.add(configuration);
            } while (cursor.moveToNext());
        }

        cursor.close();
        SQLiteManager.getInstance().closeDatabase();
        Log.d(TAG, result.size() + "");
        return result;
    }

    public ConfigurationEntity findById(int id){

        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + TABLE_CONFIGURATION
                + " WHERE "+KEY_ID+" = ? ";

        Cursor cursor = db.rawQuery(selectQuery,  new String[]{String.valueOf(id)});
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            ConfigurationEntity configuration = new ConfigurationEntity();
            configuration.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            configuration.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
            SQLiteManager.getInstance().closeDatabase();

            return configuration;
        } else return null;

    }

    static ConfigurationEntity cursorToEnt(Cursor cursor){
        ConfigurationEntity configuration = new ConfigurationEntity();
        configuration.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        configuration.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        return configuration;
    }
}
