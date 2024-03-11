package com.android.megainventory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class PROD_LIST_ACT extends AppCompatActivity {

    Button go_back_button;
    EditText SCAN_TEXT;
    EditText order_number;
    Button start_button;
    ListView LISTV;
//    ImageButton receipt_button;

    private Handler scan_handler;
    private boolean scan_isProcessing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        go_back_button = findViewById(R.id.go_back_button3);
        SCAN_TEXT = findViewById(R.id.SCAN_TEXT);
        SCAN_TEXT.setInputType(android.text.InputType.TYPE_NULL);

        LISTV=findViewById(R.id.listView_PROD1);
//        order_number = findViewById(R.id.order_number3);
        start_button = findViewById(R.id.start_button3);

//        receipt_button = findViewById(R.id.receipt_button3);

//        SQL.CONNECT();

        scan_handler = new Handler(Looper.getMainLooper());

        SCAN_TEXT.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (!scan_isProcessing && (actionId == KeyEvent.KEYCODE_ENTER || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                scan_isProcessing = true;
                // Add a delay of 1 second (1000 milliseconds) before processing the barcode
                scan_handler.postDelayed(this::processBarcode, 200);
                return true;
            }
            return false;
        });


        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmation();
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer buff =new StringBuffer();

                if( CHECK_ACTUAL_metoba(ReceiveActivity.WB_No).equals("0")) {
                    RECEIVING_FINISH();

                }else{  LIBES.MESS(PROD_LIST_ACT.this, "ყურადღება", "შეუსაბამო რაოდენობა!"); }

//                String tableHtml =   ("<font color='red' style='border:1px solid red;padding:2px;'>fox</font> <font color='green' style='border:1px solid green;padding:2px;'>running</font>");

//                buff.append("darbis").append("-----");
//                buff.append("gaqceuli ").append("-----");
//                buff.append("taxi").append("-----");
//                LISTFILL(buff.toString(),R.id.listView_PROD1);

//                LISTFILL("2-----",R.id.listView_PROD2);
            }
        });

//        order_number.setOnKeyListener((view, keyCode, keyEvent) -> {
//            // Check if the key event is a key-up event and the Enter key is pressed
//            if (keyEvent.getAction() == KeyEvent.ACTION_UP ) { //&& keyCode == KeyEvent.KEYCODE_ENTER
//                // Handle the Enter key press here
//                String ordernumber = order_number.getText().toString();
//                fill_ORDERS(ordernumber);
//                return true; // Consume the event
//            }
//            return false; // Let the event continue to be processed
//        });




//        receipt_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                WaybillService.getWaybillGoodsList("fms:405648893","473822", WB_N.getText().toString(), new WaybillService.OnTaskCompletedListener() {
//                    @Override
//                    public void onTaskCompleted(String XMLL) {
//                        // Process the result here
////                    Log.d("WaybillService", "Received data: " + result);
//                        String SELLER_NAME=""; try{SELLER_NAME=XMLL.substring(XMLL.lastIndexOf("<SELLER_NAME>")+"SELLER_NAME".length()+2, XMLL.lastIndexOf("</SELLER_NAME>") );    } catch (Exception e) {  }
//
//                        show_ask_dialog(SELLER_NAME,XMLL);
//
////                     showToastLong(SELLER_NAME);
//                    }
//                });
//                fill_ORDERS("");
//            }
//        });

        fill_PRODUCTS();
        SCAN_TEXT.requestFocus();
