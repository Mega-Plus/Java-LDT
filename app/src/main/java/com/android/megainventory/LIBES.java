package com.android.megainventory;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.Date;

public class LIBES {


    public static String GET_CALENDAR_TIME_short(){
        String   textr="";


        Date date =new Date();
//date =  jCalendar.getDate();
        int t1=date.getYear()+1900;
        int t2=date.getMonth()+1;
        int t3=date.getDate();

        String YY=Integer.toString(t1);
        String MM=""; if(t2<10){MM="0"+Integer.toString(t2);}else{MM=Integer.toString(t2);}
        String DD=""; if(t3<10){DD="0"+Integer.toString(t3);}else{DD=Integer.toString(t3);}

//int t4=date.getHours();//-1; if(t4==-1){t4=23;}
//int t5=date.getMinutes();
//int t6=date.getSeconds();
//
//       String  HOU="00";
//       String MIN="00";
//       String SEC="00";

        textr=(YY +"-"+MM +"-"+DD );

        return textr;
    }
    //----------------------------------------------------------------


    public static void MESS_Toast(Context context, String message ){  // MESS_Toast(getApplicationContext(),"123");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
//----------------------------------------------------------------



    public static void MESS(Context context, String Title, String message) {    // MESS(getApplicationContext(),"123","123");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to handle OK button click
                dialog.dismiss();
            }
        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Code to handle Cancel button click
//                dialog.dismiss();
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
//----------------------------------------------------------------



}
