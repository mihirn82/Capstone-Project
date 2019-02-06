package com.gmail.mihirn82.myteleprompter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ScriptsContract {

    public static final String CONTENT_AUTHORITY = "com.gmail.mihirn82.myteleprompter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCRIPTS = "scripts";

    public static abstract class ScriptsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SCRIPTS);

        public static final String TABLE_NAME = "scripts";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SCRIPT_NAME = "name";
        public static final String COLUMN_SCRIPT_DATA = "data";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCRIPTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCRIPTS;
    }
}
