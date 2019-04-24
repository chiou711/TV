package com.cw.tv;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class SearchActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}

	@Override
	public boolean onSearchRequested() {
		startActivity(new Intent(this, SearchActivity.class));
		return true;
	}
}
