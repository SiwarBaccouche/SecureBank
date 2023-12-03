package com.example.securebank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    EditText mEmail, mPassword, mFirstName, mLastName, mConfirmPassword;
    TextView loginText;
    Button signUpButton;
    ProgressBar progBar;
    FirebaseAuth mAuth;

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent toSignIn = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(toSignIn);
            finish();
        } else {
            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_sign_up);


        mEmail = findViewById(R.id.signupmail);
        mPassword = findViewById(R.id.signuppwd);
        mConfirmPassword = findViewById(R.id.confirmpwd);
        mFirstName = findViewById(R.id.signupname);
        mLastName = findViewById(R.id.signupname2);
        signUpButton = findViewById(R.id.signUpBtn);
        loginText = findViewById(R.id.loginTxtIntent);
        progBar = findViewById(R.id.progressBar);

        // Initialize Firebase Auth
       // mAuth = FirebaseAuth.getInstance();


        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open the sign-up activity
                Intent loginTxtIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginTxtIntent);
            }
        });




      /* if (fAuth.getCurrentUser()!=null){
            Intent SignUpnIntent = new Intent(SignUpActivity.this, AccountActivity.class);
            startActivity(SignUpnIntent);
           finish();
        }*/

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please enter a valid email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }
                progBar.setVisibility(View.VISIBLE);
//register the user
            /*   fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    if (task.isSuccessful()) {
                                        // User creation successful
                                        Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                        Intent signUpIntent = new Intent(SignUpActivity.this, AccountActivity.class);
                                        startActivity(signUpIntent);
                                    } else {
                                        // User creation failed
                                        throw task.getException(); // Throw exception to catch block
                                    }

                                } catch (Exception e) {
                                    // Handle exception
                                    Toast.makeText(SignUpActivity.this, "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace(); // Log the exception
                                }
                            }
                        }
                        );*/
            }
        });



    }
}