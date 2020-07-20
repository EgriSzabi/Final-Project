package com.example.traveljournal20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private String userID;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Sign Up");

        mAuth= FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();

        firstNameEditText=findViewById(R.id.firstNameEditText);
        lastNameEditText=findViewById(R.id.lastNameEditText);
        passwordEditText=findViewById(R.id.signUpPasswordEditText);
        emailEditText=findViewById(R.id.signUpEmailEditText);

    }

    public void signUpOnClick(View view) {
        firstName=firstNameEditText.getText().toString();
        lastName=lastNameEditText.getText().toString();
        password=passwordEditText.getText().toString();
        email=emailEditText.getText().toString();
        if (firstName.isEmpty()){
            firstNameEditText.setError(getString(R.string.first_name_missing));
        }
        else if (lastName.isEmpty()){
            lastNameEditText.setError(getString(R.string.last_name_missing));
        }
        else if (email.isEmpty()){
            emailEditText.setError(getString(R.string.username_missing));
        }
        else if (password.isEmpty() ){
            passwordEditText.setError(getString(R.string.password_missing));
        }
        else if(password.length()<6){
            passwordEditText.setError(getString(R.string.password_to_short));
        }

       else if (!email.isEmpty() && !password.isEmpty() && !lastName.isEmpty() && !firstName.isEmpty() ){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, R.string.sign_up_fail, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        userID= mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection(getString(R.string.users)).document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put(getString(R.string.firstName),firstName);
                        user.put(getString(R.string.lastName),lastName);
                        user.put(getString(R.string.email),email);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SignUpActivity.this, R.string.user_added_to_database+ userID,Toast.LENGTH_LONG).show();
                            }
                        });
                        Toast.makeText(SignUpActivity.this, R.string.sign_up_success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

                    }
                }
            });
        }

       else
        {
            Toast.makeText(SignUpActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }


}
