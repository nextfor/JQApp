package com.jojo.jojozquizz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.jojo.jojozquizz.databinding.ActivitySelectCategoriesBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.ui.categories.CategoriesHelper;
import com.jojo.jojozquizz.ui.categories.SelectCategoriesFragment;
import com.jojo.jojozquizz.ui.categories.SwitchHandler;
import com.jojo.jojozquizz.ui.categories.adapters.CategoriesAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoriesActivity extends AppCompatActivity implements CategoriesAdapter.CategoriesCheckListener, ClickHandler, SwitchHandler {

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

	ActivitySelectCategoriesBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_categories);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_categories);
		mBinding.setHandler(this);
		mBinding.setSwitchHandler(this);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		int userId = mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 0);
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(userId);

		getSupportFragmentManager().beginTransaction()
			.setReorderingAllowed(true)
			.add(mBinding.fragmentContainerViewCategories.getId(), SelectCategoriesFragment.class, null)
			.commit();

		mCategoriesHelper = new CategoriesHelper(this);

		mOldCategoriesSelected = mCategoriesHelper.getProcessedCategories(mPlayer.getCategoriesSelected());
		mNewCategoriesSelected = mOldCategoriesSelected;

//		mSelectAllSwitch = mBinding.switchSelectAll;
//		mCheckboxEasy = mBinding.activitySelectCategoriesCheckboxFacile;
//		mCheckboxMedium = mBinding.activitySelectCategoriesCheckboxMoyen;
//		mCheckboxHard = mBinding.activitySelectCategoriesCheckboxDifficile;
		mCheckBoxes = new MaterialCheckBox[]{mCheckboxEasy, mCheckboxMedium, mCheckboxHard};

		mCategoriesAdapter = new CategoriesAdapter(this, mCategoriesHelper.getCategories(), mOldCategoriesSelected);
//		mRecyclerView = mBinding.recyclerCategories;
		mRecyclerView.setAdapter(mCategoriesAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mDifficultiesSelected = mCategoriesHelper.getProcessedDifficulties(mPlayer.getDifficultiesSelected());

		int i = 0;
		for (CheckBox checkBox : mCheckBoxes) {
			checkBox.setText(mCategoriesHelper.getDifficulties()[i]);
			checkBox.setChecked(mDifficultiesSelected.contains(checkBox.getText().toString()));
			i++;
		}
	}

	@Override
	public void onBackPressed() {
		quitActivity();
	}

	private void quitActivity() {
		if (isGoodToFinish()) {
			PlayersDatabase.getInstance(this).PlayersDAO().setCategories(mPlayer.getId(), mCategoriesHelper.processCategories(mNewCategoriesSelected));
			PlayersDatabase.getInstance(this).PlayersDAO().setDifficulties(mPlayer.getId(), mCategoriesHelper.processDifficulties(convertCheckboxesToNames(mCheckBoxes)));
			finish();
		} else {
			Toast.makeText(this, getString(R.string.select_nothing_checked), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isGoodToFinish() {
		return !mCategoriesHelper.processCategories(mNewCategoriesSelected).equals(mCategoriesHelper.getNoneCategoriesProcessed()) && !mCategoriesHelper.processDifficulties(convertCheckboxesToNames(new CheckBox[]{mCheckboxEasy, mCheckboxMedium, mCheckboxHard})).equals(mCategoriesHelper.getNoneDifficultiesrocessed());
	}

	@Override
	public void checkChanged(String category, boolean isChecked) {
		if (isChecked && !mNewCategoriesSelected.contains(category)) {
			mNewCategoriesSelected.add(category);
		} else if (!isChecked) {
			mNewCategoriesSelected.remove(category);
		}
		mCategoriesAdapter.changeCategoriesChecked(mNewCategoriesSelected);
	}

	private List<String> convertCheckboxesToNames(CheckBox[] checkBoxes) {
		List<String> valuesToReturn = new ArrayList<>();
		for (CheckBox checkBox : checkBoxes) {
			if (checkBox.isChecked()) {
				valuesToReturn.add(checkBox.getText().toString());
			}
		}
		return valuesToReturn;
	}

	@Override
	public void onButtonClick(View v) {

		int id = v.getId();

		if (id == R.id.categoriesBackButton) {
			finish();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	@Override
	public void onCheckChanged(CompoundButton buttonView, boolean isChecked) {
		mSelectAllSwitch.setText(isChecked ? R.string.deselect_all : R.string.select_all);
		if (isChecked) {
			mNewCategoriesSelected = mCategoriesHelper.getProcessedCategories(mCategoriesHelper.getAllCategoriesProcessed());
		} else {
			mNewCategoriesSelected = mCategoriesHelper.getProcessedCategories(mCategoriesHelper.getNoneCategoriesProcessed());
		}
		mCategoriesAdapter.changeCategoriesChecked(mNewCategoriesSelected);
		mCategoriesAdapter.notifyDataSetChanged();
	}
}
