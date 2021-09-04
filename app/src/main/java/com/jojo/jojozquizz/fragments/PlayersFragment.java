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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

	private static final String TAG = "PlayersFragment";

	FloatingActionButton mFloatingActionButton, mFloatingActionButtonRemove, mFloatingActionButtonAdd, mFloatingActionButtonAddFromServer;
	RecyclerView mRecyclerView;
	PlayersAdapter mAdapter;

	FragmentPlayersBinding mBinding;
	boolean isMainFabRotate = false;

	List<Player> mPlayers;

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

		updateUI();
		this.configureOnClickRecyclerView();

		return mBinding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void configureOnClickRecyclerView() {
		RecyclerItemClickSupport.addTo(mRecyclerView, R.layout.fragment_players)
			.setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View v) {
					Player player = mAdapter.getUser(position);
					PlayerInformationFragment fragment = new PlayerInformationFragment();
					Bundle args = new Bundle();
					args.putInt("userId", player.getId());
					fragment.setArguments(args);
					requireActivity().getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out)
						.replace(R.id.frameLayoutPlayers, fragment)
						.addToBackStack(null)
						.commit();
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
			getActivity().finish();
		} else if (id == R.id.floatingActionButtonUsers) {
			isMainFabRotate = FabAnimation.rotateFab(v, !isMainFabRotate);
			if (isMainFabRotate) {
				FabAnimation.showIn(mFloatingActionButtonAdd, 1);
				FabAnimation.showIn(mFloatingActionButtonAddFromServer, 2);
				FabAnimation.showIn(mFloatingActionButtonRemove, 3);
			} else {
				FabAnimation.showOut(mFloatingActionButtonAdd, 3);
				FabAnimation.showOut(mFloatingActionButtonAddFromServer, 2);
				FabAnimation.showOut(mFloatingActionButtonRemove, 1);
			}
		} else if (id == R.id.floatingActionButtonChildAdd) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsNewUser(true);
			nameDialog.setIsCancelable(true);
			nameDialog.setListener(this);
			nameDialog.show(getParentFragmentManager(), "name dialog usersactivity");
		}
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
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsNewUser(true);
			nameDialog.show(getParentFragmentManager(), "name dialog usersactivity");
			Toast.makeText(getContext(), getResources().getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
		} else {
			Player player = new Player(name, getContext());
			PlayersDatabase.getInstance(getContext()).PlayersDAO().addPlayer(player);

			Player newPlayer = PlayersDatabase.getInstance(getContext()).PlayersDAO().getPlayerFromName(player.getName());
			getContext().getSharedPreferences("com.jojo.jojozquizz", Context.MODE_PRIVATE).edit().putInt("currentUserId", newPlayer.getId()).apply();
		}
	}
}