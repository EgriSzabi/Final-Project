package com.example.traveljournal20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth= FirebaseAuth.getInstance();
        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser=mAuth.getCurrentUser();
                if (mFireBaseUser !=null){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }


    public void logInOnClick(View view) {
        email=emailEditText.getText().toString();
        password=passwordEditText.getText().toString();

        if (email.isEmpty()){
            emailEditText.setError(getString(R.string.email_missing));
        }
        else if (password.isEmpty()){
            passwordEditText.setError(getString(R.string.password_missing));
        }
        else if (!email.isEmpty() && !password.isEmpty()){

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                      Toast.makeText(LoginActivity.this, R.string.log_in_error,Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(LoginActivity.this, R.string.log_in_success,Toast.LENGTH_SHORT).show();
                      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                      startActivity(intent);

                    }
                }
            });

        }
        else {
            Toast.makeText(LoginActivity.this, R.string.error,Toast.LENGTH_SHORT).show();
        }

    }


    public void goToSignUp(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }





}