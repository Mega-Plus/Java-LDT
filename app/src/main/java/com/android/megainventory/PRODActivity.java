package com.android.megainventory;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
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


public class PRODActivity extends AppCompatActivity {


    TextView header;
    TextView wb_product;
    EditText search;
    ImageButton search_button;
    Button connect_button;
    Button go_back_button;
    TextView selected2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod);

        header = findViewById(R.id.header);
        wb_product = findViewById(R.id.wb_product);
        go_back_button = findViewById(R.id.go_back_button2);
        search = findViewById(R.id.search_text);
        connect_button = findViewById(R.id.connect_button);
        search_button = findViewById(R.id.search_button);
        selected2 = findViewById(R.id.selected2);

        header.setTextColor(Color.BLACK);

        wb_product.setText(ReceiveActivity.WB_PRODUCT_NAME + "\n" + ReceiveActivity.WB_PRODUCT_BAR);
        wb_product.setTextColor(Color.RED);

        search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

        wb_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText(ReceiveActivity.WB_PRODUCT_BAR);
            }
        });

        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmation();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameorbar = search.getText().toString();
                fill_PRODS(nameorbar);
                hideKeyboard(search);
            }
        });

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect_BARCODE();
            }
        });
    }
//----------------------------------------------------------------


    public void Connect_BARCODE() {
        String[] SELECTED_PRODUCT = selected2.getText().toString().replace("\'", "\"").split("\r\n");
//    buff.append(p_name+"\r\n").append(p_barcode+"     |").append(p_id).append("-----");

        String RS_NAME = ReceiveActivity.WB_PRODUCT_NAME;
        String CODE = ReceiveActivity.WB_PRODUCT_BAR;
        String PID = SELECTED_PRODUCT[1].split("     \\|")[1];

        String said = ReceiveActivity.WB_SELLER_TIN;

        String newbar = said + "-" + CODE;
        String inbox = userInput;

        String sql = "INSERT INTO " + SQL.DB_NAME + ".dbo.RS_CODES (RS_PID,RS_SAID,RS_CODE,RS_SAID_CODE,RS_NAME,RS_INBOX ) " +
                "VALUES('" + PID + "','" + said + "',N'" + CODE + "',N'" + newbar + "',N'" + RS_NAME + "','" + inbox + "')";
        SQL.SQL_Statement(sql);

        //after connection check for other unconnected products
        ReceiveActivity.SEE_MISMATCHES();

        if (ReceiveActivity.WB_PRODUCT_NAME.equals("")) {
            finish();
        } else {
            wb_product.setText(ReceiveActivity.WB_PRODUCT_NAME + "\n" + ReceiveActivity.WB_PRODUCT_BAR);

            search.setText("");
            selected2.setText("");

            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

        }

    }
//----------------------------------------------------------------


    public void fill_PRODS(String ORDERN) {

        try {
            StringBuffer buff = new StringBuffer();
            try {
                ResultSet rs2w = SQL.SQL_ResultSet("SELECT  p_id,p_name,p_barcode  FROM [" + SQL.DB_NAME + "].[dbo].[PRODUCTS] where p_name   like N'%" + ORDERN + "%' or p_barcode   like'%" + ORDERN + "%'");
                while (rs2w.next()) {

                    String p_id = (rs2w.getString("p_id"));
                    String p_barcode = (rs2w.getString("p_barcode").replace("|", ""));
                    String p_name = (rs2w.getString("p_name").replace("|", ""));

                    buff.append(p_name + "\r\n").append(p_barcode + "     |").append(p_id).append("-----");
//                Log.d("WaybillService", "username: " + username);


                }
            } catch (Exception e) {
            }
            LISTFILL(buff.toString(), R.id.listView_products);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//----------------------------------------------------------------

//public static String P_ID_SHARED="";

    public void LISTFILL(String info, int ListVi) {

        ListView ListV = (ListView) findViewById(ListVi);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, info.toString().split("-----"));
        ListV.setAdapter(adapter);

        ListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String DOC = (String) ((TextView) v).getText();
//                  DOC =DOC.split(PR)[0];
                selected2.setText(DOC);
                showNumberInputDialog("მიუთითეთ ჩასაშლელი რაოდენობა \n(მაგალითად თუ სიგარეტი ზედნადებში ბლოკით წერია, მიუთითეთ 10, სხვა შემთხვევებში უბრალოდ 1 ჩაწერეთ) ");
            }
        });
    }
//----------------------------------------------------------------


    private void showExitConfirmation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);


        builder.setMessage("ნამდვილად გსურთ წინა გვერდზე დაბრუნება?");

        builder.setPositiveButton("კი", ((dialog, which) -> {
            finish();
        }));

        builder.setNegativeButton("არა", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }
//----------------------------------------------------------------


    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
//----------------------------------------------------------------


    public void showToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
//----------------------------------------------------------------

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
//----------------------------------------------------------------


    String userInput = "";

    private void showNumberInputDialog(String message) {

//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
//        final EditText input = dialogView.findViewById(R.id.editText);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); // Set max length if needed
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setView(input);
        input.setText("1");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userInput = input.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);


                // Do something with the user input
            }
        });

//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });

        builder.show();

    }
//----------------------------------------------------------------


}

