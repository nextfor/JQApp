package com.jojo.jojozquizz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.jojo.jojozquizz.databinding.ActivityPlayersBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.fragments.PlayerInformationFragment;
import com.jojo.jojozquizz.fragments.PlayersFragment;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutPlayers, mPlayersFragment).commit();
	}
}