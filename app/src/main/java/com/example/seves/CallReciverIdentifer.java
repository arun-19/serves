package com.example.seves;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CallReciverIdentifer extends BroadcastReceiver  {


    private static final String TAG = "CallRecoderBroadcast";
    public String Contactname;
    public static ArrayList<CallNotesArray> arrayLists;

    private static final String ACTION_PHONE = "android.intent.action.PHONE_STATE";
    private static final String OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
    AlertDialog dialog;
   private static Boolean checkAlershow=Boolean.FALSE;
    AlertDialog.Builder al;
    private static Boolean mCallOnGoing = Boolean.FALSE;
    private MediaRecorder mRecorder = new MediaRecorder();
    AudioManager audioManager;

    private boolean CallState;
    private float sensorState;
    //    private File mFile = null;

    private String mNumber;
    private Sensor myLightSensor;
    private SensorManager mSensorManager;
    public Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    android.app.AlertDialog.Builder builder1;
    String android_id;
    android.app.AlertDialog alert;
    public Boolean Incomingcall=false;
    // MyPhoneStateListener PhoneListener;
    SimpleDateFormat datetime = new SimpleDateFormat("hh:mm:aaa");

    String[] time=datetime.format(new Date()).toString().split(":");

    SimpleDateFormat dateofyear = new SimpleDateFormat("dd-MM-yyyy");
    String currentdate = dateofyear.format(new Date());
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        try {
            mNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (intent.getAction().equals(ACTION_PHONE) && mNumber!=null) {
                Bundle bundle = intent.getExtras();
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && mNumber!=null) {
                    Incomingcall=TRUE;
            //call custom screen
                    Log.e(TAG, bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                    mNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                  if (checkAlershow==false) {
                      alertshow(context, mNumber);
                     // checkAlershow=Boolean.TRUE;
                  }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (!mCallOnGoing) {
                      mCallOnGoing= TRUE;
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE) && mNumber!=null && Incomingcall==FALSE ) {
                    if ( mCallOnGoing) {
                       // stopRecording();
                            alertshow(context, mNumber);
                         Incomingcall=Boolean.FALSE;
                         mCallOnGoing=Boolean.FALSE;
                         checkAlershow=FALSE;
                    }

                }

            } else if(intent.getAction().equals(OUTGOING_CALL)) {


                Log.e(TAG, "OUTGOING cALL");
                Toast.makeText(context, "outgoing", Toast.LENGTH_SHORT).show();
                Log.e(TAG, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                mNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                alertshow(context,mNumber);


            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    @SuppressLint("ResourceType")
    public void alertshow(Context c, String number) {
     if(checkAlershow==FALSE) {
    try {
        arrayLists=new ArrayList<CallNotesArray>();

        builder1 = new android.app.AlertDialog.Builder(c);
        builder1.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.popupwithnumber, null);
        ImageView close = view.findViewById(R.id.btnCancel);
        TextView name = view.findViewById(R.id.number);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView empty = view.findViewById(R.id.empty);
        ListView listView=view.findViewById(R.id.calllist);
        //TextView title = view.findViewById(R.id.title);
        //TextView purpose = view.findViewById(R.id.purpose);
       // @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView timepopup = view.findViewById(R.id.timetxt);
        ImageView whatsapp = view.findViewById(R.id.whatsapp);
        ImageView sms = view.findViewById(R.id.sms);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView home = view.findViewById(R.id.home);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView firstleter = view.findViewById(R.id.firstletter);
        //  Toast.makeText(c, ""+number, Toast.LENGTH_SHORT).show();
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//Uri.parse("https://api.whatsapp.com/send?phone="+innumber+"&text="+"")
                    Intent whatsapp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mNumber + "&text=" + ""));
                    whatsapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    c.startActivity(whatsapp);
                    alert.dismiss();
                    mCallOnGoing = TRUE;

                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                    Toast.makeText(c, "please install whatsapp...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    c.startActivity(i);
                    alert.dismiss();
                    checkAlershow = Boolean.FALSE;

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
                intent.setData(Uri.parse("sms:" + number));
                c.startActivity(intent);
                alert.dismiss();
                checkAlershow = Boolean.FALSE;
                mCallOnGoing = TRUE;
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent gotohome = new Intent(c, MainActivity.class);
                    gotohome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    PendingIntent pintent = PendingIntent.getActivity(c.getApplicationContext(), 109, gotohome, PendingIntent.FLAG_IMMUTABLE);
                    pintent.send();
                    alert.dismiss();
                    checkAlershow = Boolean.FALSE;
                    mCallOnGoing = TRUE;
                } catch (Exception ex) {

                }

            }
        });
        if (mNumber != null && mNumber != "") {
            String customername = getContactName(c, number);
            db.collection("task").whereEqualTo("id", Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID)).whereEqualTo("date", currentdate).whereEqualTo("active", "y").whereEqualTo("number", number).whereEqualTo("hours",time[0].replaceFirst("^0","").toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) {
                        name.setText("" + customername);
                        firstleter.setText(customername != null ? customername.substring(0, 1) : "?");
                      empty.setVisibility(View.VISIBLE);
                       // title.setText("No Task Available ");
                    } else {
                        empty.setVisibility(View.GONE);
                        CallNotesListAdaptor callnotelist = null;
                        for (QueryDocumentSnapshot qr : task.getResult()) {
                            String hour=qr.getString("hours");
                            String minutes=qr.getString("minutes");
                            String timeset=qr.getString("timeset").toLowerCase();
                            if(hour.equals(time[0].replaceFirst("^0","").toString()) && time[2].toString().toLowerCase().equals(timeset) && Integer.parseInt(time[1].replaceFirst("^0",""))<=Integer.parseInt(minutes)) {
                                name.setText(" " + qr.getString("name"));
                                arrayLists.add(new CallNotesArray("title: "+qr.getString("title"),"purpose: "+qr.getString("purpose"),hour+":"+minutes+" "+timeset));
                                callnotelist=new CallNotesListAdaptor(context.getApplicationContext(), arrayLists);

                                //title.setText("title: " + qr.getString("title"));
                               // purpose.setText("purpose: " + qr.getString("purpose"));
                               // timepopup.setText(hour+":"+minutes+" "+timeset);
                                firstleter.setText(customername != null ? customername.substring(0, 1) : "?");
                            }else {

                                name.setText(" " + qr.getString("name"));
                                empty.setVisibility(View.VISIBLE);
                                //title.setText("No Task Available " );
                                firstleter.setText(customername != null ? customername.substring(0, 1) : "?");

                            }
                            }
                        listView.setAdapter(callnotelist);

                    }
                }
            });

        } else {
            name.setText("unknown call");
            firstleter.setText("?");
            empty.setVisibility(View.VISIBLE);
            //title.setText("No Task Available ");
        }
        builder1.setView(view);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallOnGoing = TRUE;
                alert.dismiss();
                checkAlershow = Boolean.FALSE;
            }
        });
        alert = builder1.create();


        Window win = alert.getWindow();

        win.setBackgroundDrawableResource(R.drawable.custom);

        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        win.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        win.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        alert.show();
        checkAlershow = TRUE;


    } catch (Exception ex) {

    }


    try {

        //  Toast.makeText(c, innumber+"-"+savedNumber+"-"+outgoing_Number+"-"+incoming_number+"="+number, Toast.LENGTH_SHORT).show();


        Intent i = new Intent(c, Notes.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        i.putExtra("name", "");
        i.putExtra("number", number);
        i.putExtra("idle", "1");
        PendingIntent pendingIntent = PendingIntent.getActivity(c.getApplicationContext(), 9, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        pendingIntent.send();

    } catch (Exception ex) {
        Log.i(TAG, ex.getMessage());
        Toast.makeText(c, ex.getMessage(), Toast.LENGTH_LONG).show();
    }

}else {
    alert.dismiss();
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
