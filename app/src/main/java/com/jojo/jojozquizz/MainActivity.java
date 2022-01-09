package com.jojo.jojozquizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ActivityMainBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.model.reponse.EventResponse;
import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;
import com.jojo.jojozquizz.model.reponse.ServerKeyResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;
import com.jojo.jojozquizz.ui.game.GameActivity;
import com.jojo.jojozquizz.ui.main.LikeDialog;
import com.jojo.jojozquizz.ui.players.PlayersActivity;
import com.jojo.jojozquizz.utils.Client;
import com.jojo.jojozquizz.utils.ErrorShower;
import com.jojo.jojozquizz.utils.QuestionsRequestsHelper;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NameDialog.NameDialogListener, ClickHandler, QuestionsRequestsHelper.ResponseListener {

	static final int GAME_ACTIVITY_REQUEST_CODE = 30;
	static final int USERS_ACTIVITY_REQUEST_CODE = 40;

	static final int PROCESS_GEN_TOKEN = 0;
	static final int PROCESS_FETCH_LASTID = 1;
	static final int PROCESS_FETCH_CONTENT = 2;
	static final int PROCESS_FETCH_EVENTS = 3;
	int currentProcess = -1;

	final Context mContext = this;
	View mContextView;

	EditText mNumberOfQuestionsInput;

	Toolbar mToolbar;

	SharedPreferences mPreferences;

	boolean isFirstTime;
	Player mPlayer;
	String lang;

	String mSecurityKey;

	ActivityMainBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_JojozQuizz);
		setContentView(R.layout.activity_main);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mBinding.setHandler(this);

		mContextView = mBinding.mainCoordinatorLayout;

		mToolbar = mBinding.toolbar;
		setSupportActionBar(mToolbar);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		lang = mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN");

		mNumberOfQuestionsInput = mBinding.activityMainNumberQuestionsInput;

		isFirstTime = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().isEmpty();

		if (isFirstTime) {
			firstTime();
		} else {
			if (mPreferences.getString(getString(R.string.PREF_LANGUAGE), "EN") == null) {
				String lang;
				switch (Locale.getDefault().getCountry()) {
					default:
						lang = "FR";
						break;
				}
				mPreferences.edit().putString(getString(R.string.PREF_LANGUAGE), lang).putString(getString(R.string.PREF_LANGUAGE), null).apply();
			}
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
			mBinding.setPlayer(mPlayer);
		}

		checkForUpdates();

		getServerKey();
	}

	@Override
	protected void onStart() {
		super.onStart();

		String[] texts = getResources().getStringArray(R.array.splash_texts);
		Random r = new Random();

		mBinding.activityMainTextTitle.setText(texts[r.nextInt(texts.length)]);
	}

	private void getServerKey() {
		currentProcess = PROCESS_GEN_TOKEN;
		Call<ServerKeyResponse> key = Client.getClient(mContext).getApi().getServerKey();
		key.enqueue(new Callback<ServerKeyResponse>() {
			@Override
			public void onResponse(Call<ServerKeyResponse> call, Response<ServerKeyResponse> response) {
				if (response.code() == 200) {
					String serverKey = response.body().getKey();
					String combinedKey = CombineKeys.combineKeys(getResources().getString(R.string.application_key), serverKey);
					String salt = BCrypt.gensalt();
					mSecurityKey = BCrypt.hashpw(combinedKey, salt);
					Client.getClient(mContext).addInterceptor(mSecurityKey);
					getLastId();
					getEvents();
				} else {
					ErrorShower.showError(mContext, null, ErrorShower.TYPE_SNACKBAR, null, null);
				}
			}

			@Override
			public void onFailure(Call<ServerKeyResponse> call, Throwable t) {
				ErrorShower.showError(mContext, null, ErrorShower.TYPE_SNACKBAR, null, null);
			}
		});
	}

	private void getEvents() {
		Call<List<EventResponse>> call = Client.getClient(mContext).getApi().getEvents();
		call.enqueue(new Callback<List<EventResponse>>() {
			@Override
			public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
				Log.d("TAG", "onResponse: ");
			}

			@Override
			public void onFailure(Call<List<EventResponse>> call, Throwable t) {
			}
		});
	}

	private void getLastId() {
		QuestionsRequestsHelper.getLastId(mContext, lang, this);
	}

	private void firstTime() {
		String lang;
		switch (Locale.getDefault().getCountry()) {
			default:
				lang = "FR";
				break;
		}
		mPreferences.edit().putString(getString(R.string.PREF_LANGUAGE), lang).apply();
		askUsernameDialog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
			mBinding.setPlayer(mPlayer);
			if (mPlayer.getGamesPlayed() >= 3 && mPreferences.getBoolean(getString(R.string.PREF_LANGUAGE), true)) {
				LikeDialog likeDialog = new LikeDialog(this);
				likeDialog.addListener(result -> {
					switch (result) {
						case -1:
							mPreferences.edit().putBoolean(getString(R.string.PREF_WANTS_RATE_APP), false).apply();
							break;
						case 1:
							mPreferences.edit().putBoolean(getString(R.string.PREF_WANTS_RATE_APP), false).apply();
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
							} catch (android.content.ActivityNotFoundException activityNotFoundException) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
							}
							break;
					}
				});
				likeDialog.popin();
			}
		} else if (requestCode == USERS_ACTIVITY_REQUEST_CODE) {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
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
		currentProcess = PROCESS_FETCH_CONTENT;

		for (long i = lastIdInDatabase; i < lastId; i++) {
			QuestionsRequestsHelper.getQuestion(mContext, lang, i, this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
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

		Call<VersionResponse> call = Client.getClient(mContext).getApi().getCurrentVersion();
		call.enqueue(new Callback<VersionResponse>() {
			@Override
			public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
				if (response.body().getVersion() > currentCode) {
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
			}

			@Override
			public void onFailure(Call<VersionResponse> call, Throwable t) {
			}
		});
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			Snackbar.make(mBinding.getRoot(), R.string.invalid_name, Snackbar.LENGTH_SHORT).show();
			askUsernameDialog();
		} else {
			Player player = new Player(name, this);
			PlayersDatabase.getInstance(this).PlayersDAO().addPlayer(player);
			mPreferences.edit().putInt(getString(R.string.PREF_CURRENT_USER_ID), PlayersDatabase.getInstance(this).PlayersDAO().getIdFromName(name)).apply();
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));

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
					Snackbar.make(mContextView, getString(R.string.no_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v1 -> getLastId()).show();
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

	private void retryProcess() {
		switch (currentProcess) {
			case PROCESS_GEN_TOKEN:
				getServerKey();
			case PROCESS_FETCH_LASTID:
				getServerKey();
			case PROCESS_FETCH_CONTENT:
				getLastId();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}

	@Override
	public void onIdResponse(Call<LastIdResponse> call, Response<LastIdResponse> response) {
		if (response.code() == 200) {
			addQuestions(response.body().getQuestionId());
		} else {
			ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
		}
	}

	@Override
	public void onIdFailure(Call<LastIdResponse> call, Throwable t) {
		ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions) + " 1", null);
	}

	@Override
	public void onQuestionResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
		if (response.code() == 200) {
			Question question = new Question(response.body());
			QuestionsDatabase.getInstance(mContext).QuestionDAO().addQuestion(question);
		} else {
			ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR_ACTION, getString(R.string.impossible_to_load_questions) + " 2", v -> retryProcess());
		}
	}

	@Override
	public void onQuestionFailure(Call<QuestionResponse> call, Throwable t) {
		ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
	}
}