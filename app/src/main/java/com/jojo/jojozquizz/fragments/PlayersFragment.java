package com.jojo.jojozquizz.fragments;

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

public class PlayersFragment extends Fragment implements ClickHandler {

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
					getActivity().getSupportFragmentManager().beginTransaction()
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

		if (id == R.id.floatingActionButtonUsers) {
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
}