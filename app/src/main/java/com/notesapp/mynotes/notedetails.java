package com.notesapp.mynotes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class notedetails extends AppCompatActivity {

    EditText titleText, contentText;
    ImageView saveButton;
    TextView titletext,detelenote;
    String title,content,docId;
    Boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notedetails);

        titleText = findViewById(R.id.notes_title);
        contentText = findViewById(R.id.contenttext);
        saveButton = findViewById(R.id.saveBtn);

        titletext = findViewById(R.id.page_title);
        detelenote = findViewById(R.id.deleteBtn);

        //recieve data from intent from note details
        title = getIntent().getStringExtra("noteid");
        content = getIntent().getStringExtra("notecontent");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        if(isEditMode){
            titleText.setText("Edit Your Note");
            detelenote.setVisibility(View.VISIBLE);

        }

        titleText.setText(title);
        contentText.setText(content);

        saveButton.setOnClickListener(v->saveNote());
        detelenote.setOnClickListener(v -> deleteNoteFromFireBase());
    }


    void saveNote(){
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        if(title.isEmpty() || title==null){
            titleText.setError("Title Is Required!");
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
}