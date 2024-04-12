package com.example.seves;

public class logArray {






        private String name;


        private String number;

        private  String type;

    private  String date;

    private  String duration;


    public logArray( String accname, String accnumber,String calltype,String accdate,String accduration) {

          name = accname;
            number = accnumber;
            type=calltype;
            date=accdate;
            duration=accduration;

        }





        public String getname() {
            return  name;
        }


        public String getnumber() {
            return number;
        }
    public String gettype() {
        return type;
    }

    public String getdate() {
        return date;
    }
    public String getduration() {
        return duration;
    }

}
