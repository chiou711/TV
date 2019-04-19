package com.cw.tv;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailsActivity extends Activity {
	public static final String MOVIE = "Movie";
	public static final String SHARED_ELEMENT_NAME = "hero";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
	}
}
