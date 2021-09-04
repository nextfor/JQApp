package com.jojo.jojozquizz;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jojo.jojozquizz.databinding.ActivityLinksBinding;
import com.jojo.jojozquizz.fragments.LinksFragment;
import com.jojo.jojozquizz.fragments.ThanksFragment;
import com.jojo.jojozquizz.tools.ClickHandler;

public class LinksActivity extends AppCompatActivity implements ClickHandler {

	ActivityLinksBinding mBinding;

	Fragment mLinksFragment, mThanksFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_links);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_links);
		mBinding.setHandler(this);

		AdView mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mLinksFragment = new LinksFragment();
		mThanksFragment = new ThanksFragment();
		getSupportFragmentManager().beginTransaction()
			.add(R.id.frame_layout_links, mLinksFragment)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.links_activity_show_links) {
			if (mLinksFragment == null) {
				mLinksFragment = new LinksFragment();
			}
			getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_from_left, R.anim.fade_out)
				.replace(R.id.frame_layout_links, mLinksFragment)
				.commit();
		} else if (id == R.id.links_activity_show_thanks) {
			if (mThanksFragment == null) {
				mThanksFragment = new ThanksFragment();
			}
			getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
				.replace(R.id.frame_layout_links, mThanksFragment)
				.commit();
		} else if (id == R.id.linksReturnButton) {
			finish();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}
}
