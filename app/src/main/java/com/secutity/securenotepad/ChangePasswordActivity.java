package com.secutity.securenotepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);

        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmButtonClick();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onConfirmButtonClick() {
        String oldPassword = editTextOldPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();

        SQliteHelper helper = new SQliteHelper(this);
        String storedPassword = helper.retrieveStoredPassword();

        if (!AES.decrypt(storedPassword).equals(oldPassword)) {
            Toast.makeText(this, "Incorrect old password", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isPasswordChanged = helper.updatePassword(newPassword);

        if (isPasswordChanged) {
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
        }
    }
}

