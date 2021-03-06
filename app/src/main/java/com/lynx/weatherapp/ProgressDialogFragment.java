package com.lynx.weatherapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by WORK on 19.08.2015.
 */
public class ProgressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                                                            getString(R.string.progress_dialog_title),
                                                            getString(R.string.progress_dialog_msg), true);
        return progressDialog;
    }

}
