package com.notesapp.mynotes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Splash extends AppCompatActivity {

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        mainLayout = findViewById(R.id.splash);

        // Apply blur effect initially
        applyBlurEffect(mainLayout, 30f);

        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Biometric authentication is available and ready to use
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Utility.showToast(this, "No biometric features available on this device");
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Utility.showToast(this, "Biometric hardware is currently unavailable");
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Utility.showToast(this, "No biometric credentials enrolled");
                break;

            default:
                Utility.showToast(this, "Biometric authentication is not supported");
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Splash.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Splash.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Splash.this, "Welcome User", Toast.LENGTH_SHORT).show();
                removeBlurEffect(mainLayout); // Remove blur on success
                mainLayout.setVisibility(LinearLayout.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Splash.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("My Notes")
                .setDescription("USE YOUR SCREENLOCK AUTHENTICATIONS")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    startActivity(new Intent(Splash.this, login.class));
                    finish();
                } else {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                }
            }
        }, 3000);
    }

    // Apply blur effect (Android 31+)
    private void applyBlurEffect(LinearLayout view, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(radius, radius, android.graphics.Shader.TileMode.CLAMP));
        }
    }

    // Remove blur effect
    private void removeBlurEffect(LinearLayout view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null);
        }
    }
}
