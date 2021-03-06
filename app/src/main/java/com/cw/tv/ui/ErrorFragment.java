package com.cw.tv.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cw.tv.R;

/**
 * Modified from AOSP sample code by corochann on 7/7/2015.
 * This class demonstrates how to extend ErrorFragment
 */
public class ErrorFragment extends androidx.leanback.app.ErrorFragment {

	private static final String TAG = ErrorFragment.class.getSimpleName();
	private static final boolean TRANSLUCENT = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setTitle(getResources().getString(R.string.app_name));
		setErrorContent();
	}

	void setErrorContent() {
		setImageDrawable(getActivity().getDrawable(R.drawable.lb_ic_sad_cloud));
		setMessage(getResources().getString(R.string.error_fragment_message));
		setDefaultBackground(TRANSLUCENT);

		setButtonText(getResources().getString(R.string.dismiss_error));
		setButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
			}
		});
	}
}