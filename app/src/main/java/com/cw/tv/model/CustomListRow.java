package com.cw.tv.model;


import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

/**
 *  Used with {@link CustomListRowPresenter}, it can display multiple rows.
 *  Use {@link #setNumRows(int)} method to specify the number of rows, default 1.
 */
public class CustomListRow extends ListRow {

    private static final String TAG = CustomListRow.class.getSimpleName();
    private int mNumRows = 1;

    public CustomListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public CustomListRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public CustomListRow(ObjectAdapter adapter) {
        super(adapter);
    }

    public void setNumRows(int numRows) {
        mNumRows = numRows;
    }

    public int getNumRows() {
        return mNumRows;
    }
    
}