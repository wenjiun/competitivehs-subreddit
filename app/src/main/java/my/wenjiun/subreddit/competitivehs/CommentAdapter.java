package my.wenjiun.subreddit.competitivehs;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wenjiun on 05/04/2015.
 */
public class CommentAdapter extends CursorAdapter {

    public static class ViewHolder {

        public final TextView commentView;
        private final TextView authorView;
        private final TextView dateView;

        public ViewHolder(View view) {
            commentView = (TextView)view.findViewById(R.id.text1);
            authorView = (TextView)view.findViewById(R.id.text2);
            authorView.setTextColor(Color.BLUE);
            dateView = (TextView)view.findViewById(R.id.text3);
        }
    }

    public CommentAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        String body = cursor.getString(CommentActivityFragment.COL_COMMENT_BODY);
        viewHolder.commentView.setText(Html.fromHtml(body));
        String author = cursor.getString(CommentActivityFragment.COL_COMMENT_AUTHOR);
        viewHolder.authorView.setText(author);
        String created = cursor.getString(CommentActivityFragment.COL_COMMENT_CREATED);
        Long timestamp = Long.parseLong(created.split("\\.")[0]) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = sdf.format(new Date(timestamp));
        viewHolder.dateView.setText(date);
    }

}
