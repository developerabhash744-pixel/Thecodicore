package com.example.thecodicore;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.monaco_webview);
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript and storage
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        // Enable local file access for offline assets
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        // Keep navigation inside the webview
        webView.setWebViewClient(new WebViewClient());

        // Load the offline Monaco editor index.html from app assets
        webView.loadUrl("file:///android_asset/index.html");
    }
}
