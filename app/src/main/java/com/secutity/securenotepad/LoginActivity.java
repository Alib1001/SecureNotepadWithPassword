package com.secutity.securenotepad;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import net.sqlcipher.database.SQLiteDatabase;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private EditText etEnteredPassword;
    private String storedPassword;

    private FingerprintManagerCompat fingerprintManager;
    private CancellationSignal cancellationSignal;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Login");

        fingerprintManager = FingerprintManagerCompat.from(this);

        Button btnFingerprint = findViewById(R.id.btnFingerprint);
        btnFingerprint.setOnClickListener(view -> authenticateWithFingerprint());

        etEnteredPassword = findViewById(R.id.password);

        String passwordID = getIntent().getStringExtra("password");
        try {
            fetchDataFromDatabase(passwordID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> {
            String enteredPassword = etEnteredPassword.getText().toString();
            if (enteredPassword.equals(storedPassword)) {
                startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDatabase(String passwordId) throws Exception {
        SQliteHelper helper = new SQliteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase("password");

        Cursor cursor = db.rawQuery("select * from user where password= '" + passwordId + "'", new String[]{});

        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() > 0) {
            do {
                int id = cursor.getInt(0);
                storedPassword = cursor.getString(1);

                if (!storedPassword.isEmpty()) {
                    storedPassword = AES.decrypt(storedPassword);
                } else {
                    storedPassword = "";
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void authenticateWithFingerprint() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED) {
            FingerprintManagerCompat.AuthenticationCallback authenticationCallback =
                    new FingerprintManagerCompat.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            // Fingerprint authentication successful
                            startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            // Handle authentication error
                            Toast.makeText(LoginActivity.this, "Fingerprint authentication error: " + errString, Toast.LENGTH_SHORT).show();
                        }
                    };

            CancellationSignal cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(null, 0, cancellationSignal, authenticationCallback, null);
        } else {
            // Request the USE_BIOMETRIC permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.USE_BIOMETRIC}, 1);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            authenticateWithFingerprint();
        }
    }


}

