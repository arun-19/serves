package com.example.seves;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class customArrayAdaptor extends ArrayAdapter<logArray> {

    AlertDialog.Builder builder1;
    AlertDialog alert;
    public customArrayAdaptor(@NonNull Context context, ArrayList<logArray> arrayList) {


        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View currentItemView = convertView;


        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_view, parent, false);
        }

        logArray currentNumberPosition = getItem(position);


        TextView name = currentItemView.findViewById(R.id.listviewnameid);
        name.setText(currentNumberPosition.getname());


        TextView number = currentItemView.findViewById(R.id.listviewnumberid);
        number.setText(currentNumberPosition.getnumber());



        ImageView type= currentItemView.findViewById(R.id.type);
        if (currentNumberPosition.gettype()=="o"){
            type.setImageResource(R.drawable.outcoming);
        }else {
            type.setImageResource(R.drawable.incoming);
        }

        Button callbtn = currentItemView.findViewById(R.id.callbtn);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "calling to "+number.getText(), Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+number.getText()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);



            }
        });

    /*    Button addnotes = currentItemView.findViewById(R.id.listnotes);
        addnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "info", Toast.LENGTH_SHORT).show();

                builder1 = new AlertDialog.Builder(getContext().getApplicationContext());


                builder1.setIcon(R.drawable.baseline_note_add_24);
                builder1.setTitle("Set Shedule");
                builder1.setCancelable(false);

                LayoutInflater inflater=LayoutInflater.from(getContext().getApplicationContext());
                View view=inflater.inflate(R.layout.popup,null);



                builder1.setView(view);



                builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {



                    EditText text=inflater.inflate(R.layout.popup,null).findViewById(R.id.namepupop);
                    Toast.makeText(getContext().getApplicationContext(), text.getText().toString(), Toast.LENGTH_SHORT).show();


                    dialog.dismiss();
                });
                builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

                    dialog.cancel();
                });
                builder1.setMessage("set Shedule to " );
                alert = builder1.create();
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                alert.cancel();
                alert.show();



            }
        });
*/
        Button info = currentItemView.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.getContext().startActivity(new Intent(getContext(),Notes.class).putExtra("name",name.getText()).putExtra("number",number.getText()));


            }
        });



        return currentItemView;
    }

}