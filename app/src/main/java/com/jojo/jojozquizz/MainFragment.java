package com.jojo.jojozquizz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.FragmentMainBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.reponse.EventResponse;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;
import com.jojo.jojozquizz.utils.Client;
import com.jojo.jojozquizz.utils.ErrorShower;
import com.jojo.jojozquizz.utils.MainActivity;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements ClickHandler, NameDialog.NameDialogListener {

	View view;
	FragmentMainBinding binding;

	SharedPreferences sharedPreferences;

	EditText mNumberOfQuestionsInput;

	public MainFragment() {
		super(R.layout.fragment_main);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean isFirstTime = getArguments().getBoolean("isFirstTime");

		sharedPreferences = MainActivity.getInstance().getPreferences();

		if (isFirstTime) {
			firstTime();
		} else {
			if (sharedPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN") == null) {
				String lang;
				switch (Locale.getDefault().getCountry()) {
					default:
						lang = "FR";
						break;
				}
				sharedPreferences.edit().putString(getString(R.string.PREF_LANGUAGE), lang).putString(getString(R.string.PREF_LANGUAGE), null).apply();

				player = PlayersDatabase.getInstance(MainActivity.getInstance().getContext()).PlayersDAO().getPlayer(sharedPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
				com.jojo.jojozquizz.utils.MainActivity.getInstance().setPlayer(player);
			}
		}
	}

	private void firstTime() {
		String lang;
		switch (Locale.getDefault().getCountry()) {
			default:
				lang = "FR";
				break;
		}
		MainActivity.getInstance().getPreferences().edit().putString(getString(R.string.PREF_LANGUAGE), lang).apply();
		askUsernameDialog();
	}

	private void askUsernameDialog() {
		NameDialog nameDialog = new NameDialog();
		nameDialog.setIsNewUser(true);
		nameDialog.setIsCancelable(false);
		nameDialog.setListener(this);
		nameDialog.show(requireActivity().getSupportFragmentManager(), "name dialog");
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentMainBinding.inflate(inflater, container, false);
		view = binding.getRoot();

		binding.setPlayer(MainActivity.getInstance().getPlayer());
		binding.setHandler(this);

		mNumberOfQuestionsInput = binding.activityMainNumberQuestionsInput;

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		String[] texts = getResources().getStringArray(R.array.splash_texts);
		Random r = new Random();

		binding.activityMainTextTitle.setText(texts[r.nextInt(texts.length)]);
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.activity_main_start_button) {
			if (!mNumberOfQuestionsInput.getText().toString().isEmpty()) {
				int mNumberOfQuestionsAsk = Integer.parseInt(mNumberOfQuestionsInput.getText().toString());
				if (mNumberOfQuestionsAsk <= 0) {
					Toast.makeText(requireContext(), R.string.error_start0, Toast.LENGTH_LONG).show();
				} else if (mNumberOfQuestionsAsk > 75) {
					Toast.makeText(requireContext(), R.string.error_start1, Toast.LENGTH_LONG).show();
				} else if (QuestionsDatabase.getInstance(requireContext()).QuestionDAO().getLastQuestion() == null) {
					ErrorShower.showError(requireContext(), view, ErrorShower.TYPE_SNACKBAR, getString(R.string.no_questions), null);
				} else {
					//startActivityForResult(new Intent(requireContext(), GameActivity.class).putExtra("userId", MainActivity.getInstance().getPlayer().getId()).putExtra("numberOfQuestions", mNumberOfQuestionsAsk), GAME_ACTIVITY_REQUEST_CODE);
				}
			}
		} else if (id == R.id.button_users || id == R.id.text_display_name) {
			//startActivityForResult(new Intent(requireContext(), PlayersActivity.class), );
		} else if (id == R.id.activity_main_select_categories_button) {
			//startActivity(new Intent(requireContext(), SelectCategoriesActivity.class));
		} else if (id == R.id.activity_main_bonus_button) {
			//startActivity(new Intent(requireContext(), BonusActivity.class));
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	private void getEvents() {
		Call<List<EventResponse>> call = Client.getClient(requireContext()).getApi().getEvents();
		call.enqueue(new Callback<List<EventResponse>>() {
			@Override
			public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
				if (response.code() == 200 && response.body() != null) {
					if (!response.body().get(0).getImage().isEmpty()) {
//						mBinding.ivEvent.setVisibility(View.VISIBLE);
//						Picasso.get().load(response.body().get(0).getImage()).into(mBinding.ivEvent);
					}
				}
			}

			@Override
			public void onFailure(Call<List<EventResponse>> call, Throwable t) {
			}
		});
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			ErrorShower.showError(MainActivity.getInstance().getContext(), binding.getRoot(), ErrorShower.TYPE_SNACKBAR, getString(R.string.invalid_name), null);
			askUsernameDialog();
		} else {
			Player player = new Player(name, MainActivity.getInstance().getContext());
			PlayersDatabase.getInstance(MainActivity.getInstance().getContext()).PlayersDAO().addPlayer(player);

			MainActivity.getInstance().getPreferences().edit().putInt(getString(R.string.PREF_CURRENT_USER_ID), PlayersDatabase.getInstance(MainActivity.getInstance().getContext()).PlayersDAO().getIdFromName(name)).apply();
			player = PlayersDatabase.getInstance(MainActivity.getInstance().getContext()).PlayersDAO().getPlayer(MainActivity.getInstance().getPreferences().getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
			com.jojo.jojozquizz.utils.MainActivity.getInstance().setPlayer(player);

//	TODO	isFirstTime = false;
			binding.setPlayer(player);
		}
	}
}