//        hideKeyboard(SCAN_TEXT);

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public String CHECK_ACTUAL_metoba(String ZED){
        String count ="0";
        try {
            StringBuffer buff =new StringBuffer();
            String ORDERS="  SELECT  count(*) as metoba FROM ["+SQL.DB_NAME+"].[dbo].[WB_GOODS] where SCANED>QUANTITY    and WB_N='"+ZED+"'  ";
            ResultSet rs2w = SQL.SQL_ResultSet(ORDERS);
            while (rs2w.next()) {
                count = (rs2w.getString("metoba"));
            }

        }catch (Exception e){  e.printStackTrace();}
        return count;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    public void RECEIVING_FINISH() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

            builder.setMessage("გსურთ მიღების დასრულება?");

            builder.setPositiveButton("კი", ((dialog, which) -> {
                RECEIVING_ACT();
                finish();
            }));

            builder.setNegativeButton("არა", ((dialog, which) -> {
                dialog.dismiss();
            }));

            AlertDialog dialog = builder.create();
            dialog.show();
        }
/////////////////////////////////////////////////////////////////////


    public void RECEIVING_ACT() {


        String finishQuery = "";
        String AGR = "";

        finishQuery += "declare @AGR nvarchar(50)\n" +
                "        set @AGR = (SELECT TOP 1 SO_AG_CODE FROM [" + SQL.DB_NAME + "].[dbo].SUPORD WHERE SO_N = N'" + ReceiveActivity.DOC + "')\n" +
                "        declare @ZED nvarchar(50)  set @ZED = N'" + ReceiveActivity.WB_No + "'\n" +
                "        DECLARE @CurrentTime bigint  SET @CurrentTime = FORMAT(GETDATE(), 'yyMMddHHmm')\n" +
                "\n" +
                "\n" +
                "        declare @SUPvat nvarchar(50)\n" +
                "        declare @SUPID nvarchar(50)\n" +
                "        SELECT TOP 1 @SUPID=[ID], @SUPvat=[vat] from [" + SQL.DB_NAME + "].[dbo].[DISTRIBUTORS] where saidentifikacio=N'" + WaybillService.SELLER_TIN + "'\n" +
                "        declare  @SUPvatx  decimal =cast (replace(replace(replace(replace(@SUPvat,N'კი','1'),'yes','1'),N'არა','0'),'no','0')  as decimal (18,4))\n" +
                "\n" +
                "\n" +

                "        update [" + SQL.DB_NAME + "].dbo.GET\n" +
                "        set G_ACT='0'\n" +
                "        where G_ZED = @ZED \n\n\n\n";


        String QUANT="SCANED";
        if (P_OrderType.equals("Picking")) { QUANT="QUANTITY";  }


        finishQuery += "  insert into [" + SQL.DB_NAME + "].dbo.GET (G_SENT,G_D_ID, G_ZED, G_USER_ID, G_P_ID, G_QUANT, G_PRICE, G_VAT, G_SELLPRICE, G_OBJ, G_TIME,G_AGR, G_ORD)\n" +
                "  SELECT  '2',@SUPID, @ZED, '1', RS_PID, "+QUANT+", PRICE, case when VAT_TYPE=0 then(PRICE/118.0)*18.0*@SUPvatx else 0 end ,\n" +
                "            P_PRICE, N'" + SQL.DB_NAME + "', @CurrentTime, @AGR, N'" + ReceiveActivity.DOC + "'   FROM [" + SQL.DB_NAME + "].[dbo].[WB_GOODS]\n" +
                "            left join [" + SQL.DB_NAME + "].[dbo].[RS_CODES] as t2  on RS_SAID_CODE =SELLER_TIN+'-'+BAR_CODE  \n" +
                "            left join  ( select p_ID,P_PRICE from [" + SQL.DB_NAME + "].[dbo].[PRODUCTS] )as t3  on t3.P_ID =RS_PID\n" +
                "            where WB_N=@ZED" +
                "\n\n\n\n";


        finishQuery += "declare @gt table\n" +
                "        (\n" +
                "                tim bigint,\n" +
                "                id  bigint\n" +
                "            )\n" +
                "        insert into @GT\n" +
                "        SELECT min(G_TIME), G_P_ID\n" +
                "        FROM ["+SQL.DB_NAME+"].[dbo].[GET]\n" +
                "        where G_ZED = @ZED\n" +
                "                group by G_P_ID\n" +
                "\n" +
                "        declare @tim bigint\n" +
                "        declare @id  bigint\n" +
                "        while (select COUNT(*)\n" +
                "        from @gt) > 0\n" +
                "        begin\n" +
                "        select top 1 @tim = tim, @id = id from @gt\n" +
                "        EXEC ["+SQL.DB_NAME+"].[dbo].[SIMULACIA_ACT] @PP_ID =@id , @T_START =@tim\n" +
                "        delete @gt where ID = @id\n" +
                "                end\n" +
                "\n" +
                "        exec ["+SQL.DB_NAME+"].[dbo].[Update_Getprices]";


        SQL.SQL_Statement(finishQuery);

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    private void processBarcode() {
        String BAR = SCAN_TEXT.getText().toString().trim();
//        Toast.makeText(PROD_LIST_ACT.this, "Scanned Barcode: " + BAR, Toast.LENGTH_SHORT).show();
        // Clear EditText for the next scan
        SCAN_TEXT.setText("");
        ADD_QUANT_InputDialog(BAR+"\r\nშეიყვანეთ რაოდენობა",BAR);
        scan_isProcessing = false;

    }
    //////////////////////////////////


    String P_OrderType ="";

    public void fill_PRODUCTS(){

        try {
            String order=ReceiveActivity.DOC;
            String WB=ReceiveActivity.WB_No;

            StringBuffer buff =new StringBuffer();
            ResultSet rs2w = SQL.SQL_ResultSet(
                    " select   P_OrderType,P_NAME,P_BARCODE,SO_P_ID, cast(SO_N_shekveta_gasagz as decimal (18,2)) as ORDER_QUANT, " +
                            "Seller_tin, BAR_CODE, W_NAME, QUANTITY, PRICE, VAT_TYPE, PROD_AGR_PRICE,SCANED \n" +
                    "from( select SO_N,SO_P_ID,SO_N_shekveta_gasagz from  ["+SQL.DB_NAME+"].[dbo].[SUPORD] where   SO_ACT=1 and so_N='"+order+"')as t1\n" +
                    " outer apply (select top 1 RS_SAID_CODE  from ["+SQL.DB_NAME+"].[dbo].[RS_CODES] where SO_P_ID=RS_PID) as t2 \n" +
                    "left join   (select SCANED,Seller_tin,BAR_CODE,W_NAME,QUANTITY,PRICE,VAT_TYPE from ["+SQL.DB_NAME+"].[dbo].[WB_GOODS] where WB_N ='"+WB+"')  as t3\n" +
                    "on   RS_SAID_CODE=(Seller_tin+'-'+BAR_CODE)\n" +
                    "left join (select P_ID,P_UUID,P_NAME,P_BARCODE,P_OrderType  from ["+SQL.DB_NAME+"].[dbo].PRODUCTS) as t4\n" +
                    "on t4.P_ID=SO_P_ID\n" +
                    " outer apply (select top 1 [PROD_AGR_PRICE] from ["+SQL.DB_NAME+"].[dbo].[PROD_AGR] where PROD_AGR_GOODS=P_UUID and PROD_AGR_SUPPLIER=Seller_tin) as t5 ");
            while (rs2w.next()) {
                String SO_P_ID = (rs2w.getString("SO_P_ID"));
                String ORDER_QUANT = (rs2w.getString("ORDER_QUANT"));
                String BAR_CODE = (rs2w.getString("BAR_CODE"));
                String W_NAME =BAR_CODE+"<br>"+ (rs2w.getString("W_NAME"));
                String P_BARCODE = (rs2w.getString("P_BARCODE"));
                String P_NAME =P_BARCODE+"<br>"+ (rs2w.getString("P_NAME"));
                String QUANTITY = (rs2w.getString("QUANTITY"));
                String PRICE = (rs2w.getString("PRICE"));
                String VAT_TYPE = (rs2w.getString("VAT_TYPE"));
                String PROD_AGR_PRICE = (rs2w.getString("PROD_AGR_PRICE"));
                String SCANED = (rs2w.getString("SCANED"));
                 try { P_OrderType = (rs2w.getString("P_OrderType")); } catch (Exception e) { }

                String W_NAME_out=W_NAME;
                try { if(W_NAME_out.contains("null")){W_NAME_out=P_NAME;W_NAME=P_NAME;} } catch (Exception e) {W_NAME_out=P_NAME;W_NAME=P_NAME;}


                Double ORD_PRICE=0.0; try { ORD_PRICE=Double.parseDouble(PROD_AGR_PRICE);}catch ( Exception e){}
                Double ORD_QUANT=0.0; try { ORD_QUANT=Double.parseDouble(ORDER_QUANT);}catch ( Exception e){}
                Double WB_PRICE=0.0;  try { WB_PRICE=Double.parseDouble(PRICE);}catch ( Exception e){}
                Double WB_QUANT=0.0;  try { WB_QUANT=Double.parseDouble(QUANTITY);}catch ( Exception e){}
                Double SC_SCANED=0.0; try { SC_SCANED=Double.parseDouble(SCANED);}catch ( Exception e){}
                                      try { if(VAT_TYPE.equals("0")) {ORD_PRICE=ORD_PRICE+((ORD_PRICE/100)*18); PROD_AGR_PRICE=Double.toString(ORD_PRICE); }}catch ( Exception e){}

                if(WB_PRICE>ORD_PRICE){W_NAME_out="<font color='red'>"+W_NAME+"</font>"; PRICE= "<font color='red'>"+PRICE+"</font>";    }
                if(WB_QUANT<ORD_QUANT){W_NAME_out="<font color='red'>"+W_NAME+"</font>"; QUANTITY= "<font color='red'>"+QUANTITY+"</font>";    }
                if(SC_SCANED<ORD_QUANT || SC_SCANED<WB_QUANT ){W_NAME_out="<font color='red'>"+W_NAME+"</font>"; SCANED= "<font color='red'>"+SCANED+"</font>";    }



                buff.append(W_NAME_out).append("<br>");
                buff.append("Price: "+ PROD_AGR_PRICE).append("&nbsp;&nbsp;RS:"+PRICE).append("<br>");
                buff.append("Quant:"+ORDER_QUANT).append("&nbsp;&nbsp;RS:"+ QUANTITY).append("&nbsp;&nbsp;AC:"+ SCANED).append("<br>");

     try { if(PRICE==null){   buff.append("<span style=\"color:black; background-color:red;\">პროდუქცია არ არის ზედნადებში</span>").append("<br>");} } catch (Exception e) { }
                buff.append("-----");
//                Log.d("WaybillService", "username: " + username);
            }
//        String htmlText = "<font color='red'>fox</font> <font color='green'>running</font>";
//
//        buff.append(htmlText).append("-----");
            LISTFILL(buff.toString(),R.id.listView_PROD1);
        }catch (Exception e){  e.printStackTrace();}

    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    public void LISTFILL(String info, int ListVi){

        ListView ListV = (ListView)  findViewById(ListVi);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, info.toString().split("-----"));      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,


        List<String> htmlStrings = Arrays.asList(info.toString().split("-----"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, htmlStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                Spanned htmlText = HtmlCompat.fromHtml(getItem(position), HtmlCompat.FROM_HTML_MODE_LEGACY);
                textView.setText(htmlText);
                return view;
            }
        };

        ListV.setAdapter(adapter);


        ListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) {

//             v.setBackgroundColor(Color.RED);
//                String  DOC=(String) ((TextView) v ).getText();
                String selectedText = htmlStrings.get(position);
                String BAR=selectedText.split("<br>")[0].replace("<font color='red'>","");
                ADD_QUANT_InputDialog(BAR+"\r\nშეიყვანეთ რაოდენობა",BAR);
//                  DOC =DOC.split(PR)[0];
//                order_number.setText(DOC);
            }
        });
    }
