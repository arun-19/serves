package com.example.seves;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class TaskPendingView {

    String i;
    String android_id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AlertDialog.Builder builder1;
    AlertDialog alert;

    public void getTask(Context c, int taskcount,String number) {

            android_id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
            builder1 = new AlertDialog.Builder(c);

            builder1.setIcon(R.drawable.baseline_note_add_24);
            builder1.setTitle("Set Shedule");
            builder1.setCancelable(false);

            LayoutInflater inflater = LayoutInflater.from(c);
            View view = inflater.inflate(R.layout.taskpending, null);


            builder1.setView(view);
            //  View view=inflater.inflate(R.layout.popup,null);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViews = view.findViewById(R.id.count);
            textViews.setText("PendingTask" + taskcount);


            builder1.setMessage("Task Available");


            builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                dialog.dismiss();
                Intent i=new Intent(c,Notes.class);
                i.putExtra("number",number);
                i.putExtra("name","Task");
                i.putExtra("idle","1");
                c.startActivity(i);


            });
            builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

                dialog.cancel();
            });

            alert = builder1.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            alert.cancel();
            alert.show();


        }

}






