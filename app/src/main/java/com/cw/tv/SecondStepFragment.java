package com.cw.tv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.widget.Toast;

import java.util.List;


public class SecondStepFragment extends GuidedStepFragment {
	/* Action set ID */
	private static final int OPTION_CHECK_SET_ID = 10;

	/* Options of SecondStepFragment */
	private static final String[] OPTION_NAMES = {"Option A", "Option B", "Option C"};
	private static final String[] OPTION_DESCRIPTIONS = {"Here's one thing you can do",
			"Here's another thing you can do", "Here's one more thing you can do"};
	private static final int[] OPTION_DRAWABLES = {R.drawable.ic_guidedstep_option_a,
			R.drawable.ic_guidedstep_option_b, R.drawable.ic_guidedstep_option_c};
	private static final boolean[] OPTION_CHECKED = {true, false, false};


	@NonNull
	@Override
	public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
		String title = "SecondStepFragment";
		String breadcrumb = "Guided Steps: 2";
		String description ="Showcasing different action configurations";
		Drawable icon = getActivity().getDrawable(R.drawable.ic_main_icon);
		return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
	}

	@Override
	public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
		String title = "infoOnly action";
		String desc = "infoOnly indicates whether this action is for information purposes only and cannot be clicked.\n" +
				"The description can be long, by set multilineDescription to true";

		actions.add(new GuidedAction.Builder()
				.title(title)
				.description(desc)
				.multilineDescription(true)
				.infoOnly(true)
				.enabled(false)
				.build());
		for (int i = 0; i < OPTION_NAMES.length; i++) {
			addCheckedAction(actions,
					OPTION_DRAWABLES[i],
					getActivity(),
					OPTION_NAMES[i],
					OPTION_DESCRIPTIONS[i],
					OPTION_CHECKED[i]);
		}
	}

	@Override
	public void onGuidedActionClicked(GuidedAction action) {
		String text = OPTION_NAMES[getSelectedActionPosition() - 1] + " clicked";
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	private static void addCheckedAction(List<GuidedAction> actions, int iconResId, Context context,
	                                     String title, String desc, boolean checked) {
		GuidedAction guidedAction = new GuidedAction.Builder()
				.title(title)
				.description(desc)
				.checkSetId(OPTION_CHECK_SET_ID)
				.iconResourceId(iconResId, context)
				.build();
		guidedAction.setChecked(checked);
		actions.add(guidedAction);
	}


	@Override
	public GuidanceStylist onCreateGuidanceStylist() {
		return new GuidanceStylist() {
			@Override
			public int onProvideLayoutId() {
				return R.layout.guidedstep_second_guidance;
			}
		};
	}
}