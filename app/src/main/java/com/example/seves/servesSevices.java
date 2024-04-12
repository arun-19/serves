package com.example.seves;



import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class servesSevices extends Service {
    CallReciverIdentifer callRecorderBroadcast;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String   android_id ;
    SimpleDateFormat dateofyear = new SimpleDateFormat("dd-MM-yyyy");
    String currentdate = dateofyear.format(new Date());
    private  static  Calendar calendar=Calendar.getInstance();


    @Override
    public void onCreate() {
        super.onCreate();
        try{
         callRecorderBroadcast=new CallReciverIdentifer();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CALL);
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction(Intent.EXTRA_PHONE_NUMBER);
        intentFilter.addAction(Intent.ACTION_CALL_BUTTON);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(callRecorderBroadcast,intentFilter,RECEIVER_EXPORTED);
            }

        }catch (Exception ex){
            Toast.makeText(this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @SuppressLint("ResourceAsColor") NotificationCompat.Builder notification=new NotificationCompat.Builder(this,"channel").setSmallIcon(R.drawable.baseline_ondemand_video_24).setContentTitle("TCall").setContentText("Running....").setColor(com.google.android.material.R.color.material_dynamic_secondary50).setSilent(true).setAutoCancel(false);
        @SuppressLint("ResourceAsColor") NotificationCompat.Builder pendingnotification=new NotificationCompat.Builder(this,"pending").setSmallIcon(R.drawable.baseline_notifications_active_24);
      new Thread(new Runnable() {
            @Override
            public void run() {

while (true) {
    startForeground(5000,notification.build());
    try {
        Log.e("log","running");


               Thread.sleep(20000);
        SimpleDateFormat date = new SimpleDateFormat("hh:mm:aaa");
        String[] timedata=date.format(new Date()).toString().split(":");
        int currentSystemhour=Integer.parseInt(timedata[0].replaceFirst("^0",""));
        int curruntSystemminutes=Integer.parseInt(timedata[1].replaceFirst("^0",""))==0?60:Integer.parseInt(timedata[1].replaceFirst("^0",""));


        db.collection("task").whereEqualTo("id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)).whereEqualTo("date", currentdate).whereEqualTo("active","y").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for(QueryDocumentSnapshot qr:task.getResult()){

                    String hours=qr.getString("hours").replaceFirst("^0","");
                    String minutes= qr.getString("minutes").replaceFirst("^0","");
                     String name= qr.getString("name");
                     String number= qr.getString("number");
                     String purpose= qr.getString("purpose");
                    String n30= qr.getString("n30");
                    String timset= qr.getString("timeset");
                     String doc_id= qr.getId();


try {
    calendar.setTime(date.parse(hours+":"+minutes+":"+timset));
    calendar.add(Calendar.MINUTE,-30);
    String time[]=date.format(calendar.getTime()).split(":");

 /*
    Calendar calendar=Calendar.getInstance();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm");
    calendar.setTime(simpleDateFormat.parse(hours+":"+minutes));
    calendar.add(Calendar.HOUR,Integer.parseInt(hours));
    calendar.add(Calendar.MINUTE,Integer.parseInt(minutes));
     Toast.makeText(servesSevices.this, ""+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
 */

    if(time[0].equals(timedata[0]) && n30=="n" && time[1].equals(timedata[1]) && time[2].equals(timedata[2]) ) {
        Intent resultIntent = new Intent(getApplicationContext(), Notes.class).putExtra("name", name).putExtra("number", number).putExtra("idle", "1");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        pendingnotification.setContentTitle(name + "-" + number).setContentText(purpose).setContentIntent(notifyPendingIntent);
        notificationManager.notify(3005, pendingnotification.build());
        Map<String, Object> taskArray = new HashMap<>();
        taskArray.put("n30", "y");
        db.collection("task").document(doc_id).update(taskArray);
    }


    if(Integer.parseInt(hours)==currentSystemhour  ){
        //  Toast.makeText(servesSevices.this, ""+currentSystemhour, Toast.LENGTH_SHORT).show();
        int balance=curruntSystemminutes<Integer.parseInt(minutes)?Integer.parseInt(minutes)-curruntSystemminutes:curruntSystemminutes-Integer.parseInt(minutes);


                   /*     if( balance==10 &&  n10=="n") {
                            Intent resultIntent = new Intent(getApplicationContext(), Notes.class).putExtra("name",name).putExtra("number",number).putExtra("idle","1");

                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                                    getApplicationContext(), 0, resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                            );

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                            pendingnotification.setContentTitle(name+"-"+number).setContentText(purpose).setContentIntent(notifyPendingIntent);

                            notificationManager.notify(3000, pendingnotification.build());
                            Map<String,Object> taskArray=new HashMap<>();
                            taskArray.put("n10","y");
                            db.collection("task").document(doc_id).update(taskArray);
                            //finish notification
                            //Map<String,Object> taskArray=new HashMap<>();
                            //taskArray.put("active","n");
                            //  db.collection("task").document(doc_id).update(taskArray);
                            //  new TaskPendingView().getTask(getApplicationContext(), count, "number now not define");
                        }

*/

        if(balance==0 ){
            Intent resultIntent = new Intent(getApplicationContext(), Notes.class).putExtra("name", name).putExtra("number", number).putExtra("idle", "1");

            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            pendingnotification.setContentTitle(name + "-" + number).setContentText(purpose).setContentIntent(notifyPendingIntent).setStyle(new NotificationCompat.BigTextStyle().bigText(name));

            notificationManager.notify(3005, pendingnotification.build());
            Map<String, Object> taskArray = new HashMap<>();
            taskArray.put("active", "n");
            db.collection("task").document(doc_id).update(taskArray);




        }


        // Toast.makeText(servesSevices.this, String.valueOf(findbalancetenminutes), Toast.LENGTH_SHORT).show();



        //   Toast.makeText(servesSevices.this,String.valueOf( findbalancetenminutes), Toast.LENGTH_SHORT).show();

        //  new TaskPendingView().getTask(getApplicationContext(), count,focusnumber);

    }


}catch (Exception ex){

}

/*
if(currentSystemhour<Integer.parseInt(hours)){
    int equal=currentSystemhour+1;
  if(30>curruntSystemminutes && curruntSystemminutes<40 && equal==Integer.parseInt(hours)  ){

  }

}
*/

                }

            }
        });


    } catch (Exception ex) {
        ex.printStackTrace();
    }

}

            }
        }).start();




        return Service.START_STICKY;
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
        unregisterReceiver(callRecorderBroadcast);


    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restaertsevices=new Intent(getApplicationContext(),this.getClass());
        restaertsevices.setPackage(getPackageName());
        PendingIntent restartservicespending=PendingIntent.getService(getApplicationContext(),1,restaertsevices, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE );
        AlarmManager alarmManager=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+1000,restartservicespending);
        super.onTaskRemoved(rootIntent);
    }
}
