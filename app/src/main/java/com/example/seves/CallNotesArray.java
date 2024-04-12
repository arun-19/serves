package com.example.seves;


import android.widget.Toast;

public class CallNotesArray {






    private String notetitle;


    private String notepurpose;

    private  String notetime;




    public CallNotesArray( String title, String purpose,String time) {

        notetitle = title;
         notepurpose= purpose;
        notetime=time;



    }





    public String getNotetitle() {
        return  notetitle;
    }


    public String getNotepurpose() {
        return notepurpose;
    }
    public String getNotetime() {
        return notetime;
    }


}

