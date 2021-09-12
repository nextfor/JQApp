package com.jojo.jojozquizz.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
		int userId = 0;
		if (args != null) {
			userId = args.getInt("userId", 0);
		}

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

		if (id == R.id.information_back_arrow) {
			getParentFragmentManager().popBackStack();
		} else if (id == R.id.editPlayerFab) {
			mBinding.editPlayerFab.shrink();
			askUsernameDialog();
		} else if (id == R.id.sharePlayerFab) {
			share();
		} else if (id == R.id.information_button_delete_user) {
			if (PlayersDatabase.getInstance(getContext()).PlayersDAO().getAllPlayers().size() <= 1) {
				Toast.makeText(getContext(), getString(R.string.delete_user_error), Toast.LENGTH_SHORT).show();
			} else {
				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
				builder.setTitle(R.string.delete_user)
					.setCancelable(true)
					.setMessage(R.string.delete_user_message)
					.setPositiveButton(R.string.delete, ((dialog, which) -> {
						PlayersFragment playersFragment = new PlayersFragment();
						Bundle args = new Bundle();
						args.putInt("action", 1);
						Log.d("TRUC", "onClick: " + mPlayer.getId());
						args.putInt("userId", mPlayer.getId());
						playersFragment.setArguments(args);
						requireContext().getSharedPreferences("com.jojo.jojozquizz", Context.MODE_PRIVATE).edit().putInt("currentUserId", PlayersDatabase.getInstance(getContext()).PlayersDAO().getFirstPlayer().getId()).apply();
						getParentFragmentManager().popBackStack("first", FragmentManager.POP_BACK_STACK_INCLUSIVE);
						getParentFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out_to_left)
							.replace(R.id.frameLayoutPlayers, playersFragment)
							.addToBackStack(null)
							.setReorderingAllowed(true)
							.commit();
					}))
					.setNegativeButton(R.string.all_cancel, null)
					.show();
			}
		}
	}

	private void askUsernameDialog() {
		NameDialog nameDialog = new NameDialog();
		nameDialog.setIsCancelable(true);
		nameDialog.setListener(this);
		nameDialog.show(getParentFragmentManager(), "information name dialog");
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
			Snackbar.make(mBinding.getRoot(), R.string.invalid_name, Snackbar.LENGTH_SHORT).show();
			askUsernameDialog();
		} else {
			PlayersDatabase.getInstance(getContext()).PlayersDAO().changeName(mPlayer.getId(), name);
			mBinding.informationUserName.setText(name);
			mBinding.editPlayerFab.extend();
		}
	}
}