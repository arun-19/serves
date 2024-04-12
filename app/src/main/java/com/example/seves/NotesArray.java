package com.example.seves;

public class NotesArray {







    private String title;
private  String id;

    private String name;

    private  String number;
    private  String purpose;
     private  String date;
     private  String time;

    public NotesArray(String accid, String acctitle, String accname,String accnumber,String accpurpose,String accdate,String acctime) {
          id=accid;
        name = accname;
        number = accnumber;
        title=acctitle;
        purpose=accpurpose;
date=accdate;
time=acctime;
    }





    public String gettitle() {
        return  title;
    }


    public String getname() {
        return name;
    }
    public String getnumber() {
        return number;
    }
    public String getid() {
        return id;
    }
    public String getPurpose() {
        return purpose;
    }

    public String getdate() {
        return date;
    }
    public String gettime() {
        return time;
    }

}
