package com.example.seves;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class alertView {
    String i;
    final Calendar myCalendar= Calendar.getInstance();
    String android_id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AlertDialog.Builder builder1;
    AlertDialog alert;
    public alertView(Context context, String accname, String acctitle, String accnumber,String accpurpose,String accdate,String acctime, String accid, int icon) {

        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        builder1 = new AlertDialog.Builder(context);
        builder1.setIcon(icon);
        builder1.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.popup, null);
        SimpleDateFormat date=new SimpleDateFormat("HH:mm:aaa");

        EditText timepupop = view.findViewById(R.id.timepupop);
        timepupop.setText(acctime);

        Button yes=view.findViewById(R.id.yes);
        yes.setText(accid=="add"?"add":"Update");
        Button No=view.findViewById(R.id.no);

        EditText name = view.findViewById(R.id.namepupop);
        name.setText(accname);

        EditText title = view.findViewById(R.id.tilepopup);
        title.setText(acctitle);

        EditText purposepupop = view.findViewById(R.id.purposepupop);
        purposepupop.setText(accpurpose);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText datepopup = view.findViewById(R.id.datepuppop);
        datepopup.setText(accdate);
        DatePickerDialog.OnDateSetListener datepicker =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateEditext(datepopup);
            }
        };


        datepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,datepicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));



            }
        });

        timepupop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int cal=hourOfDay-12==0?12:hourOfDay-12;
                                if(hourOfDay>12) {

                                    timepupop.setText(cal+ ":" + minute+":"+"PM" );
                                }else {
                                    timepupop.setText(hourOfDay + ":" + minute+":"+"AM" );
                                }
                            }
                        }, hour, minute, false);
           // timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
            }
        });





        builder1.setView(view);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] time=timepupop.getText().toString().split(":");

                Map<String,Object> taskArray=new HashMap<>();
                taskArray.put("name",name.getText().toString());
                taskArray.put("number",accnumber);
                taskArray.put("title",title.getText().toString());
                taskArray.put("purpose",purposepupop.getText().toString());
                taskArray.put("hours", time[0].toString().replaceFirst("^0",""));
                taskArray.put("date",datepopup.getText().toString());
                taskArray.put("minutes",time[1].toString().replaceFirst("^0",""));
                taskArray.put("id",android_id);
                taskArray.put("timeset",time[2]);
                taskArray.put("active","y");




                Boolean titlecheck=title.getText().toString().trim().length()!=0 ?true:false;
                Boolean purposecheck=purposepupop.getText().toString().trim().length()!=0 ?true:false;
                Boolean timecheck=timepupop.getText().toString().trim().length()!=0 ?true:false;


                if ( titlecheck && purposecheck && timecheck ) {
                    try{
                        if (accid=="add"){


                            db.collection("task").add(taskArray).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {


                                    Toast.makeText(context, "Successfully set Task ", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    title.setText("");

                                }
                            });



                        }else {
                            db.collection("task").document(accid).update(taskArray).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Upadted Task", Toast.LENGTH_SHORT).show();


                                    Intent re = new Intent(context.getApplicationContext(),Notes.class);
                                    re.putExtra("name","");
                                    re.putExtra("number",accnumber);
                                    re.putExtra("idle","1");
                                    re.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                    context.startActivity(re);

                                }
                            });

                        }


                    }catch (Exception ex){
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
                alert.dismiss();
            }
        });
        /*
        builder1.setPositiveButton(accid=="add"?"add":"Update", (DialogInterface.OnClickListener) (dialog, which) -> {

        });

         */

        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert.dismiss();

            }
        });
        /*
        builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {


        });
         */
        builder1.setTitle(accid=="add"?"Add Task to "+accnumber:"Update Task "+accnumber);
        alert = builder1.create();

        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alert.show();




    }

    private void updateEditext(EditText editText){
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");

        editText.setText(dateFormat.format(myCalendar.getTime()));
    }


}
