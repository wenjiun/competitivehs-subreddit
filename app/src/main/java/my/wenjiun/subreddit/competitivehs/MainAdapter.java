package my.wenjiun.subreddit.competitivehs;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wenjiun on 05/04/2015.
 */
public class MainAdapter extends CursorAdapter {


    public static class ViewHolder {

        private final TextView titleView;
        private final TextView selftextView;
        private final TextView authorView;
        private final TextView dateView;

        public ViewHolder(View view) {
            titleView = (TextView)view.findViewById(R.id.text1);
            selftextView = (TextView)view.findViewById(R.id.text2);
            selftextView.setMaxLines(3);
            authorView = (TextView)view.findViewById(R.id.text3);
            authorView.setTextColor(Color.BLUE);
            dateView = (TextView)view.findViewById(R.id.text4);

        }
    }

    public MainAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        String title = cursor.getString(MainActivityFragment.COL_ITEM_TITLE);
        viewHolder.titleView.setText(title);
        String author = cursor.getString(MainActivityFragment.COL_ITEM_AUTHOR);
        viewHolder.authorView.setText(author);
        String created = cursor.getString(MainActivityFragment.COL_ITEM_CREATED);
        Long timestamp = Long.parseLong(created.split("\\.")[0]) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = sdf.format(new Date(timestamp));

        viewHolder.dateView.setText(date);
        String selftext = cursor.getString(MainActivityFragment.COL_ITEM_SELFTEXT);
        if(selftext.equals("")) {
            String url = cursor.getString(MainActivityFragment.COL_ITEM_URL);
            viewHolder.selftextView.setTextColor(Color.BLUE);
            viewHolder.selftextView.setText(url);
        } else {
            viewHolder.selftextView.setTextColor(Color.BLACK);
            viewHolder.selftextView.setText(selftext);
        }
    }
}
