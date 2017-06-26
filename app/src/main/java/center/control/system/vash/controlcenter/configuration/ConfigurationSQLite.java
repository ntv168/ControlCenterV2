package center.control.system.vash.controlcenter.configuration;

import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Thuans on 6/23/2017.
 */

public class ConfigurationSQLite {

    public static final String TABLE_CONFIGURATION = "configuration";
    public static final String TABLE_TRIGGER = "trigger_condition";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String KEY_GROUP_ID = ScriptSQLite.KEY_GROUP_ID;
    private static final String KEY_CONFIG_ID = ScriptSQLite.KEY_CONFIG_ID;
    private static final String KEY_DEVICE_ID = ScriptSQLite.KEY_DEVICE_ID;
    private static final String KEY_DEVICE_STATE = ScriptSQLite.KEY_DEVICE_STATE;

    public static String createConfiguration(){
        return "CREATE TABLE " + TABLE_CONFIGURATION  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT "+ ")";
    }
    public static String createTriggerConfigution(){
        return "CREATE TABLE " + TABLE_TRIGGER  + "("
                + KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT    ,"
                +  KEY_NAME+ "  TEXT "+ ")";
    }
}
