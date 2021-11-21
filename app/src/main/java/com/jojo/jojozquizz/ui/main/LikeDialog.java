package com.jojo.jojozquizz.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jojo.jojozquizz.R;

import java.util.ArrayList;

public class LikeDialog extends MaterialAlertDialogBuilder {

	private Context ct;
	private ArrayList<LikeDialogListeners> mListeners;

	public LikeDialog(@NonNull Context context) {
		super(context);
		super.setTitle(R.string.dialog_content_rate_app)
			.setMessage(R.string.dialog_content_rate_app_message)
			.setNeutralButton(R.string.dialog_content_never_ask_again, (dialog, wich) -> {
				for (LikeDialogListeners l : mListeners) {
					// -1 : Never | 0 : Later | 1 : Yes
					l.onSubmit(-1);
				}
			}).setNegativeButton(R.string.dialog_content_ask_later, ((dialog, which) -> {
			for (LikeDialogListeners l : mListeners) {
				l.onSubmit(0);
				dialog.dismiss();
			}
		})).setPositiveButton(R.string.dialog_content_rate_it, ((dialog, which) -> {
			for (LikeDialogListeners l : mListeners) {
				l.onSubmit(1);
			}
		})).create();
	}

	public void popin() {
		super.show();
	}

	public void addListener(LikeDialogListeners l) {
		mListeners.add(l);
	}

	public interface LikeDialogListeners {
		void onSubmit(int result);
	}
}
