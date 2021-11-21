package com.jojo.jojozquizz.ui.players;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.ActivityPlayersBinding;
import com.jojo.jojozquizz.ui.players.fragments.PlayerInformationFragment;
import com.jojo.jojozquizz.ui.players.fragments.PlayersFragment;

public class PlayersActivity extends AppCompatActivity {

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
			.setReorderingAllowed(true)
			.commit();
	}
}