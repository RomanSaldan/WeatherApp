package com.lynx.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by WORK on 18.08.2015.
 */
public class ErrorDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.err_dialog_title)
                .setMessage(R.string.err_dialog_msg)
                .create();
    }
}
