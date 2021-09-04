package com.jojo.jojozquizz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jojo.jojozquizz.databinding.ActivityPlayersBinding;
import com.jojo.jojozquizz.fragments.PlayerInformationFragment;
import com.jojo.jojozquizz.fragments.PlayersFragment;

public class PlayersActivity extends AppCompatActivity {

	private static final String TAG = "PlayersActivity";

	ActivityPlayersBinding mBinding;

	PlayersFragment mPlayersFragment;
	PlayerInformationFragment mPlayerInformationFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_players);

		mPlayersFragment = new PlayersFragment();
		mPlayerInformationFragment = new PlayerInformationFragment();

		getSupportFragmentManager().beginTransaction()
			.add(R.id.frameLayoutPlayers, mPlayersFragment)
			.commit();
	}
}