package com.android.megainventory;

import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.List;

public class PROD_LIST_ACT_HTML extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);

        // Sample data with HTML formatted strings
        List<String> htmlStrings = new ArrayList<>();
        htmlStrings.add("<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Colorful Fox</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p><span style=\"color:red; background-color:yellow; border:150px solid transparent;\">fox</span> <span style=\"color:green; background-color:yellow; border:50px solid transparent;\">running</span></p>\n" +
                "</body>\n" +
                "</html>");
        htmlStrings.add("<font color='blue'>Jumping</font> <font color='yellow'>jacks</font>");

        // Create adapter
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

        // Set adapter to ListView
        ListView listView = findViewById(R.id.listView_ORDERS);
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = htmlStrings.get(position); // Get the selected text
                Toast.makeText(PROD_LIST_ACT_HTML.this, "Selected Text: " + selectedText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
