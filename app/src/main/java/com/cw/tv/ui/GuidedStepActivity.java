package com.cw.tv.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cw.tv.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

/**
 * Created by corochann on 24/7/2015.
 */
public class GuidedStepActivity extends FragmentActivity {

	private static final String TAG = GuidedStepActivity.class.getSimpleName();

	/* Action ID definition */
	private static final int ACTION_CONTINUE = 0;
	private static final int ACTION_BACK = 1;
	static FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		if (null == savedInstanceState) {
			fm = getSupportFragmentManager();
			GuidedStepSupportFragment.add(fm, new FirstStepFragment());
		}
	}

	private static void addAction(List actions, long id, String title, String desc) {
		actions.add(new GuidedAction.Builder()
				.id(id)
				.title(title)
				.description(desc)
				.build());
	}

	public static class FirstStepFragment extends GuidedStepSupportFragment {

		@Override
		public int onProvideTheme() {
			return R.style.Theme_Example_Leanback_GuidedStep_First;
		}

		@NonNull
		@Override
		public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
			String title = "Title";
			String breadcrumb = "Breadcrumb";
			String description = "Description";
			Drawable icon = getActivity().getDrawable(R.drawable.ic_main_icon);

			return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
		}

		@Override
		public void onCreateActions(@NonNull List actions, Bundle savedInstanceState) {
			addAction(actions, ACTION_CONTINUE, "Continue", "Go to SecondStepFragment");
			addAction(actions, ACTION_BACK, "Cancel", "Go back");
		}

		@Override
		public void onGuidedActionClicked(GuidedAction action) {

			switch ((int) action.getId()){
				case ACTION_CONTINUE:
					 GuidedStepSupportFragment.add(fm, new SecondStepFragment());
					break;
				case ACTION_BACK:
					getActivity().finish();
					break;
				default:
					Log.w(TAG, "Action is not defined");
					break;
			}
		}
	}


	public static class SecondStepFragment extends GuidedStepSupportFragment {
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
}