package com.example.securebank;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;


public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
       // setContentView(R.layout.activity_account);
        setContentView(R.layout.activity_transactions_history);
        RecyclerView recyclerView = findViewById(R.id.transactionRecyclerView);

        // Create a list of transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Purchase 1", 100.0));
        transactions.add(new Transaction("Purchase 2", 50.0));
        transactions.add(new Transaction("Purchase 3", 100.0));
        transactions.add(new Transaction("Purchase 4", 50.0));
        transactions.add(new Transaction("Purchase 5", 100.0));
        transactions.add(new Transaction("Purchase 6", 50.0));
        // Add more transactions as needed

        // Create and set the adapter
        TransactionAdapter adapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(adapter);

        // Set the layout manager (e.g., LinearLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (checkBiometricSupport()) {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Please confirm your identity")
                    .setSubtitle("Place your finger on the sensor")
                    .setNegativeButtonText("Cancel")
                    .build();

            BiometricPrompt biometricPrompt = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                BiometricPrompt finalBiometricPrompt = biometricPrompt;
                biometricPrompt = new BiometricPrompt(this, getMainExecutor(),
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                // Handle authentication success
                                Toast.makeText(AccountActivity.this, "Authentication succeeded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                                    // Handle user canceling the prompt (e.g., show a message or perform a specific action)
                                    Toast.makeText(AccountActivity.this, "Authentication canceled by user", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(AccountActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                finalBiometricPrompt.cancelAuthentication();
                                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }

            biometricPrompt.authenticate(promptInfo);
        }
    }
    public void onScanChequeButtonClick(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
    private boolean checkBiometricSupport() {
        PackageManager packageManager = getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            // Device supports fingerprint sensor
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission to use biometric is granted
                return true;
            } else {
                // Request permission to use biometric
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_BIOMETRIC}, 1);
            }
        }
        return false;
    }
}
