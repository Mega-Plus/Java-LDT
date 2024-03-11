package com.android.megainventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;

public class ReceiveActivity extends AppCompatActivity {

    Button go_back_button;
    EditText WB_N;
    EditText order_number;
    Button start_button;
    ImageButton receipt_button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        go_back_button = findViewById(R.id.go_back_button);
        WB_N = findViewById(R.id.overhead);
        order_number = findViewById(R.id.order_number);
        start_button = findViewById(R.id.start_button);

        receipt_button = findViewById(R.id.receipt_button);

//        SQL.CONNECT();

        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmation();
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibe.beep(getApplicationContext(), 100);
                WB_No=WB_N.getText().toString();
                if( CHECK_WB_GET(WB_No).equals("0")) {
                    Intent intent = new Intent(ReceiveActivity.this, PROD_LIST_ACT.class);
                    startActivity(intent);
                }else{  LIBES.MESS(ReceiveActivity.this, "ყურადღება", "ზედნადები უკვე მიღებულია!"); }


            }
        });

        order_number.setOnKeyListener((view, keyCode, keyEvent) -> {
            // Check if the key event is a key-up event and the Enter key is pressed
            if (keyEvent.getAction() == KeyEvent.ACTION_UP ) { //&& keyCode == KeyEvent.KEYCODE_ENTER
                // Handle the Enter key press here
                String ordernumber = order_number.getText().toString();
                fill_ORDERS(ordernumber);
                return true; // Consume the event
            }
            return false; // Let the event continue to be processed
        });


        receipt_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Vibe.beep(getApplicationContext(), 100);
            hideKeyboard(receipt_button);


            WB_No=WB_N.getText().toString();
          if( CHECK_WB_GET(WB_No).equals("0")) {
              WaybillService.getWaybillGoodsList(WaybillService.RS_USER, WaybillService.RS_PASS, WB_No, new WaybillService.OnTaskCompletedListener() {
                  @Override
                  public void onTaskCompleted(String XMLL) {
                      // Process the result here
//                    Log.d("WaybillService", "Received data: " + result);
                      String SELLER_NAME = "";
                      try {
                          SELLER_NAME = XMLL.substring(XMLL.lastIndexOf("<SELLER_NAME>") + "SELLER_NAME".length() + 2, XMLL.lastIndexOf("</SELLER_NAME>"));
                      } catch (Exception e) {
                      }
                      if (SELLER_NAME.equals("")) {
                          LIBES.MESS(ReceiveActivity.this, "", "არასწორი ნომერი !!!");
                      } else {
                          show_ask_dialog(SELLER_NAME, XMLL);
                          fill_ORDERS("");

                      }
//                     showToastLong(SELLER_NAME);
                  }
              });
          }else{  LIBES.MESS(ReceiveActivity.this, "ყურადრება", "ზედნადები უკვე მიღებულია!"); }


        }
    });
}
//----------------------------------------------------------------


    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
//----------------------------------------------------------------



public static String WB_No="";


