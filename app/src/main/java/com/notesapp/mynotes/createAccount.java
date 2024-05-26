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

public class createAccount extends AppCompatActivity {

    EditText emailtext,passwordtext,c_passwordtext;
    MaterialButton create_act;
    TextView login;
    ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        emailtext = findViewById(R.id.email);
        passwordtext = findViewById(R.id.password);
        c_passwordtext = findViewById(R.id.c_password);
        create_act = findViewById(R.id.create_accountBtn);
        login = findViewById(R.id.gotologin_text);
        load = findViewById(R.id.progressbar);

        create_act.setOnClickListener(v-> create_Account());
        login.setOnClickListener(v->startActivity(new Intent(this,login.class)));


    }
    void create_Account(){
        String email = emailtext.getText().toString();
        String password = passwordtext.getText().toString();
        String c_password = c_passwordtext.getText().toString();

        boolean is_valid = validateData(email,password,c_password);
        if(!is_valid){
            return;
        }
        createAccountInFireBase(email,password);
    }
    void createAccountInFireBase(String email, String password){
        changeProgressBar(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(createAccount.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeProgressBar(false);
                        if(task.isSuccessful()){
                            Utility.showToast(createAccount.this,"Account created successfully! Check Email For Verification");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            startActivity(new Intent(createAccount.this,login.class)); 
                        }else{
                            Utility.showToast(createAccount.this,task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void changeProgressBar(boolean inProgress){
        if(inProgress){
            load.setVisibility(View.VISIBLE);
            create_act.setVisibility(View.GONE);
        }else{
            load.setVisibility(View.GONE);
            create_act.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String c_password) {
        if (email.isEmpty() || password.isEmpty() || c_password.isEmpty()) {
            return false;
        }
        if (password.length() < 7 || c_password.length()<7) {
            passwordtext.setError("Password must be at least 7 characters");
            return false;
        }
        if (!password.equals(c_password)) {
            c_passwordtext.setError("Password does not match");
            return false;
        }
        return true;
    }
}