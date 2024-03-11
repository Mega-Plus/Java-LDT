package com.android.megainventory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PrintActivity extends AppCompatActivity {

    Button go_back_button;
    EditText bar_code;
    Button minus_button;
    Button plus_button;
    TextView count_product;
    ImageButton accept_button;
    ImageButton delete_button;
    ImageButton list_button;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        go_back_button = findViewById(R.id.go_back_button);
        bar_code = findViewById(R.id.bar_code);
        minus_button = findViewById(R.id.minus_button);
        plus_button = findViewById(R.id.plus_button);
        count_product = findViewById(R.id.count_product);
        accept_button = findViewById(R.id.accept_button);
        delete_button = findViewById(R.id.delete_button);
        list_button = findViewById(R.id.list_button);

        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmation();
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
                saveInputedItems();
            }
        });
    }


    // Exit From Download Page, Using Alert Dialog
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


    // -1 Every Time -1 Button Is Clicked
    @SuppressLint("SetTextI18n")
    private void minusProductCount(){
        String written_count_product = count_product.getText().toString();
        int product_count = Integer.parseInt(written_count_product);

        if (product_count > 0) {
            product_count--;
            count_product.setText(Integer.toString(product_count));
        }

    }


    // +1 Every Time +1 Button Is Clicked
    @SuppressLint("SetTextI18n")
    private void plusProductCount(){
        String written_count_product = count_product.getText().toString();
        int product_count = Integer.parseInt(written_count_product);

        product_count++;
        count_product.setText(Integer.toString(product_count));

    }

    // Save Scanned Product In The Specified Quantity
    @SuppressLint("SetTextI18n")
    private void saveInputedItems(){
        int count_products_to_save = Integer.parseInt(count_product.getText().toString());
        String inserted_bar_code = bar_code.getText().toString();
        System.out.println(inserted_bar_code);;

    }
}
