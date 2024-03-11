package com.android.megainventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    Button go_back;
    Button download_button;
    EditText bar_code;
    Button minus_button;
    Button plus_button;
    TextView count_product;
    ImageButton accept_button;
    ImageButton list_button;
    TextView saved_product_list_count;
    WebView webView;
    LinearLayout mother_layout;
    LinearLayout webview_linear_layout;
    ArrayList<String> products_list = new ArrayList<>();
    boolean is_slided_up = false;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        go_back = findViewById(R.id.go_back_button);
        download_button = findViewById(R.id.download_button_right);
        bar_code = findViewById(R.id.bar_code);
        minus_button = findViewById(R.id.minus_button);
        plus_button = findViewById(R.id.plus_button);
        count_product = findViewById(R.id.count_product);
        accept_button = findViewById(R.id.accept_button);
        list_button = findViewById(R.id.list_button);
        saved_product_list_count = findViewById(R.id.saved_product_list_count);
        webView = findViewById(R.id.web_view);
        mother_layout = findViewById(R.id.root_linear_layout);
        webview_linear_layout = findViewById(R.id.webview_linear_layout);


        count_product.setText(Integer.toString(0));

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showSavedProductsList();
                Intent intent = new Intent(DownloadActivity.this, WebViewActivity.class);
                startActivity(intent);
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
        for (int i = 0; i < count_products_to_save; i++) {
            products_list.add(inserted_bar_code);
        }
        int length = products_list.size();
        if (length < 100) {
            saved_product_list_count.setText(Integer.toString(length));
        }else {
            saved_product_list_count.setText("99+");
        }
    }

    // Call Slide Animation
    // If Webview Is Shown Product List Will Slide Down
    // If Webview Is Not Shown Product List Will Slide Up
    private void toggleSlideAnimation() {
        if (is_slided_up) {
            // Slide Down Animation
            slideDownAnimation();
        } else {
            // Slide Up Animation
            slideUpAnimation();
        }
    }

    // Configure Webview Parameters And Call Slide Up/Down Function
    @SuppressLint("SetJavaScriptEnabled")
    private void showSavedProductsList() {

        // Configure Webview Linear Layout For Slide Up
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int webview_linear_layoutHeight = (int) (screenHeight * 0.75f);
        ViewGroup.LayoutParams layoutParams = webview_linear_layout.getLayoutParams();
        layoutParams.height = webview_linear_layoutHeight;
        webview_linear_layout.setLayoutParams(layoutParams);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(new JavaScriptInterface(this), "AndroidInterface");

        toggleSlideAnimation();

    }

    // Slide Up Saved Product List
    private void slideUpAnimation() {
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(mother_layout, "translationY",
                0, -mother_layout.getHeight() * 0.8f);
        slideUp.setDuration(500);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(webview_linear_layout, "alpha", 1f, 0f);
        fadeOut.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideUp, fadeOut);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                webview_linear_layout.setVisibility(View.VISIBLE);
                is_slided_up = true;
            }
        });

        float height = webView.getHeight();
        Log.d("Position", String.valueOf(height));
    }

    // Slide Down Saved Product List
    private void slideDownAnimation() {
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(mother_layout, "translationY",
                -mother_layout.getHeight() * 0.8f, 0);
        slideDown.setDuration(500);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(webview_linear_layout, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideDown, fadeIn);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                webview_linear_layout.setVisibility(View.GONE);
                is_slided_up = false;
            }
        });

        float height = webView.getHeight();
        Log.d("Position", String.valueOf(height));
    }


}