public void fill_ORDERS(String ORDERN){

    try {
        StringBuffer buff =new StringBuffer();
        String ORDERS=" declare @WBCOUNT int set @WBCOUNT = ( select count(*) from  ["+SQL.DB_NAME+"].[dbo].[WB_GOODS]  )\n" +
                "\n" +
                " select  SO_N from(\n" +
                " select count(*)as connected_count,SO_N from  ["+SQL.DB_NAME+"].[dbo].[WB_GOODS]  \n" +
                " outer apply (select top 1 * from ["+SQL.DB_NAME+"].[dbo].[RS_CODES] where RS_SAID_CODE=Seller_tin+'-'+BAR_CODE) as t2 \n" +
                "left join ( select * from  ["+SQL.DB_NAME+"].[dbo].[SUPORD] where SO_sent=1 and SO_ACT=1) as t3\n" +
                "on RS_PID=SO_P_ID  and (QUANTITY*RS_INBOX)<=SO_N_shekveta_gasagz and SO_N is not null\n" +
                "group by SO_N\n" +
                " )as dd\n" +
                " where connected_count=@WBCOUNT ";
        ResultSet rs2w = SQL.SQL_ResultSet(ORDERS);
        while (rs2w.next()) {
            String username = (rs2w.getString("SO_N"));
            buff.append(username).append("-----");
            Log.d("WaybillService", "username: " + username);
        }
        LISTFILL(buff.toString(),R.id.listView_ORDERS);
    }catch (Exception e){  e.printStackTrace();}

}


    public String CHECK_WB_GET(String ZED){
        String count ="0";
        try {
            StringBuffer buff =new StringBuffer();
            String ORDERS="  select count(*) as WBCOUNT from  ["+SQL.DB_NAME+"].[dbo].[GET] where g_act='1' and g_zed='"+ZED+"'  ";
            ResultSet rs2w = SQL.SQL_ResultSet(ORDERS);
            while (rs2w.next()) {
                count = (rs2w.getString("WBCOUNT"));
            }

        }catch (Exception e){  e.printStackTrace();}
        return count;
    }






    public static String  DOC="";

    public void LISTFILL(String info, int ListVi){

        ListView ListV = (ListView)  findViewById(ListVi);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, info.toString().split("-----"));
        ListV.setAdapter(adapter);

        ListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) {

                   DOC=(String) ((TextView) v ).getText();
//                  DOC =DOC.split(PR)[0];
                order_number.setText(DOC);
            }
        });
    }








    private void showExitConfirmation(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);


        builder.setMessage("ნამდვილად გსურთ მთავარ გვერდზე დაბრუნება?");

        builder.setPositiveButton("კი", ((dialog, which) -> {
            finish();
        }));

        builder.setNegativeButton("არა", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void show_ask_dialog(String Supplier, String XMLL){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);


        builder.setMessage("მომწოდებელი: \""+Supplier+"\" გსურთ აღნიშნული მიღების გაგრძელება?");

        builder.setPositiveButton("კი", ((dialog, which) -> {
            Vibe.beep(getApplicationContext(), 100);
            WaybillService.CALI_ZEDNADEBIS_DASHLA(WB_N.getText().toString(),XMLL);
          get_process();
          
        }));

        builder.setNegativeButton("არა", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }


//       for(int i =0; i < temp.length ; i++){  }

    public static String WB_PRODUCT_NAME = "";
    public static String WB_PRODUCT_BAR = "";
    public static String WB_SELLER_TIN = "";

    public static void SEE_MISMATCHES(){
        WB_PRODUCT_NAME = ""; WB_PRODUCT_BAR = ""; WB_SELLER_TIN = "";

        String SQLL="  SELECT  top 1  WB_N, SELLER_TIN, FULL_AMOUNT, p_ID, W_NAME, UNIT_ID, QUANTITY, PRICE, AMOUNT, BAR_CODE, A_ID, VAT_TYPE, [STATUS]\n" +
                " FROM ["+SQL.DB_NAME+"].[dbo].[WB_GOODS]\n" +
                "  left join ["+SQL.DB_NAME+"].[dbo].[RS_CODES]\n" +
                "  on RS_SAID_CODE =SELLER_TIN+'-'+BAR_CODE\n" +
                "  where RS_id is null";

        try {
            ResultSet rs = SQL.SQL_ResultSet(SQLL);

            while (rs.next()) {
                WB_PRODUCT_NAME = rs.getString("W_NAME");
                WB_PRODUCT_BAR = rs.getString("BAR_CODE");
                WB_SELLER_TIN =  rs.getString("SELLER_TIN");
            }
        }catch(Exception e) {          }

    }


    public  void get_process(){
SEE_MISMATCHES();
 if(WB_PRODUCT_NAME.equals("")){

 }else{
     Intent intent = new Intent(ReceiveActivity.this, PRODActivity.class);
     startActivity(intent);
 }



  }










    public void showToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    // Method to display a long duration Toast
    public void showToastLong(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0); // Adjust position as needed
        // Set background color
        TextView toastTextView = toast.getView().findViewById(android.R.id.message);
        toastTextView.setBackgroundColor(Color.CYAN);

        // Set text size
        toastTextView.setTextSize(30);
        toast.show();
    }




    String  shvilii;
    String  shvilii_NAME="";
    String  shvilii_obj="";
    String YUTI="1";
    String getprice1="0";
    String  shvilii_ID="";
    String  shvilii_PRICE="";
    String  shvilii_P_QUANT="";
    String  shvilii_PR_STATUS="";
    String  shvilii_AQCIA="0";

    public void GET_SHVILOBILI(String said,String mama){
        String timme= LIBES.GET_CALENDAR_TIME_short().replace("-", "");

        shvilii="";
        YUTI="1";
        shvilii_NAME="";
        shvilii_obj="";
        getprice1="0";
        shvilii_ID="";
        shvilii_PRICE="";
        shvilii_P_QUANT="";
        shvilii_PR_STATUS="";
        shvilii_AQCIA="";
        try {
            String org="";
            String AGREEMENT="";

            String SQLL="SELECT  P_GROUP,PROD_AGR_ISACTIVE,PROD_AGR_PRICE,P_QUANT,P_ID,P_price,P_BARCODE,P_NAME,P_GETPRICE,P_organizacia,P_DISCOUNT, P_REC_PRICE, P_EXTRA_PR, P_EXTRA_START, P_EXTRA_END,  RS_ID,RS_PID, RS_SAID, RS_CODE, RS_SAID_CODE, RS_NAME, RS_INBOX \n" +
                    "  FROM (select * from[ATABLE].[dbo].[RS_CODES] where RS_SAID_CODE= N'"+mama+"' ) as rs \n" +
                    "  left join  [ATABLE].[dbo].PRODUCTS  \n" +
                    "  on P_ID= RS_PID \n" +
                    "  left join ( select * from [ATABLE].[dbo].DISTRIBUTORS where saidentifikacio= '"+said+"') as dist \n" +
                    "  on 1=1 \n" +
                    "  outer apply ( select top 1 * from  [ATABLE].[dbo].PROD_AGR  where PROD_AGR_PRICE>0 and P_UUID= PROD_AGR_GOODS and DIST_UUID=PROD_AGR_SUPPLIER and PROD_AGR_AGREEMENT='"+AGREEMENT+"' order by PROD_AGR_DATE desc ) as tt"
                    +org;
            ResultSet rs = SQL.SQL_ResultSet(SQLL
//        + "left join  [ATABLE].[dbo].PROD_AGR  \n" +
//"  on P_UUID= PROD_AGR_GOODS and DIST_UUID=PROD_AGR_SUPPLIER"
            );

            while (rs.next()) {
                shvilii=rs.getString("P_BARCODE");
                shvilii_NAME=rs.getString("P_NAME");
//            try { shvilii_NAME+="---"+ (rs.getString("P_GROUP"));  } catch (Exception e) {    }
                YUTI=rs.getString("RS_INBOX");
                getprice1=rs.getString("P_GETPRICE");
                String  P_AG_AC=rs.getString("PROD_AGR_ISACTIVE");
                try {   String  getprice2=rs.getString("PROD_AGR_PRICE"); if(getprice2 != null){getprice1=getprice2+"0";
                    if(P_AG_AC.equals("1") ){shvilii_PR_STATUS="<html><center><body bgcolor=\"34EB6B\" border=\"0\"><center>---------<br>ხელშ<br>--------</body>";}
                    if(P_AG_AC.equals("0")){shvilii_PR_STATUS="<html><center><body bgcolor=\"D51128\" border=\"0\"><center>---------<br>ხელშ<br>--------</body>";}
                }    } catch (Exception e) {  }
                try {   String  P_EXTRA_PR=rs.getString("P_EXTRA_PR");
                    String  P_EXTRA_START=rs.getString("P_EXTRA_START");
                    String  P_EXTRA_END=rs.getString("P_EXTRA_END");
                    if(Integer.parseInt(P_EXTRA_START)<=Integer.parseInt(timme) && Integer.parseInt(P_EXTRA_END)>=Integer.parseInt(timme)){
//     if(Double.parseDouble(P_EXTRA_PR)>0  ){getprice1=P_EXTRA_PR+"00";  shvilii_PR_STATUS="აქცია";  }
                        try {  shvilii_AQCIA=rs.getString("P_DISCOUNT");    if( Double.parseDouble(shvilii_AQCIA)>0){
                            shvilii_AQCIA="<html><body    bgcolor=\"F0EC0E\" border=\"0\"><center>---------<br>აქცია<br>--------</body>"+shvilii_AQCIA;}else{shvilii_AQCIA=""; };
                        } catch (Exception e) { shvilii_AQCIA=""; }
                    }} catch (Exception e) {  }
                shvilii_obj=rs.getString("P_organizacia");
                shvilii_ID=rs.getString("P_ID");
                shvilii_PRICE=rs.getString("P_price");
                shvilii_P_QUANT=rs.getString("P_QUANT");
            }
        }catch(Exception dsfigh){}

    }





}

