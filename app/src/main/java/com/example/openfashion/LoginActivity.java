package com.example.openfashion;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    AppCompatEditText emailEditText, passwordEditText;
    AppCompatCheckBox rememberMeCheckBox;

    AppCompatButton submitButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        submitButton = findViewById(R.id.submit_button);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                submitButton.setError(null);

                emailEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
                submitButton.setEnabled(false);

                String email, password;

                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                if(TextUtils.isEmpty(email)){
                    submitButton.setError("Email Can Not Be Empty.");
                    Toast.makeText(LoginActivity.this, "Email Can Not Be Empty.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    submitButton.setError("Password Can Not Be Empty.");
                    Toast.makeText(LoginActivity.this, "Password Can Not Be Empty.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 5){
                    submitButton.setError("Password Must Be More Than 6 Characters.");
                    Toast.makeText(LoginActivity.this, "Password Must Be More Than 6 Characters.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Check Whether email = admin@openfashion.com and password = admin123 or not
                // if yes, then it will go to Admin Dashboard
                if (email.equals("admin@openfashion.com") && password.equals("admin123")) {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    finish();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Please Try Again.",
                                            Toast.LENGTH_SHORT).show();
                                    emailEditText.setEnabled(true);
                                    passwordEditText.setEnabled(true);
                                    submitButton.setEnabled(true);
                                }
                            }
                        });
            }
        });

    }

}