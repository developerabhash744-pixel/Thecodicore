package com.example.thecodicore;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sidebarPanel;
    private TextView sidebarTitle;
    
    // Sidebar content views
    private LinearLayout contentExplorer;
    private LinearLayout contentSearch;
    private LinearLayout contentSourceControl;
    private LinearLayout contentDebug;
    private LinearLayout contentExtensions;
    private LinearLayout contentAccount;
    private LinearLayout contentSettings;

    // Sidebar icons
    private ImageView iconExplorer;
    private ImageView iconSearch;
    private ImageView iconSourceControl;
    private ImageView iconDebug;
    private ImageView iconExtensions;
    private ImageView iconAccount;
    private ImageView iconSettings;

    private int activeTabId = R.id.icon_explorer; // Explorer is default active

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize WebView for Monaco Editor
        WebView webView = findViewById(R.id.monaco_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");

        // 2. Initialize Sidebar views
        sidebarPanel = findViewById(R.id.sidebar_panel);
        sidebarTitle = findViewById(R.id.sidebar_title);

        contentExplorer = findViewById(R.id.content_explorer);
        contentSearch = findViewById(R.id.content_search);
        contentSourceControl = findViewById(R.id.content_source_control);
        contentDebug = findViewById(R.id.content_debug);
        contentExtensions = findViewById(R.id.content_extensions);
        contentAccount = findViewById(R.id.content_account);
        contentSettings = findViewById(R.id.content_settings);

        iconExplorer = findViewById(R.id.icon_explorer);
        iconSearch = findViewById(R.id.icon_search);
        iconSourceControl = findViewById(R.id.icon_source_control);
        iconDebug = findViewById(R.id.icon_debug);
        iconExtensions = findViewById(R.id.icon_extensions);
        iconAccount = findViewById(R.id.icon_account);
        iconSettings = findViewById(R.id.icon_settings);

        // 3. Configure Click Listeners for Sidebar Icons
        setupTab(iconExplorer, R.id.icon_explorer, "EXPLORER", contentExplorer);
        setupTab(iconSearch, R.id.icon_search, "SEARCH", contentSearch);
        setupTab(iconSourceControl, R.id.icon_source_control, "SOURCE CONTROL", contentSourceControl);
        setupTab(iconDebug, R.id.icon_debug, "RUN AND DEBUG", contentDebug);
        setupTab(iconExtensions, R.id.icon_extensions, "EXTENSIONS", contentExtensions);
        setupTab(iconAccount, R.id.icon_account, "ACCOUNT", contentAccount);
        setupTab(iconSettings, R.id.icon_settings, "SETTINGS", contentSettings);
    }

    private void setupTab(final ImageView tabIcon, final int tabId, final String title, final View contentView) {
        tabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTabId == tabId) {
                    // Clicked the active tab -> Toggle panel visibility
                    if (sidebarPanel.getVisibility() == View.VISIBLE) {
                        sidebarPanel.setVisibility(View.GONE);
                    } else {
                        sidebarPanel.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Clicked a different tab -> Show panel, switch content, update highlights
                    sidebarPanel.setVisibility(View.VISIBLE);
                    sidebarTitle.setText(title);
                    
                    // Hide all content layouts
                    contentExplorer.setVisibility(View.GONE);
                    contentSearch.setVisibility(View.GONE);
                    contentSourceControl.setVisibility(View.GONE);
                    contentDebug.setVisibility(View.GONE);
                    contentExtensions.setVisibility(View.GONE);
                    contentAccount.setVisibility(View.GONE);
                    contentSettings.setVisibility(View.GONE);
                    
                    // Show target content layout
                    contentView.setVisibility(View.VISIBLE);

                    // Reset all tab backgrounds (transparent)
                    iconExplorer.setBackgroundColor(Color.TRANSPARENT);
                    iconSearch.setBackgroundColor(Color.TRANSPARENT);
                    iconSourceControl.setBackgroundColor(Color.TRANSPARENT);
                    iconDebug.setBackgroundColor(Color.TRANSPARENT);
                    iconExtensions.setBackgroundColor(Color.TRANSPARENT);
                    iconAccount.setBackgroundColor(Color.TRANSPARENT);
                    iconSettings.setBackgroundColor(Color.TRANSPARENT);

                    // Highlight active tab with editor background color
                    tabIcon.setBackgroundColor(Color.parseColor("#1e1e1e"));

                    activeTabId = tabId;
                }
            }
        });
    }
}
