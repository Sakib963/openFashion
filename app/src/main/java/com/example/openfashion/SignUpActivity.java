package com.example.openfashion;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    AppCompatEditText emailEditText, passwordEditText, retypePasswordEditText, nameEditText, photoUrlEditText;
    AppCompatButton submit_button;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.email_edit_text);
        submit_button = findViewById(R.id.submit_button);
        passwordEditText = findViewById(R.id.password_edit_text);
        retypePasswordEditText = findViewById(R.id.retype_password_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        photoUrlEditText = findViewById(R.id.photo_url_edit_text);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                submit_button.setError(null);

                emailEditText.setEnabled(false);
                submit_button.setEnabled(false);
                passwordEditText.setEnabled(false);
                retypePasswordEditText.setEnabled(false);
                nameEditText.setEnabled(false);
                photoUrlEditText.setEnabled(false);


                String name, email, password, confirmPassword, photoURL;
                name = String.valueOf(nameEditText.getText());
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                confirmPassword = String.valueOf(retypePasswordEditText.getText());
                photoURL = String.valueOf(photoUrlEditText.getText());

                if(TextUtils.isEmpty(name)){
                    submit_button.setError("Name Field is Required");
                    Toast.makeText(SignUpActivity.this, "Name Field is Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    submit_button.setError("You Must Provide Email Address.");
                    Toast.makeText(SignUpActivity.this, "You Must Provide Email Address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    submit_button.setError("You Must Provide Password.");
                    Toast.makeText(SignUpActivity.this, "You Must Provide Password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    submit_button.setError("Retype Your Password.");
                    Toast.makeText(SignUpActivity.this, "Retype Your Password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!TextUtils.equals(password, confirmPassword)){
                    submit_button.setError("Password didn't match.");
                    Toast.makeText(SignUpActivity.this, "Password didn't match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .setPhotoUri(Uri.parse(photoURL))
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User profile updated.");

                                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    }
                                                    else{
                                                        Toast.makeText(SignUpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }
        });
    }
}