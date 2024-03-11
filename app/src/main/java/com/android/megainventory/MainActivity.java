package com.android.megainventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity {

    private static final String LOGCAT = null;
    //    EditText database_address;
//    EditText database_name;
//    EditText database_port;
//    Button submit_button;
//    private Context context;
    DBController dbController = new DBController(this);

    Button download_button;
    Button print_button;
    Button description_button;
    Button receive_button;
    Button return_button;
    Button parameters;

    private static final int REQUEST_CODE_BLUETOOTH_CONNECT = 1;
    @SuppressLint({"Range", "ResourceType"})

    public static String PROXYSERVER="";
    public static int PROXYPORT=0;


    public void GET_PREFERENCES(){

        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("app_info", MainActivity.MODE_PRIVATE);

        if (sharedPreferences.contains("server_ip")) {
            String serverIp = sharedPreferences.getString("server_ip", "");
            String serverPass = sharedPreferences.getString("server_pass", "");
            String objectName = sharedPreferences.getString("object_name", "");
            String proxyServerIp = sharedPreferences.getString("proxy_server_ip", "");

            SQL.DB_HOST = serverIp;
            SQL.DB_NAME = objectName;
            SQL.USER = objectName;
            SQL.PASSWORD = serverPass;


            try {  String []PX=proxyServerIp.split(":"); PROXYSERVER=PX[0]; PROXYPORT=Integer.parseInt(PX[1]);  }catch (Exception e) {}

        } else {
            Intent intent = new Intent(MainActivity.this, SaveInfoActivity.class);
            startActivity(intent);
        }

        String SQLL="SELECT distinct case when PAR_DEV='RSuser' then PAR_VAL_ST end as RSUSER , case when PAR_DEV='RSpass' then PAR_VAL_ST end as RSPASS\n" +
                "from ["+SQL.DB_NAME+"].[dbo].[PARAM] where PAR_DEV='RSuser' or PAR_DEV='RSpass' and len(PAR_VAL_ST)>0 and PAR_VAL_ST is not null ";
        try {
            ResultSet rs = SQL.SQL_ResultSet_no_loop(SQLL);

            while (rs.next()) {
           try {  String  RSUSER = rs.getString("RSUSER");  if(RSUSER!=null){WaybillService.RS_USER=RSUSER;} }catch (Exception e) {}
           try {  String  RSPASS = rs.getString("RSPASS");  if(RSPASS!=null){WaybillService.RS_PASS=RSPASS;} }catch (Exception e) {}
           LIBES.MESS(MainActivity.this,"User Connected",WaybillService.RS_USER);
            }
        }catch(Exception e) {          }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        GET_PREFERENCES();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download_button = findViewById(R.id.download_button);
        print_button = findViewById(R.id.print_button);
        description_button = findViewById(R.id.description_button);
        receive_button = findViewById(R.id.receive_button);
        return_button = findViewById(R.id.return_button);
        parameters = findViewById(R.id.parameters);


        // Open Download Page
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
                Vibe.beep(getApplicationContext(), 100);
            }
        });

        // Open Print Page
        print_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrintActivity.class);
                startActivity(intent);
                Vibe.beep(getApplicationContext(), 100);
            }
        });


        // Open Receive Page
        receive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiveActivity.class);
                startActivity(intent);
                Vibe.beep(getApplicationContext(), 100);
            }
        });


        // Open Return Page
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReturnActivity.class);
                startActivity(intent);
                Vibe.beep(getApplicationContext(), 100);
            }
        });


        // Open Description Page
        description_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
                startActivity(intent);
                Vibe.beep(getApplicationContext(), 100);
            }
        });

        // Open parameters Page
        parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONFIRM_PASS("PLease enter Password");

            }
        });

//        database_address = findViewById(R.id.database_address);
//        database_name = findViewById(R.id.database_name);
//        database_port = findViewById(R.id.database_port);
//        submit_button = findViewById(R.id.submit_button);
//
//
//        // Select Database Information From SQLite
//        @SuppressLint("Range") String DATABASE_ADDRESS = "";
//        @SuppressLint("Range") String DATABASE_NAME = "";
//        @SuppressLint("Range") String DATABASE_PORT = "";
//        Cursor cursor = dbController.getData();
//        if(cursor != null){
//            while (cursor.moveToNext()){
//                DATABASE_ADDRESS = cursor.getString(cursor.getColumnIndex("DATABASE_ADDRESS"));
//                DATABASE_NAME = cursor.getString(cursor.getColumnIndex("DATABASE_NAME"));
//                DATABASE_PORT = cursor.getString(cursor.getColumnIndex("DATABASE_PORT"));
//
//                setContentView(R.layout.web_view);
//            }
//        }else{
//            setContentView(R.layout.database_info);
//        }
//
//        cursor.close();
//        dbController.close();
//
//        System.out.println("address: " + DATABASE_ADDRESS + " name: " + DATABASE_NAME + " port: " + DATABASE_PORT);
//
//        submit_button.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("Range")
//            @Override
//            public void onClick(View view) {
//
//                // Save Inserted Database Information In Variables
//                String databaseAddress = database_address.getText().toString();
//                String databaseName = database_name.getText().toString();
//                String databasePort = database_port.getText().toString();
//                System.out.println("Database address: " + databaseAddress + "\n" +
//                        "Database Name: " + databaseName + "\n" +
//                        "Database Port: " + databasePort);
//
//
//                try {
//
//                    // Insert Database Information In SQLite
//                    dbController.insertDataToDatabase(databaseAddress, databaseName, databasePort);
//                    Toast.makeText(getApplicationContext(), "Information Inserted Successfully", Toast.LENGTH_SHORT).show();
//
//
//
//
//
//
//
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                    Log.d(LOGCAT, String.valueOf(e));
//                }
//            }
//        });





//        String htmlTable = "<html><body><table border='1'>";

//        WebView webView = (WebView) findViewById(R.id.web_view);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("file:///android_asset/index.html");
//
//        WebSettings webSettings = webView.getSettings();
//
//        webView.addJavascriptInterface(new JavaScriptInterface(this), "AndroidInterface");

    }

//----------------------------------------------------------------







    private void CONFIRM_PASS(String message ) {

//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
//        final EditText input = dialogView.findViewById(R.id.editText);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); // Set max length if needed
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setView(input);
        input.setText("");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                if(userInput.equals("415145")){
                    Intent intent = new Intent(MainActivity.this, SaveInfoActivity.class);
                    startActivity(intent);
                    Vibe.beep(getApplicationContext(), 100);
                }

                // Do something with the user input
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
//----------------------------------------------------------------

}