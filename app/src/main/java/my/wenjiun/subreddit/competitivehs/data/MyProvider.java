package my.wenjiun.subreddit.competitivehs.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int ITEM = 100;
    static final int COMMENT = 200;

    private MyDbHelper mDbHelper;

    public MyProvider() {
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MyDbHelper(getContext());
        return true;
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authoriy = MyContract.CONTENT_AUTHORITY;

        matcher.addURI(authoriy, MyContract.PATH_ITEM, ITEM);
        matcher.addURI(authoriy, MyContract.PATH_COMMENT, COMMENT);
        return matcher;
    }


    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ITEM:
                retCursor = mDbHelper.getReadableDatabase().query(
                        MyContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case COMMENT:
                retCursor = mDbHelper.getReadableDatabase().query(
                        MyContract.CommentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch(match) {
            case ITEM:
                _id = db.insert(MyContract.ItemEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = MyContract.ItemEntry.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case COMMENT:
                _id = db.insert(MyContract.CommentEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = MyContract.CommentEntry.buildCommentUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(null == selection) selection = "1";

        switch (match) {
            case ITEM:
                rowsDeleted = db.delete(MyContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COMMENT:
                rowsDeleted = db.delete(MyContract.CommentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsDeleted;
    }

}
