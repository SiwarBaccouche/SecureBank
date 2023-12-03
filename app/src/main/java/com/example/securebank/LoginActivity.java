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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    TextView signUpText;
    Button loginButton;
    ProgressBar progBar;
    FirebaseAuth mAuth;
    public void updateUI(FirebaseUser user)
    {
        //add if statement
        if (user!=null)
        {
            Intent toHomePage = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(toHomePage);
            finish();
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Please verify your email and address.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
      //  FirebaseApp.initializeApp(this);

        getSupportActionBar().hide();

        //mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.loginmail);
        mPassword = findViewById(R.id.loginpwd);
        progBar = findViewById(R.id.progressBar3);
        loginButton = findViewById(R.id.logInBtn);
        signUpText = findViewById(R.id.loginTxtIntent);


        loginButton.setOnClickListener(new View.OnClickListener() {
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

                //authenticate the user
               /* fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                // User creation successful
                                Toast.makeText(LoginActivity.this, "Connected successfully", Toast.LENGTH_SHORT).show();
                                Intent signUpIntent = new Intent(LoginActivity.this, AccountActivity.class);
                                startActivity(signUpIntent);
                            } else {
                                // User creation failed
                                throw task.getException(); // Throw exception to catch block
                            }
                        } catch (Exception e) {
                            // Handle exception
                            Toast.makeText(LoginActivity.this, "Failed to connect: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace(); // Log the exception
                        }
                    }
                                                                                        }
                );*/
            }
        });


        signUpText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create an Intent to open the sign-up activity
                        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class); // Replace "SignUpActivity" with the actual name of your sign-up activity.
                        startActivity(signUpIntent);
                    }
                });
            }
        }