package com.gopivotal.pushlib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LogItemLongClickDialogFragment extends DialogFragment{

    public static final CharSequence[] items = new CharSequence[] {"Copy", "Cancel"};
    public static final int COPY = 0;
    public static final int CANCELLED = 1;
    private final Listener listener;

    public interface Listener {
        void onClickResult(int result);
    }

    public LogItemLongClickDialogFragment(Listener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log Item");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClickResult(which);
                }
            }
        });
        return builder.create();
    }
}
