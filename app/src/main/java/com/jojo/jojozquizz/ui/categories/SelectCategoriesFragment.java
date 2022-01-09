package com.jojo.jojozquizz.ui.categories;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentSelectCategoriesBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.ui.categories.adapters.CategoriesAdapter;

import java.util.List;

public class SelectCategoriesFragment extends Fragment implements CategoriesAdapter.CategoriesCheckListener, ClickHandler, SwitchHandler {

	SwitchCompat mSelectAllSwitch;
	MaterialCheckBox mCheckboxEasy, mCheckboxMedium, mCheckboxHard;
	MaterialCheckBox[] mCheckBoxes = new MaterialCheckBox[3];

	RecyclerView mRecyclerView;
	CategoriesAdapter mCategoriesAdapter; // Adapter for RecyclerView

	List<String> mOldCategoriesSelected; // What the mPlayer choose
	List<String> mNewCategoriesSelected; // What the mPlayer chooses

	List<String> mDifficultiesSelected;

	CategoriesHelper mCategoriesHelper; //Helper to process categories and more

	SharedPreferences mPreferences;

	Player mPlayer;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentSelectCategoriesBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_categories, container, false);
		mBinding.setHandler(this);

		mOldCategoriesSelected = mCategoriesHelper.getProcessedCategories(mPlayer.getCategoriesSelected());
		mNewCategoriesSelected = mOldCategoriesSelected;

//		mSelectAllSwitch = mBinding.switchSelectAll;
//		mCheckboxEasy = mBinding.activitySelectCategoriesCheckboxFacile;
//		mCheckboxMedium = mBinding.activitySelectCategoriesCheckboxMoyen;
//		mCheckboxHard = mBinding.activitySelectCategoriesCheckboxDifficile;
		mCheckBoxes = new MaterialCheckBox[]{mCheckboxEasy, mCheckboxMedium, mCheckboxHard};

		mCategoriesAdapter = new CategoriesAdapter(getContext(), mCategoriesHelper.getCategories(), mOldCategoriesSelected);
//		mRecyclerView = mBinding.recyclerCategories;
		mRecyclerView.setAdapter(mCategoriesAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mDifficultiesSelected = mCategoriesHelper.getProcessedDifficulties(mPlayer.getDifficultiesSelected());

		int i = 0;
		for (CheckBox checkBox : mCheckBoxes) {
			checkBox.setText(mCategoriesHelper.getDifficulties()[i]);
			checkBox.setChecked(mDifficultiesSelected.contains(checkBox.getText().toString()));
			i++;
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.categoriesBackButton) {
			requireActivity().finish();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	@Override
	public void checkChanged(String category, boolean isChecked) {

	}

	@Override
	public void onCheckChanged(CompoundButton buttonView, boolean isChecked) {

	}
}