package my.wenjiun.subreddit.competitivehs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wenjiun on 05/04/2015.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "competitivehs.db";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MyContract.ItemEntry.TABLE_NAME + " (" +
                MyContract.ItemEntry._ID + " INTEGER PRIMARY KEY," +
                MyContract.ItemEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MyContract.ItemEntry.COLUMN_SELFTEXT + " TEXT," +
                MyContract.ItemEntry.COLUMN_SELFTEXT_HTML + " TEXT," +
                MyContract.ItemEntry.COLUMN_PERMALINK + " TEXT NOT NULL," +
                MyContract.ItemEntry.COLUMN_URL + " TEXT," +
                MyContract.ItemEntry.COLUMN_ID + " TEXT," +
                MyContract.ItemEntry.COLUMN_CREATED + " TEXT," +
                MyContract.ItemEntry.COLUMN_AUTHOR + " TEXT" +
                ");";
        final String SQL_CREATE_COMMENT_TABLE = "CREATE TABLE " + MyContract.CommentEntry.TABLE_NAME + " (" +
                MyContract.CommentEntry._ID + " INTEGER PRIMARY KEY," +
                MyContract.CommentEntry.COLUMN_PARENT + " TEXT NOT NULL," +
                MyContract.CommentEntry.COLUMN_BODY + " TEXT NOT NULL," +
                MyContract.CommentEntry.COLUMN_AUTHOR + " TEXT," +
                MyContract.CommentEntry.COLUMN_PERMALINK + " TEXT," +
                MyContract.CommentEntry.COLUMN_CREATED + " TEXT" +
                ");";
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyContract.ItemEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MyContract.CommentEntry.TABLE_NAME);
        onCreate(db);
    }
}
