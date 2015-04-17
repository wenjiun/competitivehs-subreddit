package my.wenjiun.subreddit.competitivehs;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import my.wenjiun.subreddit.competitivehs.sync.MySyncAdapter;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    public static final String DISPLAY_NOTIFICATION = "display_notification";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySyncAdapter.syncImmediately(this);
        if(findViewById(R.id.detail_container)!=null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        MySyncAdapter.initializeSyncAdapter(this);


    }

    @Override
    protected void onResume() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(DISPLAY_NOTIFICATION, false)
                .apply();
        super.onResume();

    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(DISPLAY_NOTIFICATION, true)
                .apply();
        super.onPause();
    }

    @Override
    public void onItemSelected(Bundle bundle, boolean isFirst) {
        String title = bundle.getString(MainActivityFragment.TITLE);
        String selftext_html = bundle.getString(MainActivityFragment.HTML);
        String permalink = bundle.getString(MainActivityFragment.PERMALINK);
        String url = bundle.getString(MainActivityFragment.URL);
        String id_parent = bundle.getString(MainActivityFragment.ID);
        String created = bundle.getString(MainActivityFragment.CREATED);
        String author = bundle.getString(MainActivityFragment.AUTHOR);
        if(mTwoPane) {
            CommentActivityFragment fragment = new CommentActivityFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            if(!isFirst) {
                Intent i = new Intent(this, CommentActivity.class);
                i.putExtra(MainActivityFragment.TITLE, title);
                i.putExtra(MainActivityFragment.HTML, selftext_html);
                i.putExtra(MainActivityFragment.PERMALINK, permalink);
                i.putExtra(MainActivityFragment.URL, url);
                i.putExtra(MainActivityFragment.ID, id_parent);
                i.putExtra(MainActivityFragment.CREATED, created);
                i.putExtra(MainActivityFragment.AUTHOR, author);
                startActivity(i);
            }
        }
    }
}
