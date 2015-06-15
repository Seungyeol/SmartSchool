package com.aura.smartschool.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.aura.smartschool.R;

public class LoginDialogFragment extends DialogFragment {
	
	private View mView;

	public LoginDialogFragment() {

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.dialog_login, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        	.setView(mView);

		return builder.create();
	}

}
