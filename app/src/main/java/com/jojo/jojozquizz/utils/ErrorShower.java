package com.jojo.jojozquizz.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.R;

public class ErrorShower {

	public static final int TYPE_TOAST = 0;
	public static final int TYPE_SNACKBAR = 1;
	public static final int TYPE_SNACKBAR_ACTION = 2;

	public static void showError(Context context, @Nullable View view, int type, @Nullable String message, @Nullable View.OnClickListener listener) {
		switch (type) {
			case TYPE_TOAST:
				Toast.makeText(context, message != null ? message : context.getString(R.string.toast_default_error), Toast.LENGTH_SHORT).show();
				break;
			case TYPE_SNACKBAR:
				if (view != null)
					Snackbar.make(context, view, message != null ? message : context.getString(R.string.toast_default_error), Snackbar.LENGTH_SHORT).show();
				else
					Toast.makeText(context, message != null ? message : context.getString(R.string.toast_default_error), Toast.LENGTH_SHORT).show();
				break;
			case TYPE_SNACKBAR_ACTION:
				if (view != null && listener != null)
					Snackbar.make(context, view, message != null ? message : context.getString(R.string.toast_default_error), Snackbar.LENGTH_SHORT).setAction(context.getString(R.string.all_retry), listener).show();
				else
					Toast.makeText(context, message != null ? message : context.getString(R.string.toast_default_error), Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
