package com.jojo.jojozquizz.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentPlayerInformationBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.PlayersDatabase;

public class PlayerInformationFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentPlayerInformationBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player_information, container, false);

		Bundle args = getArguments();
		int userId = args.getInt("userId", 0);

		mBinding.setPlayer(PlayersDatabase.getInstance(getContext()).PlayersDAO().getPlayer(userId));
		return mBinding.getRoot();
	}
}