package tony.beveragesmodulation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseDAO {
    private static final String TAG = "DatabaseDAO";
    private Context cxt;
    private SQLiteDatabase DB;
    private static final String srcDBname = "beverage.db"; //來源DB名稱
    private static final String dstDBname = "main.DB";     //儲存DB名稱

    public DatabaseDAO(Context context) {
        Log.i(TAG,"-----Constructor-----");
        this.cxt = context;
    }

    //取得檔案大小(File)
    private long getFileSize(File f)  {
        long s = 0;
        if(f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                s = fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }
    //取得檔案大小(InputStream)
    private long getFileSize(InputStream is) {
        long s = 0;
        try {
            s = is.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    //將assets資料夾內的db複製到dbfile位置
    public void copyToDatabasesFile() {
        Log.i(TAG,"-----copyToDatabasesFile-----");
        //src DB file size
        long srcDBFileSize = 0;
        try {
            srcDBFileSize = getFileSize(cxt.getAssets().open(srcDBname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //dst DB file size
        String sqLiteDBPath = getSQLiteDBPath();
        File dstDBFile = new File(sqLiteDBPath);
        long dstDBFileSize = getFileSize(dstDBFile);

        // 如果兩個檔案大小不相同，就要複製一份到databases底下儲存
        if (srcDBFileSize != dstDBFileSize) {
            Log.i(TAG, "----copy----");

            try {
                byte[] buffer = new byte[8192];
                InputStream fis = cxt.getAssets().open(srcDBname);
                FileOutputStream fos = new FileOutputStream(sqLiteDBPath);
                BufferedOutputStream dest = new BufferedOutputStream(fos, 8192);
                int count;
                while ((count = fis.read(buffer, 0, 8192)) >= 0)
                    dest.write(buffer, 0, count);
                dest.flush();
                dest.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { //如果大小一樣就不必複製
            Log.i(TAG, "----no copy----");
        }
    }

    //取得dbfile位置
    private String getSQLiteDBPath() {
        Log.i(TAG,"-----MyDataBasePath-----");
        String dbdir="/data/data/" + cxt.getApplicationContext().getPackageName()+"/databases";
        File myDataPathdbdir = new File(dbdir);
        if(!myDataPathdbdir.exists())
            (new File(dbdir)).mkdirs();
        return dbdir + dstDBname;
    }

    //開啟DB
    public void OpenDB() {
        Log.i(TAG,"OpenDB");
        DB = SQLiteDatabase.openOrCreateDatabase(getSQLiteDBPath(),null);
    }

    //關閉DB
    public void CloseDB() {
        Log.i(TAG,"CloseDB");
        DB.close();
    }

    //取得DB
    public SQLiteDatabase getDB() {
        return DB;
    }
}
