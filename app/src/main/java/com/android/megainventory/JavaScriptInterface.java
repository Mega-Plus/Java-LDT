package com.android.megainventory;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    private Context context;

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
