package com.notesapp.mynotes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Locale;

public class notedetails extends AppCompatActivity {

    EditText titletext, contentText;
    ImageView saveButton, detelenote, backBtn, shareBtn;
    String title,content,docId;
    Boolean isEditMode = false;
    FloatingActionButton voice;
    Boolean isSpeaking = false;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notedetails);

        titletext = findViewById(R.id.notes_title);
        contentText = findViewById(R.id.contenttext);
        saveButton = findViewById(R.id.saveBtn);
        shareBtn = findViewById(R.id.shareBtn);


        backBtn = findViewById(R.id.backBtn);
        detelenote = findViewById(R.id.deleteBtn);

        voice = findViewById(R.id.voiceBtn);

        //recieve data from intent from note details
        title = getIntent().getStringExtra("noteid");
        content = getIntent().getStringExtra("notecontent");
        docId = getIntent().getStringExtra("docId");



        backBtn.setOnClickListener(v -> {

            if (textToSpeech != null && textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
            startActivity(new Intent(this, MainActivity.class));
        });


        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        if(isEditMode){
            detelenote.setVisibility(View.VISIBLE);
            voice.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.VISIBLE);
        }

        titletext.setText(title);
        contentText.setText(content);
        contentText.setMovementMethod(new ScrollingMovementMethod());

        saveButton.setOnClickListener(v->saveNote());
        detelenote.setOnClickListener(v -> showDeleteConfirmationDialog());
        shareBtn.setOnClickListener(view -> shareAsText());

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US); // Set language; adjust as needed
                } else {
                    Toast.makeText(notedetails.this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        voice.setOnClickListener(v -> speakText());

    }

    private void shareAsText() {

        String textToShare = contentText.getText().toString();

        // Create the sharing intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

        // Start the share chooser
        startActivity(Intent.createChooser(shareIntent, "Share note with"));
    }

    private void speakText() {
        if (isSpeaking) {
            // Stop TTS if it's currently speaking
            textToSpeech.stop();
            isSpeaking = false;
        } else {
            // Start speaking the text
            String text = contentText.getText().toString();
            if (!text.isEmpty()) {
                // Speak the text
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                isSpeaking = true;

                // Delay setting isSpeaking to false after speech is likely done
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    isSpeaking = false;
                }, 2000); // Adjust time delay based on average speaking time
            } else {
                Toast.makeText(this, "Content is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }



    void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(notedetails.this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");

        builder.setPositiveButton("OK", (dialog, which) -> {
            deleteNoteFromFireBase();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    void saveNote(){
        String title = titletext.getText().toString();
        String content = contentText.getText().toString();

        if(title.isEmpty()){
            titletext.setError("Title Is Required!");
            return;
        }

        note note = new note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());

        saveNoteToFireBase(note);
    }
    void saveNoteToFireBase(note note){
        DocumentReference documentReference;
        if(isEditMode){
            //to update
            documentReference = Utility.getCollectionReferenceFromNotes().document(docId);
        }else {
            //or new node
            documentReference = Utility.getCollectionReferenceFromNotes().document();//note to document reference
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   Utility.showToast(notedetails.this, "Note Saved");
                   finish();
               }else{
                   Utility.showToast(notedetails.this, "Note Saving Failed");
               }
            }
        });

    }
    void deleteNoteFromFireBase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceFromNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(notedetails.this, "Note Deleted");
                    finish();
                }else{
                    Utility.showToast(notedetails.this, "Failed To Delete Note");
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        // Shutdown TextToSpeech to release resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}