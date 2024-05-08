 package com.notesapp.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

 public class MainActivity extends AppCompatActivity {

    FloatingActionButton addnoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    noteAdaptor noteAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        addnoteBtn = findViewById(R.id.addnote_btn);
        recyclerView = findViewById(R.id.recyclerview);
        menuBtn = findViewById(R.id.menuBtn);

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


    void showMenu(){//to display menu
        PopupMenu popupMenu= new PopupMenu(this,menuBtn);
        popupMenu.getMenu().add("Log Out");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Log Out")){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,login.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}