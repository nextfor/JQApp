package com.jojo.jojozquizz.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.databinding.FragmentSelectCategoriesBinding;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.ui.categories.adapters.CategoriesAdapter;

public class SelectCategoriesFragment extends Fragment implements CategoriesAdapter.CategoriesCheckListener, ClickHandler, SwitchHandler {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentSelectCategoriesBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_categories, container, false);
		mBinding.setHandler(this);

		CategoriesHelper categoriesHelper = new CategoriesHelper(requireActivity());

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