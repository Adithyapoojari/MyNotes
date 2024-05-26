package com.notesapp.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends AppCompatActivity {
    EditText emailtext,passwordtext;
    MaterialButton login_btn;
    TextView createact;
    ProgressBar load;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailtext = findViewById(R.id.email);
        passwordtext = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_accountBtn);
        createact = findViewById(R.id.gotoregister_text);
        load = findViewById(R.id.progressbar);

        login_btn.setOnClickListener(v->loginUser());
        createact.setOnClickListener(v->startActivity(new Intent(this,createAccount.class)));


    }
    void loginUser(){
        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();


        boolean is_valid = validateData(email,password);
        if(!is_valid){
            return;
        }
        loginAccountInFireBase(email,password);
    }

    void loginAccountInFireBase(String email, String password) {
        changeProgressBar(true);
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(login.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeProgressBar(false);
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(login.this,MainActivity.class));
                                finish();
                            }else{
                                Utility.showToast(login.this,
                                        "Email Not Verified!Please Verify Your Email");
                            }
                        }else{
                            Utility.showToast(login.this,task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void changeProgressBar(boolean inProgress){
        if(inProgress){
            load.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }else{
            load.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (password.length() < 7) {
            passwordtext.setError("Password is constrained to more than 7 characters");
            return false;
        }

        return true;
    }
}