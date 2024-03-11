package com.android.megainventory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity {

    public static String Dialog_Title="";
    public static String Dialog_Message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity);

        // Get references to views
        TextView titleTextView = findViewById(R.id.dialog_title);
        TextView messageTextView = findViewById(R.id.dialog_message);
        Button okButton = findViewById(R.id.ok_button);

        // Set title and message
        titleTextView.setText(Dialog_Title);
        messageTextView.setText(Dialog_Message);

        // Set click listener for OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity when the "OK" button is clicked
                finish();
            }
        });

        // Prevent dialog from being canceled by pressing back button
        setFinishOnTouchOutside(false);
    }
}
