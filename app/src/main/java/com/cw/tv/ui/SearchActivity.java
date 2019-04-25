package com.cw.tv.ui;

import android.content.Intent;
import android.os.Bundle;

import com.cw.tv.R;

import androidx.fragment.app.FragmentActivity;

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
