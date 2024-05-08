package com.notesapp.mynotes;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class noteAdaptor extends FirestoreRecyclerAdapter<note,noteAdaptor.noteViewHoldr> {


    private final Context context;

    public noteAdaptor(@NonNull FirestoreRecyclerOptions<note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull noteViewHoldr holder, int i, @NonNull note note) {
         ;
        holder.titletext.setText(note.title);
        holder.contenttextt.setText(note.content);
        holder.timetext.setText(Utility.timestamptoString(note.timestamp));

        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context,notedetails.class);
            intent.putExtra("noteid",note.title);
            intent.putExtra("notecontent",note.content);
            String docId = this.getSnapshots().getSnapshot(i).getId();
            intent.putExtra("docId",docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public noteViewHoldr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_note_item,parent,false);
        return new noteViewHoldr(view);
    }

    class noteViewHoldr extends RecyclerView.ViewHolder{

        TextView titletext,contenttextt,timetext;
        public noteViewHoldr(@NonNull View itemView) {
            super(itemView);
            titletext=itemView.findViewById(R.id.noteTitle);
            contenttextt=itemView.findViewById(R.id.noteContent);
            timetext=itemView.findViewById(R.id.noteTimestamp);


        }
    }
}
