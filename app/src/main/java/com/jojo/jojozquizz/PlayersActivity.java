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

public class PlayersActivity extends AppCompatActivity implements NameDialog.NameDialogListener, ClickHandler {

	private static final String TAG = "PlayersActivity";

	private ImageButton mBackButton;

	ActivityPlayersBinding mBinding;

	PlayersFragment mPlayersFragment;
	PlayerInformationFragment mPlayerInformationFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_players);
		mBinding.setHandler(this);

		mBackButton = findViewById(R.id.button_back_users_activity);

		mPlayersFragment = new PlayersFragment();
		mPlayerInformationFragment = new PlayerInformationFragment();

		getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutPlayers, mPlayersFragment).commit();
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.button_back_users_activity) {
			finish();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsNewUser(true);
			nameDialog.show(getSupportFragmentManager(), "name dialog usersactivity");
			Toast.makeText(this, getResources().getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
		} else {
			Player player = new Player(name, this);
			PlayersDatabase.getInstance(this).PlayersDAO().addPlayer(player);

			Player newPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayerFromName(player.getName());
			this.getSharedPreferences("com.jojo.jojozquizz", Context.MODE_PRIVATE).edit().putInt("currentUserId", newPlayer.getId()).apply();
		}
	}
}