package com.example.android.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

    //Supply keys for the Bundle
    public static final String TITLE_ID = "title";
    public static final String MESSAGE_ID = "message";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Get supplied title and message body.
        Bundle messages = getArguments();

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(messages.getString(TITLE_ID));
        builder.setMessage(messages.getString(MESSAGE_ID));
        builder.setPositiveButton(context.getString(R.string.error_button_ok), null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
