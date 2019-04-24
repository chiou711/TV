package com.cw.tv.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.tv.R;
import com.cw.tv.data.MovieProvider;
import com.cw.tv.model.Movie;
import com.cw.tv.recommendation.RecommendationFactory;

import java.util.ArrayList;

public class MainFragment extends BrowseSupportFragment {
	private static final String TAG = MainFragment.class.getSimpleName();
	private ArrayObjectAdapter mRowsAdapter;
	private static final int GRID_ITEM_WIDTH = 300;
	private static final int GRID_ITEM_HEIGHT = 200;
	private static SimpleBackgroundManager simpleBackgroundManager = null;
	private static PicassoBackgroundManager picassoBackgroundManager = null;
	ArrayList<Movie> mItems = MovieProvider.getMovieItems();
	private static final String GRID_STRING_RECOMMENDATION = "Recommendation";
	private static final String GRID_STRING_SPINNER = "Spinner";

	private static int recommendationCounter = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		setupUIElements();
		loadRows();
		setupEventListeners();
//		simpleBackgroundManager = new SimpleBackgroundManager(getActivity());
		picassoBackgroundManager = new PicassoBackgroundManager(getActivity());
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
	}

	private void loadRows() {
		mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

		/* GridItemPresenter */
		HeaderItem gridItemPresenterHeader = new HeaderItem(0, "GridItemPresenter");

		GridItemPresenter mGridPresenter = new GridItemPresenter();
		ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
//		gridRowAdapter.add("ITEM 1");
		gridRowAdapter.add("ErrorFragment");
		gridRowAdapter.add("GuidedStepFragment");
		gridRowAdapter.add(GRID_STRING_RECOMMENDATION);
		gridRowAdapter.add(GRID_STRING_SPINNER);
		mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

		/* CardPresenter */
		HeaderItem cardPresenterHeader = new HeaderItem(1, "CardPresenter");
		CardPresenter cardPresenter = new CardPresenter();
		ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

//		for(int i=0; i<10; i++) {
//			Movie movie = new Movie();
//			movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//
//			movie.setTitle("title" + i);
//			movie.setStudio("studio" + i);
//			cardRowAdapter.add(movie);
//		}

//		for(int i=0; i<10; i++) {
//			Movie movie = new Movie();
//			if(i%3 == 0) {
//				movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//			} else if (i%3 == 1) {
//				movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02630.jpg");
//			} else {
//				movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02529.jpg");
//			}
//			movie.setTitle("title" + i);
//			movie.setStudio("studio" + i);
//			cardRowAdapter.add(movie);
//		}

		for (Movie movie : mItems) {
			cardRowAdapter.add(movie);
		}

		mRowsAdapter.add(new ListRow(cardPresenterHeader, cardRowAdapter));

		/* set */
		setAdapter(mRowsAdapter);
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
//				simpleBackgroundManager.clearBackground();
				picassoBackgroundManager.updateBackgroundWithDelay("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/10/RIMG0656.jpg");

			} else if (item instanceof Movie) { // CardPresenter row
//				simpleBackgroundManager.updateBackground(getActivity().getDrawable(R.drawable.movie));
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
				if (item == "ErrorFragment") {
					Intent intent = new Intent(getActivity(), ErrorActivity.class);
					startActivity(intent);
				} else if (item == "GuidedStepFragment") {
					Intent intent = new Intent(getActivity(), GuidedStepActivity.class);
					startActivity(intent);
				} else if (item == GRID_STRING_RECOMMENDATION) {
					//https://developer.android.com/training/tv/discovery/recommendations-row.html
					//Note:
					// Use the APIs described here for making recommendations in apps running in Android versions up to and including Android 7.1 (API level 25) only.
					// To supply recommendations for apps running in Android 8.0 (API level 26) and later, your app must use recommendations channels.
					Log.v(TAG, "onClick recommendation. counter " + recommendationCounter);
					RecommendationFactory recommendationFactory = new RecommendationFactory(getActivity().getApplicationContext());
					Movie movie = mItems.get(recommendationCounter % mItems.size());
//					Movie movie = mItems.get(2);
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