package com.cw.tv.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.tv.R;
import com.cw.tv.data.MovieProvider;
import com.cw.tv.data.VideoItemLoader;
import com.cw.tv.model.CustomListRow;
import com.cw.tv.model.IconHeaderItem;
import com.cw.tv.model.Movie;
import com.cw.tv.recommendation.RecommendationFactory;
import com.cw.tv.ui.background.PicassoBackgroundManager;
import com.cw.tv.ui.presenter.CardPresenter;
import com.cw.tv.ui.presenter.CustomListRowPresenter;
import com.cw.tv.ui.presenter.IconHeaderItemPresenter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class MainFragment extends BrowseSupportFragment {
	private static final String TAG = MainFragment.class.getSimpleName();
	private ArrayObjectAdapter mRowsAdapter;
	private static final int GRID_ITEM_WIDTH = 300;
	private static final int GRID_ITEM_HEIGHT = 200;
//	private SimpleBackgroundManager simpleBackgroundManager = null;
	private PicassoBackgroundManager picassoBackgroundManager = null;
	private ArrayList<Movie> mItems = MovieProvider.getMovieItems();
	private static final String GRID_STRING_ERROR_FRAGMENT = "ErrorFragment";
	private static final String GRID_STRING_GUIDED_STEP_FRAGMENT = "GuidedStepFragment";
	private static final String GRID_STRING_VERTICAL_GRID_FRAGMENT = "VerticalGridFragment";
	private static final String GRID_STRING_RECOMMENDATION = "Recommendation";
	private static final String GRID_STRING_SPINNER = "Spinner";

	private static int recommendationCounter = 0;
	private static final int VIDEO_ITEM_LOADER_ID = 1;
	private Context context;
	private ArrayList<CustomListRow> mVideoListRowArray;
	private CustomListRow mGridItemListRow;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		setupUIElements();

		/* Set up rows with light data. done in main thread. */
		loadRows();
		setRows();

		getLoaderManager().initLoader(VIDEO_ITEM_LOADER_ID, null, new MainFragmentLoaderCallbacks());

		setupEventListeners();

		//		simpleBackgroundManager = new SimpleBackgroundManager(getActivity());
		picassoBackgroundManager = new PicassoBackgroundManager(getActivity());
		context = getActivity();
	}

	/**
	 * Updates UI after loading Row done.
	 */
	private void setRows() {
		mRowsAdapter = new ArrayObjectAdapter(new CustomListRowPresenter()); // Initialize

		if(mVideoListRowArray != null) {
			for (CustomListRow videoListRow : mVideoListRowArray) {
				mRowsAdapter.add(videoListRow);
			}
		}
		if(mGridItemListRow != null) {
			mRowsAdapter.add(mGridItemListRow);
		}

		/* Set */
		setAdapter(mRowsAdapter);
	}

	private class MainFragmentLoaderCallbacks implements LoaderManager.LoaderCallbacks<LinkedHashMap<String, List<Movie>>> {
		@Override
		public Loader<LinkedHashMap<String, List<Movie>>> onCreateLoader(int id, Bundle args) {
			/* Create new Loader */
			Log.d(TAG, "onCreateLoader");
			if(id == VIDEO_ITEM_LOADER_ID) {
				Log.d(TAG, "create VideoItemLoader");
				return new VideoItemLoader(getActivity());
			}
			return null;
		}

		@Override
		public void onLoadFinished(Loader<LinkedHashMap<String, List<Movie>>> loader, LinkedHashMap<String, List<Movie>> data) {
			Log.d(TAG, "VideoItemLoader: onLoadFinished");
			/* Loader data has prepared. Start updating UI here */
			switch (loader.getId()) {
				case VIDEO_ITEM_LOADER_ID:
					Log.d(TAG, "VideoLists UI update");

					/* Hold data reference to use it for recommendation */
					mItems = new ArrayList<Movie>();

					mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

					int index = 0;
					mVideoListRowArray = new ArrayList<>();

					/* CardPresenter */
					CardPresenter cardPresenter = new CardPresenter();

					if (null != data) {
						for (Map.Entry<String, List<Movie>> entry : data.entrySet()) {
							ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
							List<Movie> list = entry.getValue();

							for (int j = 0; j < list.size(); j++) {
								Movie movie = list.get(j);
								cardRowAdapter.add(movie);
								mItems.add(movie);           // Add movie reference for recommendation purpose.
							}

							/* loadRows: videoListRow - CardPresenter */
							IconHeaderItem header = new IconHeaderItem(index, entry.getKey(), R.drawable.ic_play_arrow_white_48dp);
							index++;
							CustomListRow videoListRow = new CustomListRow(header, cardRowAdapter);
							videoListRow.setNumRows(3); // multiple rows
							mVideoListRowArray.add(videoListRow);

						}
					} else {
						Log.e(TAG, "An error occurred fetching videos");
					}

					/* Set */
					setRows();
			}
		}

		@Override
		public void onLoaderReset(Loader<LinkedHashMap<String, List<Movie>>> loader) {
			Log.d(TAG, "VideoItemLoader: onLoadReset");
			/* When it is called, Loader data is now unavailable due to some reason. */
		}
	}

	private void setupUIElements() {
		 setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.lb_ic_launcher));
