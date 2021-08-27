package com.jojo.jojozquizz.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentLinksBinding;

public class LinksFragment extends Fragment {

	View mView;
	FragmentLinksBinding mBinding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mBinding = FragmentLinksBinding.inflate(inflater, container, false);
		mView = mBinding.getRoot();

		TextView mTextDiscord = mBinding.activityLinksDiscordText;
		TextView mTextInstagram = mBinding.activityLinksInstagramText;
		TextView mTextYoutube = mBinding.activityLinksYoutubeText;
		TextView mTextTwitter = mBinding.activityLinksTwitterText;
		TextView mTextReddit = mBinding.activityLinksRedditText;

		mTextDiscord.setClickable(true);
		mTextInstagram.setClickable(true);
		mTextYoutube.setClickable(true);
		mTextTwitter.setClickable(true);
		mTextReddit.setClickable(true);

		mTextDiscord.setMovementMethod(LinkMovementMethod.getInstance());
		mTextInstagram.setMovementMethod(LinkMovementMethod.getInstance());
		mTextYoutube.setMovementMethod(LinkMovementMethod.getInstance());
		mTextTwitter.setMovementMethod(LinkMovementMethod.getInstance());
		mTextReddit.setMovementMethod(LinkMovementMethod.getInstance());

		mTextDiscord.setText(Html.fromHtml("<a href='https://discord.gg/fntMgg7'>Serveur Discord</a>"));
		mTextInstagram.setText(Html.fromHtml("<a href='https://instagram.com/nextfor.dev'>Instagram</a>"));
		mTextYoutube.setText(Html.fromHtml("<a href='https://www.youtube.com/channel/UCU9_Y3nu76BgZqxbfr8lX1Q'>Youtube</a>"));
		mTextTwitter.setText(Html.fromHtml("<a href='https://twitter.com/nextfordev'>Twitter</a>"));
		mTextReddit.setText(Html.fromHtml("<a href='https://reddit.com/r/jojoz'>Reddit</a>"));

		return mView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mBinding = null;
	}
}