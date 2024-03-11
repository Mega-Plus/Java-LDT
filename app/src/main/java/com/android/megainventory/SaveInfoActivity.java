package com.android.megainventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SaveInfoActivity extends AppCompatActivity {

    private EditText serverIpEditText;
    private EditText serverPassEditText;
    private EditText objectNameEditText;
    private EditText proxyServerIpEditText;
    private Button saveButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_info);

        serverIpEditText = findViewById(R.id.server_ip_edit_text);
        serverPassEditText = findViewById(R.id.server_pass_edit_text);
        objectNameEditText = findViewById(R.id.object_name_edit_text);
        proxyServerIpEditText = findViewById(R.id.proxy_server_ip_edit_text);
        saveButton = findViewById(R.id.save_button);

        sharedPreferences = getSharedPreferences("app_info", Context.MODE_PRIVATE);

        // Check if information exists in cache
        if (sharedPreferences.contains("server_ip"))
        {
            String serverIp = sharedPreferences.getString("server_ip", "");
            String serverPass = sharedPreferences.getString("server_pass", "");
            String objectName = sharedPreferences.getString("object_name", "");
            String proxyServerIp = sharedPreferences.getString("proxy_server_ip", "");

            serverIpEditText.setText(serverIp);
            serverPassEditText.setText(serverPass);
            objectNameEditText.setText(objectName);
            proxyServerIpEditText.setText(proxyServerIp);
        } else {
            // Ask for input
            Toast.makeText(this, "Please provide information", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
            }
        });
    }

    private void saveInformation() {
        // Save information to cache
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("server_ip", serverIpEditText.getText().toString());
        editor.putString("server_pass", serverPassEditText.getText().toString());
        editor.putString("object_name", objectNameEditText.getText().toString());
        editor.putString("proxy_server_ip", proxyServerIpEditText.getText().toString());
        editor.apply();

        Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show();

        recreate();
    }
}
