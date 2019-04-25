package com.cw.tv.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.cw.tv.R;
import com.cw.tv.common.PlaybackController;
import com.cw.tv.common.Utils;
import com.cw.tv.model.Movie;

import androidx.fragment.app.FragmentActivity;

public class PlaybackOverlayActivity extends FragmentActivity {

	private static final String TAG = PlaybackOverlayActivity.class.getSimpleName();

	private VideoView mVideoView;

	private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
	private PlaybackController mPlaybackController;
	private int mPosition = 0;
	private long mStartTimeMillis;
	private long mDuration = -1;
	private Movie mSelectedMovie;
	private int mCurrentItem;

	/*
	 * List of various states that we can be in
	 */
	public enum LeanbackPlaybackState {
		PLAYING, PAUSED, IDLE
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSelectedMovie = (Movie) getIntent().getSerializableExtra(DetailsActivity.MOVIE);
		mCurrentItem = (int) mSelectedMovie.getId() - 1;

		mPlaybackController = new PlaybackController(this);
		setContentView(R.layout.activity_playback_overlay);

		mVideoView = (VideoView) findViewById(R.id.videoView);

		mPlaybackController.setCurrentItem(mCurrentItem);
		mPlaybackController.setVideoView(mVideoView);
		mPlaybackController.setMovie(mSelectedMovie); // it must after video view setting

		loadViews();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopPlayback();
		mVideoView.suspend();
		mVideoView.setVideoURI(null);
	}

	private void loadViews() {
		mVideoView = (VideoView) findViewById(R.id.videoView);
		// For fixing an issue which VideoView get focus by default, making control fragment lost focus on activity starts
		mVideoView.post(new Runnable() {
			@Override
			public void run() {
				mVideoView.setFocusable(false);
				mVideoView.setFocusableInTouchMode(false);
			}
		});

//		Movie movie = (Movie) getIntent().getSerializableExtra(DetailsActivity.MOVIE);
		//movie.setVideoUrl("http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4");
//		setVideoPath(movie.getVideoUrl());

		mPlaybackController.setVideoPath(mSelectedMovie.getVideoUrl());
	}

	public void setVideoPath(String videoUrl) {
		setPosition(0);
		mVideoView.setVideoPath(videoUrl);
		mStartTimeMillis = 0;
		mDuration = Utils.getDuration(videoUrl);
	}

	private void stopPlayback() {
		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}
	}

	public void playPause(boolean doPlay) {
		if (mPlaybackState == LeanbackPlaybackState.IDLE) {
			/* Callbacks for mVideoView */
			setupCallbacks();
		}

		if (doPlay && mPlaybackState != LeanbackPlaybackState.PLAYING) {
			mPlaybackState = LeanbackPlaybackState.PLAYING;
			if (mPosition > 0) {
				mVideoView.seekTo(mPosition);
			}
			mVideoView.start();
			mStartTimeMillis = System.currentTimeMillis();
		} else {
			mPlaybackState = LeanbackPlaybackState.PAUSED;
			int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
			setPosition(mPosition + timeElapsedSinceStart);
			mVideoView.pause();
		}
	}

	private void setupCallbacks() {

		mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mVideoView.stopPlayback();
				mPlaybackState = LeanbackPlaybackState.IDLE;
				return false;
			}
		});

		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
					mVideoView.start();
				}
			}
		});

		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlaybackState = LeanbackPlaybackState.IDLE;
			}
		});
	}

	public void fastForward() {
		if (mDuration != -1) {
			// Fast forward 10 seconds.
			setPosition(mVideoView.getCurrentPosition() + (10 * 1000));
			mVideoView.seekTo(mPosition);
		}
	}

	public void rewind() {
		// rewind 10 seconds
		setPosition(mVideoView.getCurrentPosition() - (10 * 1000));
		mVideoView.seekTo(mPosition);
	}

	public void setPosition(int position) {
		if (position > mDuration) {
			Log.d(TAG, "position: " + position + ", mDuration: " + mDuration);
			mPosition = (int) mDuration;
		} else if (position < 0) {
			mPosition = 0;
			mStartTimeMillis = System.currentTimeMillis();
		} else {
			mPosition = position;
		}
		mStartTimeMillis = System.currentTimeMillis();
		Log.d(TAG, "position set to " + mPosition);
	}

	public void setPlaybackState(LeanbackPlaybackState mPlaybackState) {
		this.mPlaybackState = mPlaybackState;
	}

	public int getPosition() {
		return mPosition;
	}

	public PlaybackController getPlaybackController() {
		return mPlaybackController;
	}

}