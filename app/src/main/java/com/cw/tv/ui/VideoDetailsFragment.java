package com.cw.tv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import com.cw.tv.R;
import com.cw.tv.common.Utils;
import com.cw.tv.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

/**
 * Created by corochann on 6/7/2015.
 */
public class VideoDetailsFragment extends DetailsFragment {

	private static final String TAG = VideoDetailsFragment.class.getSimpleName();

	private static final int ACTION_PLAY_VIDEO = 1;
	private static final int DETAIL_THUMB_WIDTH = 274;
	private static final int DETAIL_THUMB_HEIGHT = 274;


	private static final String MOVIE = "Movie";

	private CustomFullWidthDetailsOverviewRowPresenter mFwdorPresenter;
	private CustomDetailsOverviewRowPresenter mDorPresenter;
	private PicassoBackgroundManager mPicassoBackgroundManager;

	private Movie mSelectedMovie;
	private DetailsRowBuilderTask mDetailsRowBuilderTask;
	public static final String CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER = "DetailsOverviewRowPresenter";

	private static final int FULL_WIDTH_DETAIL_THUMB_WIDTH = 220;
	private static final int FULL_WIDTH_DETAIL_THUMB_HEIGHT = 120;
	private LinkedHashMap<String, List<Movie>> mVideoLists = null;
	private ClassPresenterSelector mClassPresenterSelector;
	private ListRow mRelatedVideoRow = null;
	private ArrayObjectAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
		mDorPresenter = new CustomDetailsOverviewRowPresenter(new DetailsDescriptionPresenter(), getActivity());

		mPicassoBackgroundManager = new PicassoBackgroundManager(getActivity());
		mSelectedMovie = getActivity().getIntent().getParcelableExtra(DetailsActivity.MOVIE);

		mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);

		setOnItemViewClickedListener(new ItemViewClickedListener());

		mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getCardImageUrl());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mClassPresenterSelector = new ClassPresenterSelector();
		Log.v(TAG, "mFwdorPresenter.getInitialState: " + mFwdorPresenter.getInitialState());
		if(mSelectedMovie.getCategory().equals(CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER)) {
			/* If category name is "DetailsOverviewRowPresenter", show DetailsOverviewRowPresenter for demo purpose (this class is deprecated from API level 22) */
			mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
		} else {
			/* Default behavior, show FullWidthDetailsOverviewRowPresenter */
			mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
		}
		mClassPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

		mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
		setAdapter(mAdapter);
	}

	@Override
	public void onStop() {
        mDetailsRowBuilderTask.cancel(true);
		super.onStop();
	}

	private final class ItemViewClickedListener implements OnItemViewClickedListener {
		@Override
		public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
		                          RowPresenter.ViewHolder rowViewHolder, Row row) {

			if (item instanceof Movie) {
				Movie movie = (Movie) item;
				Log.d(TAG, "Item: " + item.toString());
				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				intent.putExtra(DetailsActivity.MOVIE, movie);

				Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
						getActivity(),
						((ImageCardView) itemViewHolder.view).getMainImageView(),
						DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
				getActivity().startActivity(intent, bundle);
			}
		}
	}

	private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
		@Override
		protected DetailsOverviewRow doInBackground(Movie... params) {

			int width, height;
			if(mSelectedMovie.getCategory().equals(CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER)) {
				/* If category name is "DetailsOverviewRowPresenter", show DetailsOverviewRowPresenter for demo purpose (this class is deprecated from API level 22) */
				width = DETAIL_THUMB_WIDTH;
				height = DETAIL_THUMB_HEIGHT;
			} else {
				/* Default behavior, show FullWidthDetailsOverviewRowPresenter */
				width = FULL_WIDTH_DETAIL_THUMB_WIDTH;
				height = FULL_WIDTH_DETAIL_THUMB_HEIGHT;
			}


			DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
			try {
				Bitmap poster = Picasso.with(getActivity())
						.load(mSelectedMovie.getCardImageUrl())
						.resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), width),
								Utils.convertDpToPixel(getActivity().getApplicationContext(), height))
						.centerCrop()
						.get();
				row.setImageBitmap(getActivity(), poster);

				mVideoLists = VideoProvider.buildMedia(getActivity());
			} catch (IOException e) {
				Log.w(TAG, e.toString());
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}


			return row;
		}

		@Override
		protected void onPostExecute(DetailsOverviewRow row) {
			/* 1st row: DetailsOverviewRow */
//			SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
//			for (int i = 0; i<10; i++){
//				sparseArrayObjectAdapter.set(i, new Action(i, "label1", "label2"));
//			}
//			row.setActionsAdapter(sparseArrayObjectAdapter);
			SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
			sparseArrayObjectAdapter.set(0, new Action(ACTION_PLAY_VIDEO, "Play Video"));
			sparseArrayObjectAdapter.set(1, new Action(1, "Action 2", "label"));
			sparseArrayObjectAdapter.set(2, new Action(2, "Action 3", "label"));

			row.setActionsAdapter(sparseArrayObjectAdapter);

			mFwdorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
				@Override
				public void onActionClicked(Action action) {
					if (action.getId() == ACTION_PLAY_VIDEO) {
						Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
						intent.putExtra("Movie", mSelectedMovie);
						intent.putExtra("shouldStart", true);
						startActivity(intent);
					}
				}
			});
			mFwdorPresenter.setOnActionClickedListener(new DetailsOverviewRowActionClickedListener());
			mDorPresenter.setOnActionClickedListener(new DetailsOverviewRowActionClickedListener());

			/* 2nd row: ListRow CardPresenter */

			if (mVideoLists == null) {
				// Error occured while fetching videos
				Log.i(TAG, "mVideoLists is null, skip creating mRelatedVideoRow");
			} else {
				CardPresenter cardPresenter = new CardPresenter();

				for (Map.Entry<String, List<Movie>> entry : mVideoLists.entrySet()) {
					// Find only same category
					String categoryName = entry.getKey();
					if(!categoryName.equals(mSelectedMovie.getCategory())) {
						continue;
					}

					ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
					List<Movie> list = entry.getValue();

					for (int j = 0; j < list.size(); j++) {
						cardRowAdapter.add(list.get(j));
					}
					//HeaderItem header = new HeaderItem(index, entry.getKey());
					HeaderItem header = new HeaderItem(0, "Related Videos");
					mRelatedVideoRow = new ListRow(header, cardRowAdapter);
				}
			}

			/* 2nd row: ListRow */
/*            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

            ArrayList<Movie> mItems = MovieProvider.getMovieItems();
            for (Movie movie : mItems) {
                listRowAdapter.add(movie);
            }
            HeaderItem headerItem = new HeaderItem(0, "Related Videos");
*/

			mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
			/* 1st row */
			mAdapter.add(row);

			/* 2nd row */
			if(mRelatedVideoRow != null){
				mAdapter.add(mRelatedVideoRow);
			}
			//mAdapter.add(new ListRow(headerItem, listRowAdapter));

			/* 3rd row */
			//adapter.add(new ListRow(headerItem, listRowAdapter));
			setAdapter(mAdapter);

		}
	}

	public class DetailsOverviewRowActionClickedListener implements OnActionClickedListener {
		@Override
		public void onActionClicked(Action action) {
			if (action.getId() == ACTION_PLAY_VIDEO) {
				Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
				intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
				intent.putExtra(getResources().getString(R.string.should_start), true);
				startActivity(intent);
			}
		}
	}
}