//		setTitle("Hello Android TV!"); // Badge, when set, takes precedent
		// over title
		setHeadersState(HEADERS_ENABLED);
		setHeadersTransitionOnBackEnabled(true);

		// set fastLane (or headers) background color
		setBrandColor(getResources().getColor(R.color.fastlane_background));
		// set search icon color
		setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

		setHeaderPresenterSelector(new PresenterSelector() {
			@Override
			public Presenter getPresenter(Object o) {
				return new IconHeaderItemPresenter();
			}
		});
	}

	private void loadRows() {
		mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

		/* GridItemPresenter */
		IconHeaderItem gridItemPresenterHeader = new IconHeaderItem(0, "GridItemPresenter", R.drawable.ic_add_white_48dp);

		GridItemPresenter mGridPresenter = new GridItemPresenter();
		ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
		gridRowAdapter.add(GRID_STRING_ERROR_FRAGMENT);
		gridRowAdapter.add(GRID_STRING_GUIDED_STEP_FRAGMENT);
		gridRowAdapter.add(GRID_STRING_RECOMMENDATION);
		gridRowAdapter.add(GRID_STRING_VERTICAL_GRID_FRAGMENT);
		gridRowAdapter.add(GRID_STRING_SPINNER);
		mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

		mGridItemListRow = new CustomListRow(gridItemPresenterHeader, gridRowAdapter);
	}

	private void setupEventListeners() {
		setOnItemViewSelectedListener(new ItemViewSelectedListener());
		setOnItemViewClickedListener(new ItemViewClickedListener());
		// Existence of this method make In-app search icon visible
		setOnSearchClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				startActivity(intent);
			}
		});
	}

	private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
		@Override
		public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
		                           RowPresenter.ViewHolder rowViewHolder, Row row) {
			// each time the item is selected, code inside here will be executed.
			if (item instanceof String) { // GridItemPresenter row
				picassoBackgroundManager.updateBackgroundWithDelay("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/10/RIMG0656.jpg");

			} else if (item instanceof Movie) { // CardPresenter row
				picassoBackgroundManager.updateBackgroundWithDelay(((Movie) item).getCardImageUrl());
			}
		}
	}

	private final class ItemViewClickedListener implements OnItemViewClickedListener {
		@Override
		public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
		                          RowPresenter.ViewHolder rowViewHolder, Row row) {
			// each time the item is clicked, code inside here will be executed.
			Log.d(TAG, "onItemClicked: item = " + item.toString());
			if (item instanceof Movie) {
				Movie movie = (Movie) item;
				Log.d(TAG, "Item: " + item.toString());

				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				intent.putExtra(DetailsActivity.MOVIE, movie);
				getActivity().startActivity(intent);
			} else if (item instanceof String) {
				if (item == GRID_STRING_ERROR_FRAGMENT) {
					Intent intent = new Intent(getActivity(), ErrorActivity.class);
					startActivity(intent);
				} else if (item == GRID_STRING_GUIDED_STEP_FRAGMENT) {
					Intent intent = new Intent(getActivity(), GuidedStepActivity.class);
					startActivity(intent);
				} else if (item == GRID_STRING_VERTICAL_GRID_FRAGMENT) {
					Intent intent = new Intent(getActivity(), VerticalGridActivity.class);
					startActivity(intent);
				} else if (item == GRID_STRING_RECOMMENDATION) {
					//https://developer.android.com/training/tv/discovery/recommendations-row.html
					//Note:
					// Use the APIs described here for making recommendations in apps running in Android versions up to and including Android 7.1 (API level 25) only.
					// To supply recommendations for apps running in Android 8.0 (API level 26) and later, your app must use recommendations channels.
					Log.v(TAG, "onClick recommendation. counter " + recommendationCounter);
					RecommendationFactory recommendationFactory = new RecommendationFactory(getActivity().getApplicationContext());
					Movie movie = mItems.get(recommendationCounter % mItems.size());
					recommendationFactory.recommend(recommendationCounter, movie, NotificationCompat.PRIORITY_HIGH);
					Toast.makeText(getActivity(), "Recommendation sent (item " + recommendationCounter +")", Toast.LENGTH_SHORT).show();
					recommendationCounter++;
				} else if (item == GRID_STRING_SPINNER) {
					// Show SpinnerFragment, while doing some is executed.
					new ShowSpinnerTask().execute();
				}

			}
		}
	}


	private class GridItemPresenter extends Presenter {
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent) {
			TextView view = new TextView(parent.getContext());
			view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setBackgroundColor(getResources().getColor(R.color.default_background));
			view.setTextColor(Color.WHITE);
			view.setGravity(Gravity.CENTER);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, Object item) {
			((TextView) viewHolder.view).setText((String) item);
		}

		@Override
		public void onUnbindViewHolder(ViewHolder viewHolder) {

		}
	}

	private class ShowSpinnerTask extends AsyncTask<Void, Void, Void> {

		SpinnerFragment mSpinnerFragment;

		@Override
		protected void onPreExecute() {
			mSpinnerFragment = new SpinnerFragment();
			getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mSpinnerFragment).commit();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Do some background process here.
			// It just waits 5 sec in this Tutorial
			SystemClock.sleep(5000);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
		}
	}

}