package org.resoft.bitcoin.helpers;

/**
 * Created by onuragtas on 18.07.2017.
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import org.resoft.bitcoin.models.Data;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bitcoin.db";
    public static final String BITCOIN_TABLE_NAME = "bitcoin";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_DATE = "date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table IF NOT EXISTS bitcoin (id integer PRIMARY KEY AUTOINCREMENT, data REAL, date DATETIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS bitcoin");
        onCreate(db);
    }


    public boolean insertBitcoin(Double data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATA, data);
        contentValues.put(COLUMN_DATE, new Date().getTime());
        db.insert(BITCOIN_TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<Data> getData(){
        ArrayList<Data> datas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date,COUNT(id),AVG(data), strftime('%Y-%m-%d', date) dmy FROM bitcoin GROUP BY dmy ORDER BY date DESC LIMIT 60;",null);
        if(cursor.moveToFirst()){
            do{
                double data = cursor.getDouble(2);
                Date date = new Date(cursor.getLong(0));
                String dates = new  SimpleDateFormat("yyyy-MM-dd").format(date);
                datas.add(new Data(data, dates));
            }while(cursor.moveToNext());
        }

        return datas;
    }
}