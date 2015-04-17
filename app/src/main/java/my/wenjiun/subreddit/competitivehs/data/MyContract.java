package my.wenjiun.subreddit.competitivehs.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wenjiun on 05/04/2015.
 */
public class MyContract {

    public static final String CONTENT_AUTHORITY = "my.wenjiun.subreddit.competitivehs";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEM = "item";
    public static final String PATH_COMMENT = "comment";

    public static final class CommentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();

        public static final String TABLE_NAME = "comment";

        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_PARENT = "parent";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_PERMALINK = "permalink";
        public static final String COLUMN_CREATED = "created_utc";

        public static Uri buildCommentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String TABLE_NAME = "item";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SELFTEXT = "selftext";
        public static final String COLUMN_SELFTEXT_HTML = "selftext_html";
        public static final String COLUMN_PERMALINK = "permalink";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CREATED = "created_utc";
        public static final String COLUMN_AUTHOR = "author";

        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
