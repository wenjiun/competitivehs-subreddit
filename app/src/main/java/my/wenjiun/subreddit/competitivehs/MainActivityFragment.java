package my.wenjiun.subreddit.competitivehs;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import my.wenjiun.subreddit.competitivehs.data.MyContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String HTML = "html";
    public static final String TITLE = "title";
    public static final String PERMALINK = "permalink";
    public static final String URL = "url";
    public static final String ID = "id";
    public static final String CREATED = "created";
    public static final String AUTHOR = "author";

    private MainAdapter mMainAdapter;

    private static final int ITEM_LOADER = 0;

    private static final String[] ITEM_COLUMNS = {

            MyContract.ItemEntry.TABLE_NAME + "." + MyContract.ItemEntry._ID,
            MyContract.ItemEntry.COLUMN_TITLE,
            MyContract.ItemEntry.COLUMN_SELFTEXT,
            MyContract.ItemEntry.COLUMN_SELFTEXT_HTML,
            MyContract.ItemEntry.COLUMN_PERMALINK,
            MyContract.ItemEntry.COLUMN_URL,
            MyContract.ItemEntry.COLUMN_ID,
            MyContract.ItemEntry.COLUMN_CREATED,
            MyContract.ItemEntry.COLUMN_AUTHOR
    };

    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_TITLE = 1;
    static final int COL_ITEM_SELFTEXT = 2;
    static final int COL_ITEM_SELFTEXT_HTML = 3;
    static final int COL_ITEM_PERMALINK = 4;
    static final int COL_ITEM_URL = 5;
    static final int COL_ITEM_PARENT_ID = 6;
    static final int COL_ITEM_CREATED = 7;
    static final int COL_ITEM_AUTHOR = 8;

    public interface Callback {

        public void onItemSelected(Bundle bundle, boolean isFirst);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainAdapter = new MainAdapter(getActivity(), null, 0);
        setListAdapter(mMainAdapter);
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = (Cursor)l.getItemAtPosition(position);
        if(cursor!=null) {
            Bundle bundle = new Bundle();
            String title = cursor.getString(COL_ITEM_TITLE);
            String selftext_html = cursor.getString(COL_ITEM_SELFTEXT_HTML);
            String permalink = cursor.getString(COL_ITEM_PERMALINK);
            String url = cursor.getString(COL_ITEM_URL);
            String id_parent = cursor.getString(COL_ITEM_PARENT_ID);
            String created = cursor.getString(COL_ITEM_CREATED);
            String author = cursor.getString(COL_ITEM_AUTHOR);
            bundle.putString(TITLE, title);
            bundle.putString(HTML, selftext_html);
            bundle.putString(PERMALINK, permalink);
            bundle.putString(URL, url);
            bundle.putString(ID, id_parent);
            bundle.putString(CREATED, created);
            bundle.putString(AUTHOR, author);
            ((Callback)getActivity()).onItemSelected(bundle, false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri itemUri = MyContract.ItemEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                itemUri,
                ITEM_COLUMNS,
                null,
                null,
                MyContract.ItemEntry.COLUMN_CREATED + " DESC"
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (data != null && data.getCount() > 0) {
                    data.moveToFirst();
                    Bundle bundle = new Bundle();
                    String title = data.getString(COL_ITEM_TITLE);
                    String selftext_html = data.getString(COL_ITEM_SELFTEXT_HTML);
                    String permalink = data.getString(COL_ITEM_PERMALINK);
                    String url = data.getString(COL_ITEM_URL);
                    String id_parent = data.getString(COL_ITEM_PARENT_ID);
                    String created = data.getString(COL_ITEM_CREATED);
                    String author = data.getString(COL_ITEM_AUTHOR);
                    bundle.putString(TITLE, title);
                    bundle.putString(HTML, selftext_html);
                    bundle.putString(PERMALINK, permalink);
                    bundle.putString(URL, url);
                    bundle.putString(ID, id_parent);
                    bundle.putString(CREATED, created);
                    bundle.putString(AUTHOR, author);
                    ((Callback) getActivity()).onItemSelected(bundle, true);

                }

            }
        });
        mMainAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }
}
