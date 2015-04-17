package my.wenjiun.subreddit.competitivehs;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class CommentActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        if(savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(MainActivityFragment.TITLE,
                    getIntent().getStringExtra(MainActivityFragment.TITLE));
            arguments.putString(MainActivityFragment.HTML,
                    getIntent().getStringExtra(MainActivityFragment.HTML));
            arguments.putString(MainActivityFragment.PERMALINK,
                    getIntent().getStringExtra(MainActivityFragment.PERMALINK));
            arguments.putString(MainActivityFragment.URL,
                    getIntent().getStringExtra(MainActivityFragment.URL));
            arguments.putString(MainActivityFragment.ID,
                    getIntent().getStringExtra(MainActivityFragment.ID));
            arguments.putString(MainActivityFragment.CREATED,
                    getIntent().getStringExtra(MainActivityFragment.CREATED));
            arguments.putString(MainActivityFragment.AUTHOR,
                    getIntent().getStringExtra(MainActivityFragment.AUTHOR));

            CommentActivityFragment fragment = new CommentActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }

    }

}
