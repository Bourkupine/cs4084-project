package com.example.cs4084_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnterCafeDialogFragment extends DialogFragment {

    String cafeName;
    String cafeLocation;
    EnterCafeDialogListener listener;

    public interface EnterCafeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enter_cafe, null);

        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText cafeNameEditText = (EditText)  view.findViewById(R.id.cafe_name);
                        EditText cafeLocationEditText = (EditText) view.findViewById(R.id.cafe_location);
                        cafeName = String.valueOf(cafeNameEditText.getText());
                        cafeLocation = String.valueOf(cafeLocationEditText.getText());

                        if (TextUtils.isEmpty(cafeName) || !validateInput(cafeName)) {
                            Toast.makeText(getActivity(), "Please enter a cafe name between 2 and 32 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(cafeLocation) || !validateInput(cafeLocation)) {
                            Toast.makeText(getActivity(), "Please enter a cafe location between 2 and 32 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listener.onDialogPositiveClick(EnterCafeDialogFragment.this);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (EnterCafeDialogFragment.EnterCafeDialogListener) getParentFragment();
    }

    private static boolean validateInput(String name) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^.{2,32}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);

        return matcher.matches();
    }
}