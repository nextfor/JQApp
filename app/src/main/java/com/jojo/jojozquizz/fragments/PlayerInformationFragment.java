package com.jojo.jojozquizz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jojo.jojozquizz.PlayersActivity;
import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentPlayerInformationBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerInformationFragment extends Fragment implements ClickHandler, NameDialog.NameDialogListener {

	FragmentPlayerInformationBinding mBinding;

	Player mPlayer;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player_information, container, false);

		Bundle args = getArguments();
		int userId = args.getInt("userId", 0);

		mPlayer = PlayersDatabase.getInstance(getContext()).PlayersDAO().getPlayer(userId);

		mBinding.setPlayer(mPlayer);
		mBinding.setHandler(this);

		return mBinding.getRoot();
	}

	public void share() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getQuantityString(R.plurals.score_to_send, (int) mPlayer.getGamesPlayed(), mPlayer.getValidatedQuestions(), mPlayer.getTotalQuestions(), mPlayer.getGamesPlayed()));
		sendIntent.setType("text/plain");

		Intent shareIntent = Intent.createChooser(sendIntent, null);
		startActivity(shareIntent);
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.information_button_edit_user) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsCancelable(true);
			nameDialog.setListener(this);
			nameDialog.show(getParentFragmentManager(), "information name dialog");
		} else if (id == R.id.information_share_button) {
			share();
		} else if (id == R.id.information_button_delete_user) {
			if (PlayersDatabase.getInstance(getContext()).PlayersDAO().getAllPlayers().size() <= 1) {
				Toast.makeText(getContext(), getString(R.string.delete_user_error), Toast.LENGTH_SHORT).show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle(getString(R.string.delete_user))
					.setCancelable(true)
					.setIcon(getContext().getResources().getDrawable(R.drawable.trash_icon))
					.setMessage(R.string.delete_user_message)
					.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlayersDatabase.getInstance(getContext()).PlayersDAO().deletePlayerWithId(mPlayer.getId());
							getContext().getSharedPreferences("com.jojo.jojozquizz", Context.MODE_PRIVATE).edit().putInt("currentUserId", PlayersDatabase.getInstance(getContext()).PlayersDAO().getFirstPlayer().getId()).apply();
							getParentFragmentManager().beginTransaction().replace(R.id.frameLayoutPlayers, new PlayersFragment()).commit();
						}
					}).setNegativeButton(getString(R.string.all_cancel), null);
				builder.show();
			}
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
			nameDialog.show(getParentFragmentManager(), "information name dialog");
			Toast.makeText(getContext(), getResources().getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
		} else {
			PlayersDatabase.getInstance(getContext()).PlayersDAO().changeName(mPlayer.getId(), name);
			mBinding.informationUserName.setText(name);
		}
	}
}