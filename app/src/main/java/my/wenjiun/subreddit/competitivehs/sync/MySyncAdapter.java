package my.wenjiun.subreddit.competitivehs.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import my.wenjiun.subreddit.competitivehs.MainActivity;
import my.wenjiun.subreddit.competitivehs.R;
import my.wenjiun.subreddit.competitivehs.data.MyContract;

/**
 * Created by wenjiun on 16/04/2015.
 */
public class MySyncAdapter extends AbstractThreadedSyncAdapter {

    private Context mContext;

    private static final int NOTIFICATION_ID = 1001;

    public static final String BASE_URL_STRING = "http://www.reddit.com";
    public static final String URL_STRING = BASE_URL_STRING + "/r/CompetitiveHS/new/.json";
    public static final String BASE_COMMENT_URL_STRING = "http://www.reddit.com/r/CompetitiveHS/comments/";

    private static final String[] ITEM_COLUMNS = {
            MyContract.ItemEntry.COLUMN_ID,
            MyContract.ItemEntry.COLUMN_CREATED
    };

    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_CREATED = 1;

    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;
    private String latestId = "";
    private String latestCreated = "";

    private boolean isExist;
    private ArrayList<String> newItems;

    public MySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        //Log.d("CompetitiveHS", "Sync performed");
        try {

            fetchItems();

            notifyForNew();


            fetchComments();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fetchItems() throws IOException {
        URL url_reddit = new URL(URL_STRING);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url_reddit.openConnection();
        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            Cursor cursor = mContext.getContentResolver().query(MyContract.ItemEntry.CONTENT_URI,
                    ITEM_COLUMNS, null, null, MyContract.ItemEntry.COLUMN_CREATED + " DESC") ;
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                latestId = cursor.getString(COL_ITEM_ID);
                latestCreated = cursor.getString(COL_ITEM_CREATED);
                //Log.d("CompetitiveHS", "Latest ID: " + latestId);
                //Log.d("CompetitiveHS", "Latest created: " + latestCreated);
            }
            cursor.close();
            newItems = new ArrayList<String>();
            InputStream inputStream = httpURLConnection.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.beginObject();
            isExist = false;
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("data")) {
                    reader.beginObject();
                    while(reader.hasNext()) {
                        name = reader.nextName();
                        if(name.equals("children")) {
                            reader.beginArray();
                            while(reader.hasNext()) {
                                reader.beginObject();
                                while(reader.hasNext()) {
                                    name = reader.nextName();
                                    if(name.equals("data")) {
                                        ContentValues itemValues = new ContentValues();
                                        reader.beginObject();
                                        while(reader.hasNext()) {
                                            name = reader.nextName();
                                            if(name.equals("id"))  {
                                                String id = reader.nextString();
                                                Log.d("CompetitiveHS", id);
                                                if(id.equals(latestId)) {
                                                    isExist = true;
                                                    //Log.d("CompetitiveHS", id + " already exist");
                                                }
                                                itemValues.put(MyContract.ItemEntry.COLUMN_ID, id);
                                            } else if(name.equals("title")) {
                                                String title = reader.nextString();
                                                if(!isExist) {
                                                    newItems.add(title);
                                                }
                                                itemValues.put(MyContract.ItemEntry.COLUMN_TITLE, title);
                                            } else if(name.equals("selftext")) {
                                                String selftext = reader.nextString();
                                                itemValues.put(MyContract.ItemEntry.COLUMN_SELFTEXT, selftext);
                                            } else if(name.equals("selftext_html")) {
                                                if(reader.peek() == JsonToken.NULL) {
                                                    reader.skipValue();
                                                } else {
                                                    String selftext_html = reader.nextString();
                                                    itemValues.put(MyContract.ItemEntry.COLUMN_SELFTEXT_HTML, selftext_html);
                                                }
                                            } else if(name.equals("permalink")) {
                                                String permalink = reader.nextString();
                                                itemValues.put(MyContract.ItemEntry.COLUMN_PERMALINK, permalink);
                                            } else if(name.equals("url")) {
                                                String url = reader.nextString();
                                                itemValues.put(MyContract.ItemEntry.COLUMN_URL, url);
                                            } else if(name.equals("created_utc")) {
                                                String created = reader.nextString();
                                                if(!latestCreated.equals("")) {
                                                    if(Long.parseLong(created.split("\\.")[0]) < Long.parseLong(latestCreated.split("\\.")[0])) {
                                                        isExist = true;
                                                        //Log.d("CompetitiveHS", "Time is earlier than latest");
                                                    }
                                                }
                                                itemValues.put(MyContract.ItemEntry.COLUMN_CREATED, created);
                                            } else if(name.equals("author")) {
                                                String author = reader.nextString();
                                                itemValues.put(MyContract.ItemEntry.COLUMN_AUTHOR, author);
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                        reader.endObject();
                                        if(!isExist) {
                                            mContext.getContentResolver().insert(MyContract.ItemEntry.CONTENT_URI, itemValues);
                                            //Log.d("CompetitiveHS", "inserted");
                                        }

                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            }
                            reader.endArray();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            reader.close();
        }
    }

    private void fetchComments() throws IOException {
        mContext.getContentResolver().delete(MyContract.CommentEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver()
                .query(MyContract.ItemEntry.CONTENT_URI, ITEM_COLUMNS, null, null, MyContract.ItemEntry.COLUMN_CREATED + " DESC");
        cursor.moveToFirst();
        //Log.d("CompetitiveHS", "count: " + cursor.getCount());
        while(!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            URL url_reddit_comment = new URL(BASE_COMMENT_URL_STRING + id + "/.json");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url_reddit_comment.openConnection();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = httpURLConnection.getInputStream();
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("data")) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                name = reader.nextName();
                                if (name.equals("children")) {
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        reader.beginObject();
                                        while (reader.hasNext()) {
                                            name = reader.nextName();
                                            if (name.equals("kind")) {
                                                String kind = reader.nextString();
                                            } else if (name.equals("data")) {
                                                boolean isComment = false;
                                                ContentValues commentValues = new ContentValues();
                                                reader.beginObject();
                                                while (reader.hasNext()) {
                                                    name = reader.nextName();
                                                    if (name.equals("body")) {
                                                        isComment = true;
                                                        String body = reader.nextString();
                                                        commentValues.put(MyContract.CommentEntry.COLUMN_BODY, body);
                                                    } else if(name.equals("author")) {
                                                        String author = reader.nextString();
                                                        commentValues.put(MyContract.CommentEntry.COLUMN_AUTHOR, author);
                                                    } else if(name.equals("id")) {
                                                        String permalink = reader.nextString();
                                                        commentValues.put(MyContract.CommentEntry.COLUMN_PERMALINK, permalink);
                                                    } else if(name.equals("created_utc")) {
                                                        String created = reader.nextString();
                                                        commentValues.put(MyContract.CommentEntry.COLUMN_CREATED, created);
                                                    } else {
                                                        reader.skipValue();
                                                    }
                                                }
                                                reader.endObject();
                                                if(isComment) {
                                                    commentValues.put(MyContract.CommentEntry.COLUMN_PARENT, id);
                                                    mContext.getContentResolver().insert(MyContract.CommentEntry.CONTENT_URI, commentValues);
                                                }
                                            }
                                        }
                                        reader.endObject();
                                    }
                                    reader.endArray();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
                reader.close();
            }
            cursor.moveToNext();
        }
    }

    private void notifyForNew() {
        boolean displayNotification = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(MainActivity.DISPLAY_NOTIFICATION, true);
        if(displayNotification) {
            if(newItems.size() > 0) {
                // NotificationCompatBuilder is a very convenient way to build backward-compatible
                // notifications.  Just throw in some data.
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.ic_notify)
                                .setContentTitle(mContext.getString(R.string.app_name));

                if(newItems.size() > 1) {
                    mBuilder.setContentText(newItems.size() + " new CompetitiveHS Subreddit posts");
                } else {
                    mBuilder.setContentText(newItems.get(0));
                }

                // Make something interesting happen when the user clicks on the notification.
                // In this case, opening the app is sufficient.
                Intent resultIntent = new Intent(mContext, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }
    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
