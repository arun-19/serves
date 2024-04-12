package com.example.seves;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver mycontextsevice;

    int count = 0;
    public Context c;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String event;
    AlertDialog.Builder builder1;
    public String Contactname;

    TelephonyManager tmgr;
    public  static String innumber="";

    public boolean checkcallidle = false;
    public boolean checkcallringig = false;



    String android_id;
    AlertDialog alert;
    // MyPhoneStateListener PhoneListener;
    SimpleDateFormat date = new SimpleDateFormat("HH:mm");
    String outgoing_Number="";
    private static final String TAG = "PhoneStatReceiver";


    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static boolean isOutcoming;
    private static String savedNumber;

    private  static boolean initfirst=false;
    public boolean isIsIncominglive=false;
    private static String incoming_number = null;
    public Intent recevier_intent;
    String[] timedata = date.format(new Date()).split(":");
    SimpleDateFormat dateofyear = new SimpleDateFormat("dd-MM-yyyy");
    String currentdate = dateofyear.format(new Date());

    public  String incomefirst="";
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {




        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                innumber=incomingNumber.isEmpty()==false?incomingNumber:innumber;
                if(incomingNumber!="" && incomingNumber!=null && !incomingNumber.isEmpty()) {
                    innumber = incomingNumber;
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

        mycontextsevice = MyReceiver.this;


        if (intent.getAction() == Intent.ACTION_NEW_OUTGOING_CALL) {
            savedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }


        event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        //   c = context;
        //   tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //   PhoneListener = new MyPhoneStateListener();
        //   tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);


    }

    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up

    public void onCallStateChanged(Context context, int state, String number) {
if(number!=null  && innumber==null  || innumber=="" ){
    savedNumber=number;
    innumber=number;
    if(initfirst!=true) {
        forcealert(context, number);
    }
}
        if (lastState == state) {

            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                //after incoming ringing
                if (lastState != TelephonyManager.CALL_STATE_OFFHOOK && state!=TelephonyManager.CALL_STATE_IDLE ) {

                    if ( innumber != "" && innumber != null && isIncoming ) {
                        try {
                            Thread.sleep(200);
                            alertshow(context, innumber);
                            isIncoming=false;

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        TelephonyManager telephony1 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        telephony1.listen(new PhoneStateListener() {
                            @Override
                            public void onCallStateChanged(int state, String incomingNumber) {
                                super.onCallStateChanged(state, incomingNumber);
                                if (lastState == TelephonyManager.CALL_STATE_RINGING  ) {
                                    try {
                                        if (!incomingNumber.isEmpty() && incomingNumber != "" && incomingNumber != null && isIncoming) {
                                            Thread.sleep(200);
                                            innumber = incomingNumber;
                                            alertshow(context, incomingNumber);
                                            isIncoming = false;

                                        }
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            }
                        }, PhoneStateListener.LISTEN_CALL_STATE);


                        // Toast.makeText(context, "Number can't capture", Toast.LENGTH_SHORT).show();

                    }

                }

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:


                isIncoming=false;
                isOutcoming=false;

                if (lastState != TelephonyManager.CALL_STATE_RINGING ) {

                   isOutcoming=true;

                    if ( innumber!="" && innumber!=null  && isOutcoming  ) {
                        try {
                            Thread.sleep(200);

                            alertshow(context, innumber);
                             isOutcoming=false;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }else {

                        if ( innumber == "" && innumber == null) {

                            TelephonyManager telephony1 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                            telephony1.listen(new PhoneStateListener() {
                                @Override
                                public void onCallStateChanged(int state, String incomingNumber) {
                                    super.onCallStateChanged(state, incomingNumber);
                                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                                        try {

                                            Thread.sleep(200);
                                            innumber = incomingNumber;
                                            alertshow(context, incomingNumber);
                                            isOutcoming = false;
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }else {
                                        innumber = incomingNumber;
                                      //  Toast.makeText(context, ""+innumber, Toast.LENGTH_SHORT).show();
                                        alertshow(context, incomingNumber);
                                        isOutcoming = false;

                                    }

                                }
                            }, PhoneStateListener.LISTEN_CALL_STATE);
                        }else {
                            try {
                             //   Toast.makeText(context, "initial calling wait...", Toast.LENGTH_SHORT).show();
                                isOutcoming = false;
                            }catch (Exception e){
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                        // Toast.makeText(context, "Number can't capture", Toast.LENGTH_SHORT).show();


                    isIncoming = false;
                    callStartTime = new Date();
                    //  Toast.makeText(context, "Outgoing Call Started", Toast.LENGTH_SHORT).show();
                }


                break;
            case TelephonyManager.CALL_STATE_IDLE:
             innumber="";
                isIncoming=false;


                if (lastState == TelephonyManager.CALL_STATE_RINGING) {

// Toast.makeText(context, "Ringing but no pickup 3" + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
                } else if (isIncoming) {

                    try {

                        //after incoming call accept and when finish call show msgage

                       // alertshow(context,innumber);

                      //  new alertView(context,"","",innumber,"","add",R.drawable.baseline_add_task_24);

                            /*
                            db.collection("task").whereEqualTo("id", android_id).whereEqualTo("date", currentdate).whereEqualTo("number", innumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int count = 0;

                                    if (task.getResult().getDocuments().isEmpty()) {
                                        //  Toast.makeText(context, "empty record", Toast.LENGTH_SHORT).show();

                                    } else {
                                        for (QueryDocumentSnapshot qr : task.getResult()) {
                                            count = +1;
                                            String hours = qr.getString("hours");
                                            String minutes = qr.getString("minutes");

                                            if (Integer.parseInt(hours) >= Integer.parseInt(timedata[0])) {
                                                if (Integer.parseInt(minutes) > Integer.parseInt(timedata[1])) {
                                                    count = +1;

                                                    new TaskPendingView().getTask(c, count,innumber);


                                                } else {
                                                    if (Integer.parseInt(hours) == Integer.parseInt(timedata[0])) {

                                                        //Toast.makeText(context, "again emty", Toast.LENGTH_SHORT).show();
                                                        alertshow();
                                                    }


                                                }
                                            } else {
                                                Toast.makeText(c.getApplicationContext(), "task no", Toast.LENGTH_SHORT).show();

                                            }


                                        }


                                    }


                                }
                            });
*/

                    } catch (Exception ex) {

                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(context, "8789Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
                } else {

                    //outgoing number cut
                    //alertshow(context,innumber);

                    //Toast.makeText(context.getApplicationContext(), "outgoing " + savedNumber + " Call time " + callStartTime + " Date " + new Date(), Toast.LENGTH_SHORT).show();
                }
                isOutcoming=true;
                isIncoming=true;

                break;

            default:

                  Toast.makeText(context, "Busy", Toast.LENGTH_SHORT).show();
              //  alertshow(context,innumber);
                break;
        }

        lastState = state;
    }

    @SuppressLint("MissingPermission")
    public void alertshow(Context c,String number) {


        try{
            builder1 = new AlertDialog.Builder(c);
            builder1.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(c);
            View  view = inflater.inflate(R.layout.popupwithnumber, null);
            ImageView close=view.findViewById(R.id.btnCancel);
            TextView name=view.findViewById(R.id.number);
            TextView title=view.findViewById(R.id.title);
            TextView purpose=view.findViewById(R.id.purpose);
            ImageView whatsapp=view.findViewById(R.id.whatsapp);
            ImageView sms=view.findViewById(R.id.sms);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView home=view.findViewById(R.id.home);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView firstleter=view.findViewById(R.id.firstletter);
          //  Toast.makeText(c, ""+number, Toast.LENGTH_SHORT).show();
            whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//Uri.parse("https://api.whatsapp.com/send?phone="+innumber+"&text="+"")
                        Intent whatsapp=new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone="+innumber+"&text="+""));
                        whatsapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        c.startActivity(whatsapp);
                        alert.dismiss();


                    }catch (Exception ex){
                        Log.e("",ex.getMessage());
                        Toast.makeText(c, "please install whatsapp...", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        c.startActivity(i);
                        alert.dismiss();

                    }

                }
            });
            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP;
                    intent.setFlags(flags);
                    intent.setData(Uri.parse("sms:"+innumber));
                    c.startActivity(intent);
                    alert.dismiss();
                }
            });

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   try{
                       Intent gotohome=new Intent(c,MainActivity.class);
                       gotohome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                       PendingIntent pintent=PendingIntent.getActivity(c.getApplicationContext(),109,gotohome,PendingIntent.FLAG_IMMUTABLE);
                       pintent.send();
                       alert.dismiss();
                   }catch (Exception ex){

                   }

                }
            });
            if(innumber!=null  && innumber!="") {
                String customername=getContactName(c, innumber);
                db.collection("task").whereEqualTo("id", Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID)).whereEqualTo("date", currentdate).whereEqualTo("active", "y").whereEqualTo("number", innumber).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            name.setText("" + customername);
                            firstleter.setText(customername!=null?customername.substring(0,1):"?");
                            title.setText("No Task Available ");
                        } else {
                            for (QueryDocumentSnapshot qr : task.getResult()) {
                                name.setText("name: " + qr.getString("name"));
                                title.setText("title: " + qr.getString("title"));
                                purpose.setText("purpose: " + qr.getString("purpose"));
                                firstleter.setText(customername!=null?customername.substring(0,1):"?");
                            }
                        }
                    }
                });

            }else{
                name.setText("unknown call");
                firstleter.setText("?");
                title.setText("No Task Available ");
            }
            builder1.setView(view);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    alert.dismiss();
                }
            });
            alert = builder1.create();


            Window win =alert. getWindow();

            win.setBackgroundDrawableResource(R.drawable.custom);

            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            win.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            win.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            alert.show();



        }catch (Exception ex){

        }



        try {

            //  Toast.makeText(c, innumber+"-"+savedNumber+"-"+outgoing_Number+"-"+incoming_number+"="+number, Toast.LENGTH_SHORT).show();



            Intent i=new Intent(c,  Notes.class);
           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        //    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            i.putExtra("name","");
            i.putExtra("number",innumber);
            i.putExtra("idle","1");
            PendingIntent pendingIntent= PendingIntent.getActivity(c.getApplicationContext(), 9,i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE );
            pendingIntent.send();





            //  c.startActivity(i);
    /*        builder1 = new AlertDialog.Builder(c);


            builder1.setIcon(R.drawable.baseline_note_add_24);
            builder1.setTitle("Set Shedule");
            builder1.setCancelable(false);

            LayoutInflater inflater = LayoutInflater.from(c);

            View  view = inflater.inflate(R.layout.popup, null);


            SimpleDateFormat date = new SimpleDateFormat("HH:mm");
            EditText timepupop = view.findViewById(R.id.timepupop);


            timepupop.setText(date.format(new Date()));

            builder1.setView(view);


            builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {


                EditText name = view.findViewById(R.id.namepupop);
                EditText title = view.findViewById(R.id.tilepopup);
                EditText purposepupop = view.findViewById(R.id.purposepupop);
                //   EditText editnumber = view.findViewById(R.id.numberpupop);
                String[] time = timepupop.getText().toString().split(":");

                Map<String, Object> taskArray = new HashMap<>();


                taskArray.put("number",innumber!=null?innumber:tmgr.getLine1Number().toString());
                taskArray.put("name","dfgdfg");
                taskArray.put("title",title.getText().toString());
                taskArray.put("purpose",purposepupop.getText().toString());
                taskArray.put("hours",time[0]);
                taskArray.put("date",currentdate);
                taskArray.put("minutes",time[1]);
                taskArray.put("id",android_id);




                Boolean titlecheck=title.getText().toString().trim().length()!=0 ?true:false;
                Boolean purposecheck=purposepupop.getText().toString().trim().length()!=0 ?true:false;
                Boolean timecheck=timepupop.getText().toString().trim().length()!=0 ?true:false;


                if ( titlecheck && purposecheck && timecheck ) {
                    try{
                        db.collection("task").add(taskArray).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {


                                Toast.makeText(c.getApplicationContext(), "Successfully set Task ", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(c.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {


                            }
                        });

                    }catch (Exception ex){
                        //Toast.makeText(c, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
                dialog.dismiss();

            });
            builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {


                dialog.dismiss();

            });
            builder1.setMessage("set Task");
            alert = builder1.create();

            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            alert.show();
*/








        }catch (Exception ex){
            Log.i(TAG,ex.getMessage());
            Toast.makeText(c, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



 /*  private class MyPhoneStateListener extends PhoneStateListener {



        public void onCallStateChanged(int state, String incomingNumber) {


            switch (state) {

                case TelephonyManager.CALL_STATE_OFFHOOK:

try {

    if (checkcallringig == false && incomingNumber != null) {
        innumber = incomingNumber;

    }

}catch (Exception ex){

}
                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                   checkcallidle = true;
                    Intent launch_intent = new Intent("android.intent.action.MAIN");
                    launch_intent.setComponent(new ComponentName("com.example.seves", "com.example.seves.MainActivity"));
                    launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    launch_intent.putExtra("some_data", "value");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent service = new Intent(c, MyReceiver.class);
                        c.startService(service);
                        c.startForegroundService(launch_intent);
                    }


                    if (checkcallringig == false && incomingNumber != null) {
                        innumber = incomingNumber;


                    }

                    break;



                    //   alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    //  alert.show();
                    //Toast.makeText(c, "hook", Toast.LENGTH_SHORT).show();
                default:
                    //  Toast.makeText(c, "TEST", Toast.LENGTH_SHORT).show();
                    break;


                // Handle other states if needed
            }


            if (event.equals(TelephonyManager.EXTRA_STATE_RINGING) ) {

                innumber=incomingNumber;

                checkcallidle = true;
                Intent launch_intent = new Intent("android.intent.action.MAIN");
                launch_intent.setComponent(new ComponentName("com.example.seves", "com.example.seves.MainActivity"));
                launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
                launch_intent.putExtra("some_data", "value");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent service = new Intent(c, MyReceiver.class);
                    c.startService(service);
                    c.startForegroundService(launch_intent);
                }





            }




            if (state == TelephonyManager.CALL_STATE_IDLE ) {


                Intent launch_intent = new Intent("android.intent.action.MAIN");
                launch_intent.setComponent(new ComponentName("com.example.seves", "com.example.seves.MainActivity"));
                launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
                launch_intent.putExtra("some_data", "value");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent service = new Intent(c, MyReceiver.class);
                    c.startService(service);
                    c.startForegroundService(launch_intent);
                }

                checkcallidle = true;



            }





        }

        public void getnumber() {

        }

   /*     public void getTask() {


            builder1 = new AlertDialog.Builder(c);


            builder1.setIcon(R.drawable.baseline_note_add_24);
            builder1.setTitle("Set Shedule");
            builder1.setCancelable(false);

            LayoutInflater inflater = LayoutInflater.from(c);
            View view = inflater.inflate(R.layout.popup, null);


            builder1.setView(inflater.inflate(R.layout.popup, null));
            //  View view=inflater.inflate(R.layout.popup,null);


            builder1.setMessage("Task Available");


            builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {


                c.startActivity(new Intent(c, Notes.class));


                dialog.dismiss();
            });
            builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

                dialog.cancel();
            });

            alert = builder1.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            alert.cancel();
            alert.show();
            checkcallringig = false;


        }

*/
   /*     public void alertView(Context c, String income) {

            SimpleDateFormat dateofyear=new SimpleDateFormat("dd-MM-yyyy");
            String currentdate=dateofyear.format(new Date());
            //     c.startActivity(new Intent(c, Notes.class).putExtra("name", "dfdf").putExtra("number", income));
            if ( count==0 && checkdialog == true && income != null ) {
                int hour=Integer.parseInt(timedata[0]);
                int minute=Integer.parseInt(timedata[1]);
         db.collection("task").whereEqualTo("id",android_id).whereEqualTo("date",currentdate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {

                 if (task.getResult().isEmpty()){
                     Toast.makeText(c, "no task assign", Toast.LENGTH_SHORT).show();
                     alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                     alert.show();


                 }else {




                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });


                //nothing hapend


            }

        }



    }

 */

    public void forcealert(Context context,String onetimenumber){
        boolean onetime=true;
        if(onetime && onetimenumber!=null){

            alertshow(context,""+onetimenumber);
            initfirst=true;
            onetime=false;
        }
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
             Contactname = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
         }

         if (cursor != null && !cursor.isClosed()) {
             cursor.close();
         }


     } catch (Exception ex) {
         Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();

     }
     return Contactname;
 }





}

