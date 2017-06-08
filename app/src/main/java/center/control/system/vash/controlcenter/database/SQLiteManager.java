package center.control.system.vash.controlcenter.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
