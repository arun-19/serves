package com.example.seves;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.CallLog;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> arrayAdapter;
    AlertDialog.Builder builder1;
    Cursor cursor;
    ArrayList<logArray> arrayLists;
    @SuppressLint({"NewApi", "MissingPermission", "MissingInflatedId", "ResourceType", "MissingInflatedId", "ResourceType"})
    ListView listView;
    //MyReceiver receiver=new MyReceiver();
    IntentFilter intentFilter;
    // StringBuffer sb=new StringBuffer();
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         arrayLists=new ArrayList<logArray>();

        //permissions check
        
        Intent i=new Intent(MainActivity.this,servesSevices.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M | Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE ) {
            startForegroundService(i);
        }



        listView=(ListView) findViewById(R.id.listview);
        /*
        builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Do you want to exit ?");
        builder1.setTitle("Alert !");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
        });
        builder1.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

            dialog.cancel();
        });

AlertDialog alert=builder1.create();
alert.show();
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M | Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS},1000);



                    Intent holdIntent = new Intent();
                    String packagename = getApplicationContext().getPackageName();
                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(packagename)) {
                        holdIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

                        holdIntent.setData(Uri.parse("package:" + packagename));

                        startActivity(holdIntent);
                    }

            }



            if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},202);

            }
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},203);

            }



        }


        //raise permissiion again show if denied
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW) &&  ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SYSTEM_ALERT_WINDOW}, 201);

        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)==PackageManager.PERMISSION_GRANTED) {
            Calllogs();
        }





        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O | Build.VERSION.SDK_INT>=Build.VERSION_CODES.BASE){
            NotificationChannel channel=new NotificationChannel("channel","test",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationChannel pendingchanel=new NotificationChannel("pending","pending Task",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManagerpending=getSystemService(NotificationManager.class);
            notificationManagerpending.createNotificationChannel(pendingchanel);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M | Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            if (!Settings.canDrawOverlays(this)) {
                IntentFilter filterCalls = new IntentFilter();
                filterCalls.addAction(Intent.ACTION_CALL);
                filterCalls.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

                 CallReciverIdentifer myCallReceiver = new CallReciverIdentifer();
                registerReceiver(myCallReceiver, filterCalls);
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 101);
            }
        }


//call log list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                logArray data=arrayLists.get(position);
                String number=data.getnumber();
                String name=""+data.getname();


                //  Toast.makeText(MainActivity.this,,Toast.LENGTH_LONG).show();






            }
        });

    }


    public  void   Calllogs() {
        cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {
            String cnumber = cursor.getString(number);
            String cname = cursor.getString(name);
            String ctype = cursor.getString(type);
            String cdate = cursor.getString(date);
            String cduration = cursor.getString(duration);

            int typedir = Integer.parseInt(ctype);
            arrayLists.add(new logArray(cname, cnumber, CallLog.Calls.OUTGOING_TYPE == typedir ? "o" : "i", cdate, cduration));
        }
        customArrayAdaptor customarrayAdaptor = new customArrayAdaptor(this, arrayLists);
        // arrayAdapter =new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list_view,R.id.listviewnumberid,  arrayLists);

        listView.setAdapter(customarrayAdaptor);
        cursor.close();


    }

    public void funview(View view){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==201) {

            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
        }
        if (requestCode==101){
            if (resultCode== Activity.RESULT_OK){
                Toast.makeText(this, "permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        AlertDialog al;
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        buider.setTitle("Close");
        buider.setMessage("Do You Want Close?");
        buider.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });
        buider.setIcon(R.mipmap.close);

        buider.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

           dialog.dismiss();

            }
        });

        al = buider.create();

        al.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1000){
            Calllogs();
            Toast.makeText(this, "permision granted", Toast.LENGTH_SHORT).show();
        }
    }
}