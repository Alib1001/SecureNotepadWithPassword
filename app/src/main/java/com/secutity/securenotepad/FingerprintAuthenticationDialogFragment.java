package com.secutity.securenotepad;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import javax.crypto.Cipher;

public class FingerprintAuthenticationDialogFragment extends DialogFragment {

    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private Cipher cipher;

    private TextView textViewStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprint_authentication, container, false);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startFingerprintAuthentication();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopFingerprintAuthentication();
    }

    private void startFingerprintAuthentication() {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                updateStatus("Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                updateStatus("Authentication help: " + helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                updateStatus("Authentication failed. Try again.");
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                updateStatus("Authentication succeeded!");
                dismiss();
                // Handle successful authentication, e.g., start the main activity
                startActivity(new Intent(getActivity(), TaskListActivity.class));
                getActivity().finish();
            }
        }, null);
    }

    private void stopFingerprintAuthentication() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    private void updateStatus(String status) {
        if (textViewStatus != null) {
            textViewStatus.setText(status);
        }
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
