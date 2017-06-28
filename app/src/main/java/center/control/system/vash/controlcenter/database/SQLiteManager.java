package center.control.system.vash.controlcenter.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.configuration.CommandSQLite;
import center.control.system.vash.controlcenter.configuration.ConfigurationSQLite;
import center.control.system.vash.controlcenter.configuration.TriggerSQLite;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.script.ScriptSQLite;

/**
 * Created by Thuans on 4/27/2017.
 */

public class SQLiteManager {
    private Integer mOpenCounter = 0;

    private static SQLiteManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new SQLiteManager();
            instance.mDatabaseHelper = helper;
        }
    }

    public static synchronized SQLiteManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(SQLiteManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter+=1;
        if(mOpenCounter == 1) {
            // Opening new database only once. Else return already open
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter-=1;
        if(mOpenCounter == 0) {
            mDatabase.close();

        }
    }
    public void clearAllData(){
        SQLiteDatabase db = getInstance().openDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DeviceSQLite.TABLE_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + AreaSQLite.TABLE_AREA);
        db.execSQL("DROP TABLE IF EXISTS " + ScriptSQLite.TABLE_SCRIPT);
        db.execSQL("DROP TABLE IF EXISTS " + ScriptSQLite.TABLE_SCRIPT_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TermSQLite.TABLE_HUMAN_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + TermSQLite.TABLE_TARGET_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_FUNCTION_DETECT);
        db.execSQL("DROP TABLE IF EXISTS " + DetectIntentSQLite.TABLE_SOCIAL_DETECT);

        //Configuration
        db.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLite.TABLE_CONFIGURATION);
        db.execSQL("DROP TABLE IF EXISTS " + CommandSQLite.TABLE_COMMAND);
        db.execSQL("DROP TABLE IF EXISTS " + TriggerSQLite.TABLE_TRIGGER);

        db.execSQL(DeviceSQLite.createTable());
        db.execSQL(AreaSQLite.createTable());
        db.execSQL(ScriptSQLite.createScriptDeviceTable());
        db.execSQL(ScriptSQLite.createScriptTable());
        db.execSQL(TermSQLite.createHumanTerm());
        db.execSQL(TermSQLite.createTargetTerm());
        db.execSQL(DetectIntentSQLite.createFunction());
        db.execSQL(DetectIntentSQLite.createSocial());

        //Create configuration
        db.execSQL(ConfigurationSQLite.createConfiguration());
        db.execSQL(CommandSQLite.createCommand());
        db.execSQL(TriggerSQLite.createTriggerConfigution());

        closeDatabase();
    }
}
