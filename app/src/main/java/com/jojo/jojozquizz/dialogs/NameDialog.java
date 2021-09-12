package com.jojo.jojozquizz.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.jojo.jojozquizz.R;

public class NameDialog extends AppCompatDialogFragment {

	private EditText mNewName;

	public static String REGEX = "^[a-zA-Z0-9]{1}[a-zA-Z0-9-\\s]{1,14}$";

	private boolean isNewUser;
	private boolean isCancelable = false;

	private NameDialogListener mainListener;

	public void setIsNewUser(boolean is) {
		isNewUser = is;
	}

	public void setIsCancelable(boolean is) {
		isCancelable = is;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = requireActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_name, null);

		mNewName = view.findViewById(R.id.dialog_name_input);

		setCancelable(isCancelable);

		TextView title = view.findViewById(R.id.dialog_progress_title);

		title.setText(isNewUser ? R.string.new_user_name_dialog_title : R.string.whats_your_name);

		builder.setView(view)
			.setTitle(isNewUser ? R.string.string_welcome : R.string.change_title_name_dialog)
			.setPositiveButton(R.string.text_confirm, (dialog, which) -> {
				String name = mNewName.getText().toString();
				mainListener.applyText(name);
			})
			.setCancelable(isCancelable);

		return builder.create();
	}

	public void setListener(NameDialogListener mainListener) {
		this.mainListener = mainListener;
	}

	public interface NameDialogListener {
		void applyText(String name);
	}
}