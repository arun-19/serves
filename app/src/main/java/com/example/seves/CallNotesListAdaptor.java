package com.example.seves;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.activity.ComponentActivity;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.ktx.Firebase;

        import java.util.ArrayList;

public class CallNotesListAdaptor  extends ArrayAdapter<CallNotesArray> {
    Notes notes;
    Context C;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CallNotesListAdaptor (@NonNull Context context, ArrayList<CallNotesArray> arrayList) {


        super(context, 0, arrayList);
        C=context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View currentItemView = convertView;


        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.callnote_list, parent, false);
        }

        CallNotesArray currentNumberPosition = getItem(position);


        TextView title= currentItemView.findViewById(R.id.notestitle);
        title.setText(currentNumberPosition.getNotetitle());


        TextView purpose = currentItemView.findViewById(R.id.notespurpose);
        purpose.setText(currentNumberPosition.getNotepurpose());

        TextView time = currentItemView.findViewById(R.id.notestimetxt);
        time.setText(currentNumberPosition.getNotetime());


/*
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("task").document(currentNumberPosition.getid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),String.valueOf( position), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "successfully delete", Toast.LENGTH_SHORT).show();
                        Intent re = new Intent(C.getApplicationContext(),Notes.class);
                        re.putExtra("name",name.getText().toString());
                        re.putExtra("number",number.getText().toString());
                        re.putExtra("idle","1");
                        re.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP );

                        C.startActivity(re);
                        name.setText("Deleted ");
                        number.setText("Deleted ");
                        title.setText("Deleted ");
                        delete.setEnabled(false);




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
*/










        return currentItemView;
    }



}
