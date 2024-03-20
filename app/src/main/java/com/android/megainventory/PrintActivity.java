package com.android.megainventory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PrintActivity extends AppCompatActivity {

    public static final String DB_HOST = "192.168.1.5";
    private static final int DB_PORT = 1433;
    public static final String DB_NAME = "FM2";
    public static final String USER = "FM2";
    public static final String PASSWORD = "#$%1018545246ERT";

    AppCompatButton go_back_button;
    AppCompatButton print_button;
    AppCompatEditText bar_code;
    AppCompatButton minus_button;
    AppCompatButton plus_button;
    AppCompatTextView count_product;
    AppCompatImageButton accept_button;
    AppCompatImageButton delete_button;
    AppCompatImageButton list_button;
    AppCompatTextView saved_product_list_count;

    protected List<ListItem> product_list_for_print;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        go_back_button = findViewById(R.id.go_back_button);
        print_button = findViewById(R.id.print_button);
        bar_code = findViewById(R.id.bar_code);
        minus_button = findViewById(R.id.minus_button);
        plus_button = findViewById(R.id.plus_button);
        count_product = findViewById(R.id.count_product);
        accept_button = findViewById(R.id.accept_button);
        delete_button = findViewById(R.id.delete_button);
        list_button = findViewById(R.id.list_button);
        saved_product_list_count = findViewById(R.id.saved_product_list_count);

        product_list_for_print = new ArrayList<>();

        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmation();
            }
        });

        print_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printProducts();
            }
        });

        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusProductCount();
            }
        });

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusProductCount();
            }
        });

        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveInputedItems();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductList();
            }
        });
    }


    // Exit From Download Page, Using Alert Dialog
    private void showExitConfirmation() {

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


    private void printProducts() {
//        AsyncTask<Void, Void, Void> zebraPrinterService = new ZebraPrinterService(PrintActivity.this,).execute();
    }

    // -1 Every Time -1 Button Is Clicked
    @SuppressLint("SetTextI18n")
    private void minusProductCount() {
        String written_count_product = count_product.getText().toString();
        int product_count = Integer.parseInt(written_count_product);

        if (product_count > 0) {
            product_count--;
            count_product.setText(Integer.toString(product_count));
        }

    }


    // +1 Every Time +1 Button Is Clicked
    @SuppressLint("SetTextI18n")
    private void plusProductCount() {
        String written_count_product = count_product.getText().toString();
        int product_count = Integer.parseInt(written_count_product);

        product_count++;
        count_product.setText(Integer.toString(product_count));

    }

    // Save Scanned Product In The Specified Quantity
    @SuppressLint("SetTextI18n")
    private void saveInputedItems() throws Exception {
        String inserted_bar_code = bar_code.getText().toString();
        System.out.println(inserted_bar_code);


        try {

            String escaped_inserted_bar_code = inserted_bar_code.replace("'", "''");


            String query = String.format("SELECT top 1 P_ID, P_CODE, REPLACE(P_NAME_SALE, '\"', '') as P_NAME_SALE ,P_PRICE,P_DISCOUNT,P_BARCODE,P_UUID,P_DAFAULTSUPPLIER,P_GOODSGROUP from ATABLE.dbo.PRODUCTS left join ( select * from [ATABLE].[dbo].[BARCODES] where B_BARCODE='%s') as t2 on B_P_UUID=P_UUID where P_BARCODE='%s' or P_CODE='%s' or B_BARCODE='%s'", inserted_bar_code, inserted_bar_code, inserted_bar_code, inserted_bar_code);
            ResultSet resultSet = SQL.SQL_ResultSet(query);
            while (resultSet.next()) {


                String P_BARCODE = (resultSet.getString("P_BARCODE"));
                String P_NAME_SALE = (resultSet.getString("P_NAME_SALE"));
                String P_PRICE = (resultSet.getString("P_PRICE"));
//                String  CurrentDate=(resultSet.getString("CurrentDate"));
                String P_GOODSGROUP = (resultSet.getString("P_GOODSGROUP")).replace("---", "--").replace("--", "-").replace(" ", "");
                String P_DAFAULTSUPPLIER = (resultSet.getString("P_DAFAULTSUPPLIER")).replace(" ", "");
                String P_UUID = (resultSet.getString("P_UUID")).replace(" ", "");
                System.out.println(P_BARCODE + P_NAME_SALE + P_PRICE + P_GOODSGROUP + P_DAFAULTSUPPLIER + P_UUID);

                ListItem listItem = new ListItem(P_BARCODE, P_NAME_SALE, P_PRICE, P_GOODSGROUP, P_DAFAULTSUPPLIER, P_UUID, Integer.parseInt(count_product.getText().toString()));
                product_list_for_print.add(listItem);

            }
        } catch (Exception e) {
            System.out.println(e);
            ;
        }

        saved_product_list_count.setText(Integer.toString(product_list_for_print.size()));

    }

    public void showProductList() {

        View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int peekHeight = (int) (screenHeight * 0.7f);


        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, peekHeight);
        bottomSheetLayout.setLayoutParams(layoutParams);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetLayout);


        ListView listView = bottomSheetDialog.findViewById(R.id.bottom_sheet_list);


        AppCompatTextView product_count_view = bottomSheetLayout.findViewById(R.id.product_count);
        AppCompatTextView product_count_whole_view = bottomSheetLayout.findViewById(R.id.product_count_whole);


        CustomListViewPrint adapter = new CustomListViewPrint(this, R.layout.bottom_sheet_layout, product_list_for_print, product_count_view, product_count_whole_view);
        listView.setAdapter(adapter);


        bottomSheetDialog.show();
    }
}
