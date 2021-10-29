package com.jojo.jojozquizz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentPlayersBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersAdapter;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.RecyclerItemClickSupport;
import com.jojo.jojozquizz.ui.FabAnimation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayersFragment extends Fragment implements ClickHandler, NameDialog.NameDialogListener {

	FloatingActionButton mFloatingActionButton, mFloatingActionButtonRemove, mFloatingActionButtonAdd, mFloatingActionButtonAddFromServer;
	RecyclerView mRecyclerView;
	PlayersAdapter mAdapter;

	FragmentPlayersBinding mBinding;
	boolean isMainFabRotate = false;

	List<Player> mPlayers;

	private int mSelectionMode = 0; // 0 -> Open info - 1 -> Delete

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentPlayersBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_players, container, false);

		mBinding.setHandler(this);
		mFloatingActionButton = mBinding.floatingActionButtonUsers;
		mFloatingActionButtonAdd = mBinding.floatingActionButtonChildAdd;
		mFloatingActionButtonAddFromServer = mBinding.floatingActionButtonChildAddFromServer;
		mFloatingActionButtonRemove = mBinding.floatingActionButtonChildRemove;

		mRecyclerView = mBinding.recyclerUsers;

		FabAnimation.init(mFloatingActionButtonRemove);
		FabAnimation.init(mFloatingActionButtonAdd);
		FabAnimation.init(mFloatingActionButtonAddFromServer);

		mPlayers = PlayersDatabase.getInstance(getContext()).PlayersDAO().getAllPlayers();

		mFloatingActionButtonRemove.setEnabled(mPlayers.size() > 1);

		updateUI();
		this.configureOnClickRecyclerView();

		return mBinding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		isMainFabRotate = false;

		Bundle args = getArguments();
		if (args != null) {
			if (args.getInt("action") == 1) {
				deleteUser(args.getInt("userId"));
			}
			args.clear();
		}
	}

	void deleteUser(int id) {
		Player removedPlayer = PlayersDatabase.getInstance(getContext()).PlayersDAO().getPlayer(id);
		int i = mPlayers.indexOf(removedPlayer);
		PlayersDatabase.getInstance(getContext()).PlayersDAO().deletePlayerWithId(removedPlayer.getId());
		mPlayers.remove(i);
		mAdapter.notifyItemRemoved(i);
		if (mAdapter.getItemCount() <= 1 && mSelectionMode == 1) {
			mSelectionMode = 0;
		}
	}

	private void configureOnClickRecyclerView() {
		RecyclerItemClickSupport.addTo(mRecyclerView, R.layout.fragment_players)
			.setOnItemClickListener((recyclerView, position, v) -> {
				Player player = mAdapter.getUser(position);
				if (mSelectionMode == 0) {
					PlayerInformationFragment fragment = new PlayerInformationFragment();
					Bundle args = new Bundle();
					args.putInt("userId", player.getId());
					fragment.setArguments(args);
					requireActivity().getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out)
						.replace(R.id.frameLayoutPlayers, fragment)
						.setReorderingAllowed(true)
						.addToBackStack("first")
						.commit();
				} else if (mSelectionMode == 1) {
					Toast.makeText(getContext(), "Vous voulez supprimer " + player.getName(), Toast.LENGTH_SHORT).show();
					MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
					builder.setTitle(R.string.delete_user)
						.setCancelable(true)
						.setMessage(R.string.delete_user_message)
						.setPositiveButton(R.string.delete, ((dialog, which) -> {
							deleteUser(mAdapter.getUser(position).getId());
						}))
						.setNegativeButton(R.string.all_cancel, null)
						.show();
				}
			});
	}

	protected void updateUI() {
		mAdapter = new PlayersAdapter(getContext(), mPlayers);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.returnButtonPlayersFragment) {
			requireActivity().finish();
		} else if (id == R.id.floatingActionButtonUsers) {
			isMainFabRotate = FabAnimation.rotateFab(v, !isMainFabRotate);
			if (isMainFabRotate) {
				FabAnimation.showIn(mFloatingActionButtonAdd, 1);
				FabAnimation.showIn(mFloatingActionButtonAddFromServer, 2);
				FabAnimation.showIn(mFloatingActionButtonRemove, 3);
			} else {
				if (mSelectionMode == 1) {
					mSelectionMode = 0;
				} else {
					FabAnimation.showOut(mFloatingActionButtonAdd, 3);
					FabAnimation.showOut(mFloatingActionButtonAddFromServer, 2);
					FabAnimation.showOut(mFloatingActionButtonRemove, 1);
				}
			}
		} else if (id == R.id.floatingActionButtonChildAdd) {
			showOutEverything();
			askUsernameDialog();
		} else if (id == R.id.floatingActionButtonChildRemove) {
			mSelectionMode = 1;
			showOutEverything();
		}
	}

	private void showOutEverything() {
		FabAnimation.showOut(mFloatingActionButtonAdd, 3);
		FabAnimation.showOut(mFloatingActionButtonAddFromServer, 2);
		FabAnimation.showOut(mFloatingActionButtonRemove, 1);
	}

	private void askUsernameDialog() {
		NameDialog nameDialog = new NameDialog();
		nameDialog.setIsNewUser(true);
		nameDialog.setIsCancelable(true);
		nameDialog.setListener(this);
		nameDialog.show(getParentFragmentManager(), "name dialog usersactivity");
	}

	@Override
	public boolean onLongButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.floatingActionButtonChildAdd || id == R.id.floatingActionButtonChildAddFromServer || id == R.id.floatingActionButtonChildRemove) {
			Toast.makeText(getContext(), v.getContentDescription(), Toast.LENGTH_SHORT).show();
		}
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
			Player player = new Player(name, getContext());
			PlayersDatabase.getInstance(getContext()).PlayersDAO().addPlayer(player);
			Player newPlayer = PlayersDatabase.getInstance(getContext()).PlayersDAO().getPlayerFromName(player.getName());
			requireActivity().getSharedPreferences("com.jojo.jojozquizz", Context.MODE_PRIVATE).edit().putInt("currentUserId", newPlayer.getId()).apply();
			mPlayers.add(newPlayer);
			mAdapter.notifyItemInserted(mPlayers.size() - 1);
			mFloatingActionButtonRemove.setEnabled(mPlayers.size() > 1);
		}
	}
}