package jp.co.vantageapps.famous.app;

import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Liuwei on 5/5/2017.
 */
public class DatabaseAdapter {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DatabaseAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DatabaseAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public int getFamousPeopleManCount() {
        try {
            String strSQLQuery = "SELECT * FROM famous WHERE gender = 'man'";

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);
            if (mCur != null) {
                return mCur.getCount();
            }

            return 0;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getFamousPeopleManCount >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public int getFamousPeopleWomanCount() {
        try {
            String strSQLQuery = "SELECT * FROM famous WHERE gender = 'woman'";

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);
            if (mCur != null) {
                return mCur.getCount();
            }

            return 0;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getFamousPeopleManCount >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getKey1Table()
    {
        try {
            String strSQLQuery = "SELECT * FROM key1_table";

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);

            return mCur;

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCategoryData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getKey2Table()
    {
        try {
            String strSQLQuery = "SELECT * FROM key2_table";

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);

            return mCur;

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCategoryData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getRareTable()
    {
        try {
            String strSQLQuery = "SELECT * FROM rare_table";

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);

            return mCur;

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCategoryData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getSimilarFamousPeople(String gender, String key1, String key2, String temprare)
    {
        try {
            String strSQLQuery = "SELECT * FROM famous WHERE 1 = 1";

            if(gender.equals("any") == false)
                strSQLQuery += " AND gender = '" + gender + "'";

            if(key1.equals("") == false)
                strSQLQuery += " AND key1 = '" + key1 + "'";

            if(key2.equals("") == false)
                strSQLQuery += " AND key2 = '" + key2 + "'";

            if(temprare.equals("") == false)
            {
                String rare = "";
                String[] rareArr = temprare.split(",");
                for(int i = 0; i < rareArr.length; i++)
                {
                    if (i > 0)
                        rare += ",";

                    rare += "'" + rareArr[i] + "'";
                }

                strSQLQuery += " AND rare IN (" + rare + ")";
            }

            Cursor mCur = mDb.rawQuery(strSQLQuery, null);
            int rescount = mCur.getCount();

            return mCur;

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getSimilarFamousPeople >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
}