////////////////////////////////////////////////////////////////


    private void ADD_QUANT_InputDialog(String message, String BARCODE) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL); // Allow numbers and negative sign
        input.setKeyListener(DigitsKeyListener.getInstance("0123456789-")); // Allow only digits and minus sign
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setView(input);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show keyboard when dialog is shown
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                input.requestFocus(); // Request focus for the EditText

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200); // Adjust the delay time as needed (200 milliseconds in this example)

            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();
                try {
                    Double.parseDouble(userInput);
                    String scanned = ADD_SCANNED(userInput, BARCODE);
                    if (scanned.equals("0")) {
                        Vibe.beep(getApplicationContext(), 1000);
                        LIBES.MESS(PROD_LIST_ACT.this, "არასწორი შტრიქოდი !!!", BARCODE);
                    } else {
                        fill_PRODUCTS();
                        hideKeyboard(input);
                    }
                } catch (Exception d) {
                    LIBES.MESS(PROD_LIST_ACT.this, "არასწორი რაოდენობა !!!", BARCODE);
                }
            }
        });

        dialog.show();
    }

//----------------------------------------------------------------

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
//----------------------------------------------------------------

    public String ADD_SCANNED(String ADDQUANT, String BAR) {
        String is_scaned="0";
        try { Double.parseDouble(ADDQUANT);

            String WB = ReceiveActivity.WB_No;
//            new StringBuffer();
            String SQLL=" declare @barco varchar (30) set @barco='" + BAR + "'\n" +
                    "\n" +//kods ezebs yvela shesazlo adgilas>>>>>>>>>>
                    "set @barco= (select   top 1 t0.BAR_CODE  FROM ["+SQL.DB_NAME+"].[dbo].[WB_GOODS] as t0\n" +
                    "left join ["+SQL.DB_NAME+"].[dbo].[RS_CODES] as t1\n" +
                    "on RS_SAID_CODE =SELLER_TIN+'-'+BAR_CODE                 \n" +
                    "left join (select  P_ID, P_UUID, P_CODE, P_BARCODE, P_NAME, P_GROUP from ["+SQL.DB_NAME+"].[dbo].[products] ) as t2 \n" +
                    "on t2.P_ID  =t1.RS_PID  \n" +
                    "outer apply (SELECT STUFF((SELECT CAST(B_BARCODE+'---' AS VARCHAR(MAX))\n" +
                    "FROM  ["+SQL.DB_NAME+"].[dbo].BARCODES where B_P_UUID=P_UUID    FOR XML PATH('')), 1, 0, '') AS BUNCH_BARCODE) as stuf  \n" +
                    "where BUNCH_BARCODE like '%'+@barco+'---%' or RS_CODE=@barco or P_CODE=@barco or P_BARCODE=@barco)\n" +
                    "\n" +//kods ezebs yvela shesazlo adgilas<<<<<<<<<<<<<

                    "" +  //ro ipovis uketebs updates>>>>>>>>>>>>>>>>>
                    "update ["+SQL.DB_NAME+"].[dbo].[WB_GOODS] set SCANED=SCANED+'" + ADDQUANT + "' where BAR_CODE=@barco and WB_N ='" + WB + "'\r\n " +
                    " SELECT @@ROWCOUNT AS RowsAffected; ";
            try {
                ResultSet rs = SQL.SQL_ResultSet(SQLL);

                while (rs.next()) {
                    is_scaned = rs.getString("RowsAffected");
                }
            }catch(Exception e) {          }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  is_scaned;
    }
//----------------------------------------------------------------





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
            Intent intent = new Intent(PROD_LIST_ACT.this, PRODActivity.class);
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

