 package com.notesapp.mynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.concurrent.Executor;

 public class MainActivity extends AppCompatActivity {

    FloatingActionButton addnoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    noteAdaptor noteAdaptor;
    /*BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    RelativeLayout mainLayout;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

       // mainLayout = findViewById(R.id.main);

        addnoteBtn = findViewById(R.id.addnote_btn);
        recyclerView = findViewById(R.id.recyclerview);
        menuBtn = findViewById(R.id.menuBtn);

        /*BiometricManager biometricManager=BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Biometric authentication is available and ready to use so no need any action
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

        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
                mainLayout.setVisibility(RelativeLayout.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("My Notes")
                .setDescription("USE YOUR SCREENLOCK AUTHENTICATIONS")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);*/


        menuBtn.setOnClickListener(v -> showMenu());
        setupRecyclerView();


        addnoteBtn.setOnClickListener(v-> startActivity(new Intent(this, notedetails.class)));
    }

    void setupRecyclerView(){//to set the data from firebase
        Query  query= Utility.getCollectionReferenceFromNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<note> options = new FirestoreRecyclerOptions.Builder<note>()
                .setQuery(query,note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdaptor  = new noteAdaptor(options,this);
        recyclerView.setAdapter(noteAdaptor);
    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdaptor.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        noteAdaptor.stopListening();
    }
    @Override
    protected void onResume() {
        super.onResume();
        noteAdaptor.notifyDataSetChanged();
    }


     void showMenu() { // to display menu
         PopupMenu popupMenu = new PopupMenu(this, menuBtn);

         // Add menu options
         popupMenu.getMenu().add("Log Out");
         popupMenu.getMenu().add("Feedback");

         // Show the popup menu
         popupMenu.show();

         // Handle menu item clicks
         popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 if (item.getTitle().equals("Log Out")) {
                     FirebaseAuth.getInstance().signOut();
                     startActivity(new Intent(MainActivity.this, login.class));
                     finish();
                     return true;
                 } else if (item.getTitle().equals("Feedback")) {
                     // Open feedback link
                     Intent feedbackIntent = new Intent(Intent.ACTION_VIEW,
                             Uri.parse("https://ewi1f5vz6s8.typeform.com/to/A24zo7TD"));
                     startActivity(feedbackIntent);
                     return true;
                 }
                 return false;
             }
         });
     }

 }