package com.notesapp.mynotes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends AppCompatActivity {
    EditText emailtext, passwordtext;
    MaterialButton login_btn;
    TextView createact ,forgotpassword;
    ProgressBar load;
    private FirebaseAuth mAuth;
    private EditText passwordEditText;
    private ImageButton passwordToggle;
    private boolean isPasswordVisible = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailtext = findViewById(R.id.email);
        passwordtext = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_accountBtn);
        createact = findViewById(R.id.gotoregister_text);
        forgotpassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        load = findViewById(R.id.progressbar);

        passwordEditText = findViewById(R.id.password);
        passwordToggle = findViewById(R.id.password_toggle);

        login_btn.setOnClickListener(v->loginUser());
        createact.setOnClickListener(v->startActivity(new Intent(this,createAccount.class)));
        forgotpassword.setOnClickListener(v -> showResetPasswordDialog());

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    // Show password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                passwordEditText.setSelection(passwordEditText.length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Input field for email
        final EditText input = new EditText(this);
        input.setHint("Enter your email");
        builder.setView(input);

        // Set up the Reset Password button
        builder.setPositiveButton("Send Reset Email", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                sendPasswordResetEmail(email);
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
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

    void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email sent! Please check email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
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