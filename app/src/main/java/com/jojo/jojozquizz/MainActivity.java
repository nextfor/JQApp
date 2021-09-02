package com.jojo.jojozquizz;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ActivityMainBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.dialogs.NiuDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.Global;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NameDialog.NameDialogListener, ClickHandler {

	static final int GAME_ACTIVITY_REQUEST_CODE = 30;
	static final int USERS_ACTIVITY_REQUEST_CODE = 40;

	String API_URL;

	final Context mContext = this;
	View mContextView;

	EditText mNumberOfQuestionsInput;

	Toolbar mToolbar;

	SharedPreferences mPreferences;

	boolean isFirstTime;
	Player mPlayer;

	RequestQueue mRequestQueue;
	Cache mCache;
	BasicNetwork mNetwork;

	MutableLiveData<Long> LAST_ID;

	ActivityMainBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mBinding.setHandler(this);

		mContextView = mBinding.mainCoordinatorLayout;

		mToolbar = mBinding.toolbar;
		setSupportActionBar(mToolbar);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		API_URL = getResources().getString(R.string.api_domain);

		mCache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		mNetwork = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(mCache, mNetwork);
		mRequestQueue.start();

		mNumberOfQuestionsInput = mBinding.activityMainNumberQuestionsInput;

		isFirstTime = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().isEmpty();

		if (isFirstTime) {
			firstTime();
		} else {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
			mBinding.setPlayer(mPlayer);
		}

		checkForUpdates();

		if (((Global) this.getApplication()).getProcessedKey() == null) {
			getServerKey();
		}
		getLastIdFromServer();

		LAST_ID = new MutableLiveData<>();
		LAST_ID.observe(this, long_number -> {
			if (QuestionsDatabase.getInstance(mContext).QuestionDAO().getAllQuestions().isEmpty() || long_number > QuestionsDatabase.getInstance(mContext).QuestionDAO().getLastQuestion().getId()) {
				addQuestions(long_number);
			}
		});
	}

	private void getServerKey() {
		String serverKeyRoute = getResources().getString(R.string.api_endpoint_getServerKey);
		JsonObjectRequest serverKeyRequest = new JsonObjectRequest(Request.Method.GET, API_URL + serverKeyRoute, null,
			response -> {
				try {
					String serverKey = response.getString("key");
					String combinedKey = CombineKeys.combineKeys(getResources().getString(R.string.application_key), serverKey);
					String salt = BCrypt.gensalt();
					((Global) mContext.getApplicationContext()).setProcessedKey(BCrypt.hashpw(combinedKey, salt));
				} catch (JSONException ignore) {
				}
			}, error -> Snackbar.make(mContextView, R.string.impossible_to_load_questions, Snackbar.LENGTH_LONG).show());
		mRequestQueue.add(serverKeyRequest);
	}

	private void firstTime() {
		String lang;
		switch (Locale.getDefault().getCountry()) {
			default:
				lang = "FR";
				break;
		}
		mPreferences.edit().putString("langage", lang).apply();
		askUsernameDialog();
	}

	private void getLastIdFromServer() {
		String lastIdRoute = getResources().getString(R.string.api_endpoint_getLastId);
		String lang = mPreferences.getString("langage", "EN");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + lastIdRoute + lang, null,
			response -> {
				try {
					LAST_ID.setValue(response.getLong("questionId"));
				} catch (JSONException ignore) {
				}
			}, error -> Snackbar.make(mContextView, R.string.impossible_to_load_questions, Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v -> getLastIdFromServer()).show()) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<>();
				String key = ((Global) mContext.getApplicationContext()).getAuthKey();
				headers.put("app-auth", key);
				return headers;
			}
		};
		mRequestQueue.add(jsonObjectRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
			mBinding.setPlayer(mPlayer);
		} else if (requestCode == USERS_ACTIVITY_REQUEST_CODE) {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
			mBinding.setPlayer(mPlayer);
		}
	}

	private void addQuestions(long lastId) {
		long lastIdInDatabase;
		if (QuestionsDatabase.getInstance(this).QuestionDAO().getAllQuestions().isEmpty()) {
			lastIdInDatabase = 0;
		} else {
			lastIdInDatabase = QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion().getId() + 1;
		}

		String apiRoute = getResources().getString(R.string.api_endpoint_getQuestion);
		String lang = mPreferences.getString("langage", "EN");

		String fullRoute = API_URL + apiRoute + lang + "/";
		for (long i = lastIdInDatabase; i < lastId + 1; i++) {

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullRoute + i, null, response -> {
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
			}, error -> Snackbar.make(mContextView, R.string.impossible_to_load_questions, Snackbar.LENGTH_LONG).show()) {
				@Override
				public Map<String, String> getHeaders() {
					HashMap<String, String> headers = new HashMap<>();
					String key = ((Global) mContext.getApplicationContext()).getAuthKey();
					headers.put("app-auth", key);
					return headers;
				}
			};
			mRequestQueue.add(jsonObjectRequest);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.menu_niu:
				new NiuDialog().showDialog(this);
				return true;
			case R.id.menu_links:
				startActivity(new Intent(mContext, LinksActivity.class));
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(mContext, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private void askUsernameDialog() {
		NameDialog nameDialog = new NameDialog();
		nameDialog.setIsNewUser(true);
		nameDialog.setIsCancelable(false);
		nameDialog.setListener(this);
		nameDialog.show(getSupportFragmentManager(), "name dialog");
	}

	private void checkForUpdates() {
		int currentCode = BuildConfig.VERSION_CODE;

		String url = getResources().getString(R.string.api_endpoint_getCurrentVersion);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + url, null, response -> {
			try {
				if (response.getInt("version") > currentCode) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

					builder.setTitle(R.string.update_available);
					builder.setMessage(R.string.update_available_text);
					builder.setPositiveButton(R.string.update, (dialog, which) -> {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
						} catch (android.content.ActivityNotFoundException activityNotFoundException) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
						}
					});
					builder.setNegativeButton(R.string.later, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			} catch (JSONException ignored) {
			}
		}, null);
		mRequestQueue.add(jsonObjectRequest);
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			askUsernameDialog();
		} else {
			Player player = new Player(name, this);
			PlayersDatabase.getInstance(this).PlayersDAO().addPlayer(player);
			mPreferences.edit().putInt("currentUserId", PlayersDatabase.getInstance(this).PlayersDAO().getIdFromName(name)).apply();
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));

			isFirstTime = false;
			mBinding.setPlayer(player);
		}
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.activity_main_start_button) {
			if (!mNumberOfQuestionsInput.getText().toString().isEmpty()) {
				int mNumberOfQuestionsAsk = Integer.parseInt(mNumberOfQuestionsInput.getText().toString());
				if (mNumberOfQuestionsAsk <= 0) {
					Toast.makeText(mContext, R.string.error_start0, Toast.LENGTH_LONG).show();
				} else if (mNumberOfQuestionsAsk > 75) {
					Toast.makeText(mContext, R.string.error_start1, Toast.LENGTH_LONG).show();
				} else if (QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion() == null) {
					Snackbar.make(mContextView, getString(R.string.no_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v1 -> getLastIdFromServer()).show();
				} else {
					startActivityForResult(new Intent(mContext, GameActivity.class).putExtra("userId", mPlayer.getId()).putExtra("numberOfQuestions", mNumberOfQuestionsAsk), GAME_ACTIVITY_REQUEST_CODE);
				}
			}
		} else if (id == R.id.button_users || id == R.id.text_display_name) {
			startActivityForResult(new Intent(mContext, PlayersActivity.class), USERS_ACTIVITY_REQUEST_CODE);
		} else if (id == R.id.activity_main_select_categories_button) {
			startActivity(new Intent(mContext, SelectCategoriesActivity.class));
		} else if (id == R.id.activity_main_bonus_button) {
			startActivity(new Intent(mContext, BonusActivity.class));
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}
}