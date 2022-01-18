package com.jojo.jojozquizz;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ActivityMainBinding;
import com.jojo.jojozquizz.databinding.FragmentMainBinding;
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
import com.jojo.jojozquizz.utils.NetUtils;
import com.jojo.jojozquizz.utils.QuestionsRequestsHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements QuestionsRequestsHelper.ResponseListener {

	static final int GAME_ACTIVITY_REQUEST_CODE = 30;
	static final int USERS_ACTIVITY_REQUEST_CODE = 40;

	static final int PROCESS_GEN_TOKEN = 0;
	static final int PROCESS_FETCH_LASTID = 1;
	static final int PROCESS_FETCH_CONTENT = 2;
	static final int PROCESS_FETCH_EVENTS = 3;
	int currentProcess = -1;

	final Context mContext = this;
	View mContextView;

	SharedPreferences mPreferences;

	boolean isFirstTime;
	Player player;
	String lang;

	String mSecurityKey;

	private long lastId;

	ActivityMainBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_JojozQuizz);
		setContentView(R.layout.activity_main);

		setSupportActionBar(new Toolbar(this));

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		com.jojo.jojozquizz.utils.MainActivity.getInstance().setPreferences(mPreferences);

		isFirstTime = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().isEmpty();

		HashMap<String, String> args = com.jojo.jojozquizz.utils.MainActivity.getInstance().getArgs();
		args.put("isFirstTime", String.valueOf(isFirstTime));
		com.jojo.jojozquizz.utils.MainActivity.getInstance().setArgs(args);
		if (isFirstTime) {
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
			player = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
			com.jojo.jojozquizz.utils.MainActivity.getInstance().setPlayer(player);
		}

		if (NetUtils.isConnected(mContext)) {
			mBinding.llFetchStatus.setVisibility(View.VISIBLE);
			mBinding.tvFetchStatus.setText("Initialisation . . .");
			checkForUpdates();
			getServerKey();
		}

		initView();
	}

	private void initView() {
		MainFragment fragment = new MainFragment();
		getSupportFragmentManager().beginTransaction()
			.add(R.id.frame_layout_main, fragment)
			.commitAllowingStateLoss();
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

	private void getLastId() {
		QuestionsRequestsHelper.getLastId(mContext, lang, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		player = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
			mBinding.setPlayer(player);
			if (player.getGamesPlayed() >= 3 && mPreferences.getBoolean(getString(R.string.PREF_LANGUAGE), true)) {
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
			player = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt(getString(R.string.PREF_CURRENT_USER_ID), 1));
			mBinding.setPlayer(player);
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

		if (lastId != lastIdInDatabase) {
			mBinding.tvFetchStatus.setText("Récupération des dernières questions depuis le serveur");
			this.lastId = lastId;
			for (long i = lastIdInDatabase; i < lastId; i++) {
				QuestionsRequestsHelper.getQuestion(mContext, lang, i, this);
			}
		} else {
			dismissStatusBar();
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

	private void dismissStatusBar() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top);
				mBinding.llFetchStatus.setAnimation(anim);
				mBinding.llFetchStatus.setVisibility(View.GONE);
			}
		}, 3000);
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
	public void onIdResponse(Call<LastIdResponse> call, Response<LastIdResponse> response) {
		if (response.code() == 200) {
			addQuestions(response.body().getQuestionId());
		} else {
			ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
		}
	}

	@Override
	public void onIdFailure(Call<LastIdResponse> call, Throwable t) {
		ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);
	}

	@Override
	public void onQuestionResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
		if (response.code() == 200 && response.body() != null) {
			Question question = new Question(response.body());
			QuestionsDatabase.getInstance(mContext).QuestionDAO().addQuestion(question);

			if (this.lastId == question.getId()) {
				mBinding.llFetchStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.fetch_success_status));
				mBinding.tvFetchStatus.setText("Récupération terminée");
				dismissStatusBar();
			}
		} else {
			ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR_ACTION, getString(R.string.impossible_to_load_questions), v -> retryProcess());
		}
	}

	@Override
	public void onQuestionFailure(Call<QuestionResponse> call, Throwable t) {
		ErrorShower.showError(mContext, mContextView, ErrorShower.TYPE_SNACKBAR, getString(R.string.impossible_to_load_questions), null);

		mBinding.llFetchStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.fetch_failed_status));
		mBinding.tvFetchStatus.setText("Récupération échouée");
		dismissStatusBar();
	}
}