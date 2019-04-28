package com.cw.tv.ui.presenter;


import com.cw.tv.model.CustomListRow;

import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.RowPresenter;

/**
 * Custom {@link #ListRowPresenter}, it can have multiple rows.
 *
 * Detail: Internally it is changing {@link ListRowPresenter.ViewHolder}'s
 * {@link ListRowPresenter.ViewHolder#mGridView} to set number of rows.
 */
public class CustomListRowPresenter extends ListRowPresenter {

    private static final String TAG = CustomListRowPresenter.class.getSimpleName();

    public CustomListRowPresenter() {
        super();
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        /* This two line codes changes the number of rows of ListRow */
        int numRows = ((CustomListRow) item).getNumRows();
        ((ListRowPresenter.ViewHolder) holder).getGridView().setNumRows(numRows);

        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);

        /* Disable Shadow */
        setShadowEnabled(false);
    }
}