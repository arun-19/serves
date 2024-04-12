package com.example.seves;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;

public class Notes extends AppCompatActivity {

    TextView textViewname,textViewnumber;
    final Calendar myCalendar= Calendar.getInstance();

    EditText editTime,title,purpose,notename,editTextDate;
Button startTask;
GridView gridViewlist;
String android_id;
    View progreview;
    ArrayList<NotesArray> arrayLists;
    FirebaseFirestore db;

    String idle;
   public  String contactName = null;
    customNotesArrayAdaptor notesArrayAdaptor;
    LinearLayout linearLayoutGridroot;
    String name,number;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
       arrayLists=new ArrayList<NotesArray>();


        purpose=findViewById(R.id.purpose);
        editTime=findViewById(R.id.editTextTime);
        gridViewlist=findViewById(R.id.tasklist);
        title=findViewById(R.id.title);
       notename=findViewById(R.id.name);
        editTextDate=findViewById(R.id.editTextDate);
        startTask=(Button) findViewById(R.id.starttask);


         android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        editTime=findViewById(R.id.editTextTime);

        name=getIntent().getExtras().getString("name");
        number=getIntent().getExtras().getString("number");

        if (name.isEmpty()){

          notename.setText(getContactName(this,number));

        }else {
            notename.setText(name);
        }





       db=FirebaseFirestore.getInstance();
        SimpleDateFormat date=new SimpleDateFormat("hh:mm:aaa");
        SimpleDateFormat dateofyear=new SimpleDateFormat("dd-MM-yyyy");
                editTextDate.setText( dateofyear.format(new Date()));

        DatePickerDialog.OnDateSetListener datepicker =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateEditext();
            }
        };
        editTime.setText(date.format(new Date()).replaceFirst("^0",""));
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Notes.this,datepicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(Notes.this,
                        new TimePickerDialog.OnTimeSetListener() {
                              @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                  if(hourOfDay>12) {
                                      int cal=hourOfDay-12==0?12:hourOfDay-12;
                                      editTime.setText(cal + ":" + minute+":"+"PM" );
                                  }else {
                                      editTime.setText(hourOfDay + ":" + minute+":"+"AM" );
                                  }
                            }
                        }, hour, minute, false);

                timePickerDialog.show();


            }
        });




        String focusnumber=number.toString();


    /*    db.collection("task").whereEqualTo("id", android_id).whereEqualTo("date", currentdate).whereEqualTo("number",number).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int count=0;
              for(QueryDocumentSnapshot qr:task.getResult()){
               count=+1;
           String hours= qr.getString("hours").replaceFirst("^0","");
                  String minutes= qr.getString("minutes");
                  Toast.makeText(Notes.this, hours, Toast.LENGTH_SHORT).show();
           if(Integer.parseInt(hours)>=Integer.parseInt(timedata[0])){
               if(  Integer.parseInt(minutes)>Integer.parseInt(timedata[1])) {
                 //  new TaskPendingView().getTask(getApplicationContext(), count,focusnumber);
               }else {
                 //  new TaskPendingView().getTask(getApplicationContext(), count,focusnumber);
               }
                  }else {
               Toast.makeText(Notes.this, "task no", Toast.LENGTH_SHORT).show();

           }


              }

            }
        });

*/
        final String[] alert30 = new String[2];

        startTask.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String[] timedata=editTime.getText().toString().split(":");


        Map<String,Object> taskArray=new HashMap<>();


        taskArray.put("name",notename.getText().toString());
        taskArray.put("number",focusnumber);
        taskArray.put("title",title.getText().toString());
        taskArray.put("purpose",purpose.getText().toString());
     taskArray.put("hours", timedata[0].toString().replaceFirst("^0",""));
     taskArray.put("date",editTextDate.getText().toString());
        taskArray.put("minutes",timedata[1].toString().replaceFirst("^0",""));
        taskArray.put("id",android_id);
        taskArray.put("timeset",timedata[2]);
        taskArray.put("active","y");
        taskArray.put("n10","n");
        taskArray.put("n30","n");




        Boolean titlecheck=title.getText().toString().trim().length()!=0 ?true:false;
        Boolean purposecheck=purpose.getText().toString().trim().length()!=0 ?true:false;
        Boolean timecheck=editTime.getText().toString().trim().length()!=0 ?true:false;



        if ( titlecheck&& purposecheck && timecheck) {
try {
    startTask.setText("Registing...");
    startTask.setEnabled(false);

    db.collection("task").add(taskArray).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
        @Override
        public void onSuccess(DocumentReference documentReference) {

            getlistgrid();
            Toast.makeText(Notes.this, "Successfully set Task ", Toast.LENGTH_SHORT).show();
            arrayLists.clear();
            startTask.setEnabled(true);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(Notes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            startTask.setText("setTask");
            startTask.setEnabled(true);
        }
    }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {
            title.setText("");
            purpose.setText("");

           editTime.setText("");
           editTextDate.setText("");
            startTask.setText("setTask");
        }
    });

}catch (Exception ex){
    Toast.makeText(Notes.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
}


        }else {
            if (title.getText().toString().isEmpty()) {
                title.requestFocus();

                Toast.makeText(Notes.this, "Must Be Fill The Title ", Toast.LENGTH_SHORT).show();


            } else {
                purpose.requestFocus();

                Toast.makeText(Notes.this, "Must Be Fill The  Purpose ", Toast.LENGTH_SHORT).show();

            }
        }




    }
});

try {



    getlistgrid();
}catch (Exception ex){
    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
}






    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public  void getlistgrid(){


        db.collection("task").whereEqualTo("id",android_id).whereEqualTo("number",number.toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
          Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
                }else {
                    for (QueryDocumentSnapshot q : task.getResult()) {

                        String title = q.getString("title");
                        String name = q.getString("name");
                        String number = q.getString("number");
                        String purpose = q.getString("purpose");

                        String date = q.getString("date");
                        String hour=q.getString("hours");
                        String minutes=q.getString("minutes");
                        String timeset=q.getString("timeset");

                        arrayLists.add(new NotesArray(q.getId(),title == "" ? "not define" : title, name == "" ? "not define" : name, number == "" ? "not define" : number,purpose == "" ? "not define" : purpose,date,hour+":"+minutes+":"+timeset));

                    }

                    notesArrayAdaptor = new customNotesArrayAdaptor(getApplicationContext(), arrayLists);
                    gridViewlist.setAdapter(notesArrayAdaptor);

                    gridViewlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          //  Toast.makeText(Notes.this, "fdfklg", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });


    }

    private void updateEditext(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");

        editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }


    @SuppressLint("Range")
    public String getContactName(Context context, String phoneNumber) {
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor == null) {
                return null;
            }

            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return contactName;
    }



}

