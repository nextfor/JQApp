package com.jojo.jojozquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.jojo.jojozquizz.databinding.ActivitySettingsBinding;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.QuestionsDatabase;
import com.jojo.jojozquizz.utils.ErrorShower;
import com.jojo.jojozquizz.utils.QuestionsRequestsHelper;

import retrofit2.Call;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements ClickHandler, Observer<Integer>, QuestionsRequestsHelper.ResponseListener {

	private Context mContext;
	private SharedPreferences mPreferences;

	private RadioButton mFrButton, mEnButton;

	private String API_URL;


	private MutableLiveData<Integer> LAST_ID;

	ActivitySettingsBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		mBinding.setHandler(this);

		mContext = this;

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		API_URL = getResources().getString(R.string.api_domain);

		mFrButton = mBinding.settingsRadioFr;
		mEnButton = mBinding.settingsRadioEn;

		String language = mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN");
		switch (language) {
			case "FR":
				mFrButton.setChecked(true);
				break;
			default:
				mEnButton.setChecked(true);
				break;
		}

		LAST_ID = new MutableLiveData<>();
		getLastIdFromServer();
		LAST_ID.observe(this, this);
	}

	@Override
	protected void onStop() {
		String selectedLang = mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN");
		if (mFrButton.isChecked()) {
			if (selectedLang.equals("EN")) {
				QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
			}
			mPreferences.edit().putString(getString(R.string.PREF_LANGUAGE), "FR").apply();
		} else if (mEnButton.isChecked()) {
			if (selectedLang.equals("FR"))
				QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
			mPreferences.edit().putString(getString(R.string.PREF_LANGUAGE), "EN").apply();
		}

		super.onStop();
	}

	private void getLastIdFromServer() {
		String lastIdRoute = getResources().getString(R.string.api_endpoint_getLastId);
		String lang = mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN");


	}

	private void addQuestions(long lastId) {
		long lastIdInDatabase;
		if (QuestionsDatabase.getInstance(this).QuestionDAO().getAllQuestions().isEmpty()) {
			lastIdInDatabase = 0;
		} else {
			lastIdInDatabase = QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion().getId() + 1;
		}

		for (long i = lastIdInDatabase; i < lastId; i++) {
			QuestionsRequestsHelper.getQuestion(mContext, getString(R.string.PREF_LANGUAGE), i, this);
		}
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.settings_reload_questions) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.settings_rewrite_database))
				.setMessage(getResources().getString(R.string.settings_rewrite_database_confirmation))
				.setNegativeButton(getResources().getString(R.string.all_cancel), null)
				.setPositiveButton(getString(R.string.rewrite), (dialog, which) -> {
					QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
					getLastIdFromServer();
				});
			builder.show();
		} else if (id == R.id.privacy_policy) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.fr/jojozquizz/privacy_policy/" + mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN")));
			startActivity(launchBrowser);
		} else if (id == R.id.terms_of_service) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.fr/jojozquizz/terms_of_service/" + mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN")));
			startActivity(launchBrowser);
		} else if (id == R.id.settingsBackButton) {
			finish();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	@Override
	public void onChanged(Integer integer) {
		if (QuestionsDatabase.getInstance(mContext).QuestionDAO().getAllQuestions().isEmpty() || integer > QuestionsDatabase.getInstance(mContext).QuestionDAO().getLastQuestion().getId()) {
			addQuestions(integer);
		}
	}

	@Override
	public void onIdResponse(Call<LastIdResponse> call, Response<LastIdResponse> response) {
		if (response.code() == 200) {
			addQuestions(response.body().getQuestionId());
		} else {
			ErrorShower.showError(mContext, mBinding.getRoot(), ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
		}
	}

	@Override
	public void onIdFailure(Call<LastIdResponse> call, Throwable t) {
		ErrorShower.showError(mContext, mBinding.getRoot(), ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
	}

	@Override
	public void onQuestionResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
		if (response.code() == 200 && response.body() != null) {
			Question question = new Question(response.body());
			QuestionsDatabase.getInstance(mContext).QuestionDAO().addQuestion(question);
		} else {
			ErrorShower.showError(mContext, mBinding.getRoot(), ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
		}
	}

	@Override
	public void onQuestionFailure(Call<QuestionResponse> call, Throwable t) {

	}
}