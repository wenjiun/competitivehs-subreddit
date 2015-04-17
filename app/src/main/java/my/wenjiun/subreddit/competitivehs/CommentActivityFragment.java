package my.wenjiun.subreddit.competitivehs;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;

import my.wenjiun.subreddit.competitivehs.data.MyContract;
import my.wenjiun.subreddit.competitivehs.sync.MySyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class CommentActivityFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int COMMENT_LOADER = 1;

    private String html_string;
    private String title;
    private String permalink;
    private String url;
    private String id;
    private String created;
    private String author;

    private ShareActionProvider mShareActionProvider;
    private WebView htmlView;
    private CommentAdapter mCommentAdapter;


    private static final String[] COMMENT_COLUMNS = {
            MyContract.CommentEntry.TABLE_NAME + "." + MyContract.CommentEntry._ID,
            MyContract.CommentEntry.COLUMN_BODY,
            MyContract.CommentEntry.COLUMN_PARENT,
            MyContract.CommentEntry.COLUMN_AUTHOR,
            MyContract.CommentEntry.COLUMN_PERMALINK,
            MyContract.CommentEntry.COLUMN_CREATED
    };

    static final int COL_COMMENT_ID = 0;
    static final int COL_COMMENT_BODY = 1;
    static final int COL_COMMENT_PARENT = 2;
    static final int COL_COMMENT_AUTHOR = 3;
    static final int COL_COMMENT_PERMALINK = 4;
    static final int COL_COMMENT_CREATED = 5;

    public CommentActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null) {
            title = arguments.getString(MainActivityFragment.TITLE);
            html_string = arguments.getString(MainActivityFragment.HTML);
            permalink = arguments.getString(MainActivityFragment.PERMALINK);
            url = arguments.getString(MainActivityFragment.URL);
            id = arguments.getString(MainActivityFragment.ID);
            created = arguments.getString(MainActivityFragment.CREATED);
            author = arguments.getString(MainActivityFragment.AUTHOR);
        }
        if(html_string == null) {
            html_string = "";
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        htmlView = new WebView(getActivity());
        Long timestamp = Long.parseLong(created.split("\\.")[0]) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = sdf.format(new Date(timestamp));
        String content;
        if(html_string.equals("")) {
            content = "<h3>" + title + "</h3><a href=\"" + url + "\"/>"  + url + "</a>";
        } else {
            content = "<h3>" + title + "</h3><p><small>Posted by <font color=\"blue\">" + author + "</font> at " + date + "</small></p>" + Html.fromHtml(html_string).toString();
        }
        htmlView.loadData(content, "text/html; charset=utf-8", "UTF-8");


        mCommentAdapter = new CommentAdapter(getActivity(), null, 0);
        getListView().setDivider(new ColorDrawable(Color.LTGRAY));
        getListView().setDividerHeight(8);
        getListView().addHeaderView(htmlView);
        setListAdapter(mCommentAdapter);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivityFragment.ID, id);
        getLoaderManager().initLoader(COMMENT_LOADER, bundle, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_comment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider((menuItem));
        if(title!=null) {
            mShareActionProvider.setShareIntent(createShareItemIntent());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = (Cursor)l.getItemAtPosition(position);
        if(cursor != null) {
            Uri uri = Uri.parse(MySyncAdapter.BASE_URL_STRING + permalink
                    + cursor.getString(COL_COMMENT_PERMALINK) + "/.compact");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }
    }

    @SuppressWarnings("deprecation")
    private Intent createShareItemIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + MySyncAdapter.BASE_URL_STRING + permalink );
        return shareIntent;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri commentUri = MyContract.CommentEntry.CONTENT_URI;
        Log.d(getActivity().getPackageName(), args.getString(MainActivityFragment.ID));
        return new CursorLoader(getActivity(),
                commentUri,
                COMMENT_COLUMNS,
                MyContract.CommentEntry.COLUMN_PARENT+"=?",
                new String[]{args.getString(MainActivityFragment.ID)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCommentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCommentAdapter.swapCursor(null);
    }
}
