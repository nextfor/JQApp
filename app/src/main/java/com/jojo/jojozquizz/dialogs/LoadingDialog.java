package com.jojo.jojozquizz.dialogs;

import android.app.Activity;
import android.app.Dialog;

import com.jojo.jojozquizz.databinding.DialogLoadingBinding;

public class LoadingDialog {

	private Dialog dialog;
	private DialogLoadingBinding binding;

	public LoadingDialog(Activity activity, boolean isCancelable, int max) {
		binding = DialogLoadingBinding.inflate(activity.getLayoutInflater(), null, false);
		binding.progressBar2.setMax(max);
		dialog = new Dialog(activity);
		dialog.setCancelable(isCancelable);
		dialog.setContentView(binding.getRoot());
		dialog.show();
	}

	public void increment() {
		binding.progressBar2.setProgress(binding.progressBar2.getProgress() + 1);
		if (binding.progressBar2.getProgress() >= binding.progressBar2.getMax()) {
			dialog.dismiss();
		}
	}
}
