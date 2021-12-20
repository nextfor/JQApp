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

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ActivitySettingsBinding;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements ClickHandler, Observer<Integer> {

	private Context mContext;
	private SharedPreferences mPreferences;

	private RadioButton mFrButton, mEnButton;

	private String API_URL;

	private RequestQueue mRequestQueue;
	private Cache mCache;
	private BasicNetwork mNetwork;

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

		mCache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		mNetwork = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(mCache, mNetwork);
		mRequestQueue.start();

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
		getServerKey();
		getLastIdFromServer();
		LAST_ID.observe(this, this);
	}

	private void getServerKey() {
		String serverKeyRoute = getResources().getString(R.string.api_endpoint_getServerKey);
		JsonObjectRequest serverKeyRequest = new JsonObjectRequest(Request.Method.GET, API_URL + serverKeyRoute, null,
			response -> {
				try {
					String serverKey = response.getString("key");
					String combinedKey = CombineKeys.combineKeys(getResources().getString(R.string.application_key), serverKey);
					String salt = BCrypt.gensalt();
				} catch (JSONException ignore) {
				}
			}, error -> Snackbar.make(mBinding.getRoot(), R.string.impossible_to_load_questions, Snackbar.LENGTH_LONG).show());
		mRequestQueue.add(serverKeyRequest);
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

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + lastIdRoute + lang, null, response -> {
			try {
				LAST_ID.setValue(response.getInt("questionId"));
			} catch (JSONException ignore) {
			}
		}, error -> Snackbar.make(findViewById(R.id.settings_constraint_layout), getString(R.string.impossible_to_load_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v -> getLastIdFromServer()).show());
		mRequestQueue.add(jsonObjectRequest);
	}

	public void addQuestions(int lastId) {

		long lastIdInDatabase;
		if (QuestionsDatabase.getInstance(this).QuestionDAO().getAllQuestions().isEmpty()) {
			lastIdInDatabase = 0;
		} else {
			lastIdInDatabase = QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion().getId() + 1;
		}

		String apiRoute = getResources().getString(R.string.api_endpoint_getQuestion);
		String lang = mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN");

		for (long i = lastIdInDatabase; i < lastId + 1; i++) {
			String fullRoute = API_URL + apiRoute + lang + "/" + i;

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullRoute, null, response -> {
				try {
					int id = response.getInt("questionId");
					String q = response.getString("question");
					String choices = response.getString("choices");
					int category = response.getInt("category");
					int difficulty = response.getInt("difficulty");
					Question question = new Question(id, q, choices, category, difficulty);
					QuestionsDatabase.getInstance(mContext).QuestionDAO().addQuestion(question);
				} catch (JSONException ignored) {
				}
			}, error -> Snackbar.make(findViewById(R.id.drawer_layout), R.string.impossible_to_load_questions, Snackbar.LENGTH_LONG).show()) {
				@Override
				public Map<String, String> getHeaders() {
					HashMap<String, String> headers = new HashMap<>();
					headers.put("app-auth", "");
					return headers;
				}
			};
			mRequestQueue.add(jsonObjectRequest);
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
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.studio/html/jojozquizz/privacy_policy/" + mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN")));
			startActivity(launchBrowser);
		} else if (id == R.id.terms_of_service) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.studio/html/jojozquizz/terms_of_service/" + mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN")));
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
}