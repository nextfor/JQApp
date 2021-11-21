package com.jojo.jojozquizz.dialogs;

import android.app.Activity;
import android.app.Dialog;

import com.jojo.jojozquizz.R;

public class IndeterminateLoadingDialog {

	public IndeterminateLoadingDialog(Activity activity, boolean isCancelable) {
		final Dialog dialog = new Dialog(activity);
		dialog.setCancelable(isCancelable);
		dialog.setContentView(R.layout.dialog_loading);

		dialog.show();
	}
}
