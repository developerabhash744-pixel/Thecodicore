package com.example.thecodicore;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sidebarPanel;
    private TextView sidebarTitle;
    
    // Sidebar content layouts
    private LinearLayout contentExplorer;
    private LinearLayout contentSearch;
    private LinearLayout contentSourceControl;
    private LinearLayout contentDebug;
    private LinearLayout contentExtensions;
    private LinearLayout contentAccount;
    private LinearLayout contentSettings;

    // Sidebar navigation icons
    private ImageView iconExplorer;
    private ImageView iconSearch;
    private ImageView iconSourceControl;
    private ImageView iconDebug;
    private ImageView iconExtensions;
    private ImageView iconAccount;
    private ImageView iconSettings;

    // Status bar views
    private TextView statusCursor;
    private TextView statusLanguage;
    private ImageView statusSave;

    // Terminal views
    private View terminalDivider;
    private LinearLayout terminalPanel;
    private TextView terminalText;
    private EditText terminalInput;
    private ScrollView terminalScroll;
    private TextView btnCloseTerminal;

    // Explorer dynamic file list
    private LinearLayout explorerFileList;

    private WebView webView;
    private int activeTabId = R.id.icon_explorer;
    private int fontSize = 14;

    // Active file system references
    private File workspaceDir;
    private File activeFile;
    private TextView activeFileTextView;

    // Live terminal process
    private Process shellProcess;
    private OutputStream shellOutputStream;
    private InputStream shellInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize WebView for Monaco Editor
        webView = findViewById(R.id.monaco_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadUrl("file:///android_asset/index.html");

        // 2. Initialize UI Views
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

        statusCursor = findViewById(R.id.status_cursor);
        statusLanguage = findViewById(R.id.status_language);
        statusSave = findViewById(R.id.status_save);

        terminalDivider = findViewById(R.id.terminal_divider);
        terminalPanel = findViewById(R.id.terminal_panel);
        terminalText = findViewById(R.id.terminal_text);
        terminalInput = findViewById(R.id.terminal_input);
        terminalScroll = findViewById(R.id.terminal_scroll);
        btnCloseTerminal = findViewById(R.id.btn_close_terminal);

        explorerFileList = findViewById(R.id.explorer_file_list);

        // 3. Configure Click Listeners for Sidebar Icons
        setupTab(iconExplorer, R.id.icon_explorer, "EXPLORER", contentExplorer);
        setupTab(iconSearch, R.id.icon_search, "SEARCH", contentSearch);
        setupTab(iconSourceControl, R.id.icon_source_control, "SOURCE CONTROL", contentSourceControl);
        setupTab(iconDebug, R.id.icon_debug, "RUN AND DEBUG", contentDebug);
        setupTab(iconExtensions, R.id.icon_extensions, "EXTENSIONS", contentExtensions);
        setupTab(iconAccount, R.id.icon_account, "ACCOUNT", contentAccount);
        setupTab(iconSettings, R.id.icon_settings, "SETTINGS", contentSettings);

        // 4. Initialize Local Workspace Files
        workspaceDir = getFilesDir(); // app-private internal storage workspace
        prepareDefaultWorkspaceFiles();
        loadWorkspaceFiles();

        // 5. Configure Settings panel preferences
        CheckBox settingMinimap = findViewById(R.id.setting_minimap);
        settingMinimap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                webView.evaluateJavascript("window.toggleMinimap(" + isChecked + ");", null);
            }
        });

        CheckBox settingAutocomplete = findViewById(R.id.setting_autocomplete);
        settingAutocomplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                webView.evaluateJavascript("window.editor.updateOptions({ suggestOnTriggerCharacters: " + isChecked + " });", null);
            }
        });

        // Theme control bindings
        findViewById(R.id.btn_theme_dark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("window.setTheme('Dark');", null);
            }
        });
        findViewById(R.id.btn_theme_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("window.setTheme('Light');", null);
            }
        });
        findViewById(R.id.btn_theme_hc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("window.setTheme('High Contrast');", null);
            }
        });

        // Font size control bindings
        final TextView txtFontSize = findViewById(R.id.txt_font_size);
        findViewById(R.id.btn_font_dec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontSize > 10) {
                    fontSize -= 2;
                    txtFontSize.setText(String.valueOf(fontSize));
                    webView.evaluateJavascript("window.updateFontSize(" + fontSize + ");", null);
                }
            }
        });
        findViewById(R.id.btn_font_inc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontSize < 30) {
                    fontSize += 2;
                    txtFontSize.setText(String.valueOf(fontSize));
                    webView.evaluateJavascript("window.updateFontSize(" + fontSize + ");", null);
                }
            }
        });

        // 6. Configure Status Bar Save Action
        statusSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerFileSave();
            }
        });
        statusSave.setVisibility(View.GONE); // Hidden initially on Welcome Page

        // 7. Configure Live Shell Terminal
        findViewById(R.id.status_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTerminal();
            }
        });

        btnCloseTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalDivider.setVisibility(View.GONE);
                terminalPanel.setVisibility(View.GONE);
            }
        });

        // Terminal input submit action listener
        terminalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String cmd = terminalInput.getText().toString();
                    submitTerminalCommand(cmd);
                    terminalInput.setText("");
                    return true;
                }
                return false;
            }
        });

        // Start local interactive shell process
        startInteractiveShell();
    }

    private void prepareDefaultWorkspaceFiles() {
        String[] defaults = {"MainActivity.java", "build.gradle", "AndroidManifest.xml"};
        for (String filename : defaults) {
            File f = new File(workspaceDir, filename);
            if (!f.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("mock/" + filename)));
                    FileOutputStream fos = new FileOutputStream(f);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fos.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                    reader.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadWorkspaceFiles() {
        explorerFileList.removeAllViews();
        File[] files = workspaceDir.listFiles();
        if (files == null) return;

        for (final File file : files) {
            if (file.isDirectory()) continue;

            final TextView tv = new TextView(this);
            tv.setText("  📄 " + file.getName());
            tv.setTextColor(Color.parseColor("#858585"));
            tv.setTextSize(12);
            tv.setPadding(0, 16, 0, 16);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setClickable(true);
            tv.setFocusable(true);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFileIntoEditor(file);
                    highlightFileSelection(tv);
                }
            });

            explorerFileList.addView(tv);
        }
    }

    private void loadFileIntoEditor(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            fis.close();

            String escaped = sb.toString()
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");

            activeFile = file;
            webView.evaluateJavascript("window.setEditorValue('" + escaped + "', '" + file.getAbsolutePath() + "');", null);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void highlightFileSelection(TextView selected) {
        if (activeFileTextView != null) {
            activeFileTextView.setTextColor(Color.parseColor("#858585"));
        }
        activeFileTextView = selected;
        activeFileTextView.setTextColor(Color.parseColor("#3794ff"));
    }

    private void clearFileHighlight() {
        if (activeFileTextView != null) {
            activeFileTextView.setTextColor(Color.parseColor("#858585"));
            activeFileTextView = null;
        }
    }

    private void triggerFileSave() {
        if (activeFile == null) {
            Toast.makeText(this, "No active file open to save!", Toast.LENGTH_SHORT).show();
            return;
        }
        webView.evaluateJavascript("window.Android.saveActiveFile(window.editor.getValue());", null);
    }

    private void setupTab(final ImageView tabIcon, final int tabId, final String title, final View contentView) {
        tabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTabId == tabId) {
                    if (sidebarPanel.getVisibility() == View.VISIBLE) {
                        sidebarPanel.setVisibility(View.GONE);
                    } else {
                        sidebarPanel.setVisibility(View.VISIBLE);
                    }
                } else {
                    sidebarPanel.setVisibility(View.VISIBLE);
                    sidebarTitle.setText(title);
                    
                    contentExplorer.setVisibility(View.GONE);
                    contentSearch.setVisibility(View.GONE);
                    contentSourceControl.setVisibility(View.GONE);
                    contentDebug.setVisibility(View.GONE);
                    contentExtensions.setVisibility(View.GONE);
                    contentAccount.setVisibility(View.GONE);
                    contentSettings.setVisibility(View.GONE);
                    
                    contentView.setVisibility(View.VISIBLE);

                    iconExplorer.setBackgroundColor(Color.TRANSPARENT);
                    iconSearch.setBackgroundColor(Color.TRANSPARENT);
                    iconSourceControl.setBackgroundColor(Color.TRANSPARENT);
                    iconDebug.setBackgroundColor(Color.TRANSPARENT);
                    iconExtensions.setBackgroundColor(Color.TRANSPARENT);
                    iconAccount.setBackgroundColor(Color.TRANSPARENT);
                    iconSettings.setBackgroundColor(Color.TRANSPARENT);

                    tabIcon.setBackgroundColor(Color.parseColor("#1e1e1e"));
                    activeTabId = tabId;
                }
            }
        });
    }

    private void toggleTerminal() {
        if (terminalPanel.getVisibility() == View.VISIBLE) {
            terminalDivider.setVisibility(View.GONE);
            terminalPanel.setVisibility(View.GONE);
        } else {
            terminalDivider.setVisibility(View.VISIBLE);
            terminalPanel.setVisibility(View.VISIBLE);
            terminalInput.requestFocus();
        }
    }

    private void startInteractiveShell() {
        try {
            shellProcess = Runtime.getRuntime().exec("/system/bin/sh");
            shellOutputStream = shellProcess.getOutputStream();
            shellInputStream = shellProcess.getInputStream();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[2048];
                    int read;
                    try {
                        while ((read = shellInputStream.read(buffer)) != -1) {
                            final String output = new String(buffer, 0, read, StandardCharsets.UTF_8);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    terminalText.append(output);
                                    terminalScroll.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            terminalScroll.fullScroll(View.FOCUS_DOWN);
                                        }
                                    });
                                }
                            });
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                terminalText.append("\n[Shell disconnected: " + e.getMessage() + "]\n");
                            }
                        });
                    }
                }
            }).start();

            String initCmd = "cd " + workspaceDir.getAbsolutePath() + " && clear\n";
            shellOutputStream.write(initCmd.getBytes(StandardCharsets.UTF_8));
            shellOutputStream.flush();

        } catch (Exception e) {
            terminalText.append("Failed to start shell process: " + e.getMessage() + "\n");
        }
    }

    private void submitTerminalCommand(String cmd) {
        if (shellOutputStream == null) {
            terminalText.append("\nShell process not running.\n");
            return;
        }
        try {
            shellOutputStream.write((cmd + "\n").getBytes(StandardCharsets.UTF_8));
            shellOutputStream.flush();
        } catch (Exception e) {
            terminalText.append("\nError writing to shell: " + e.getMessage() + "\n");
        }
    }

    private void populateRecentFilesListInWebView() {
        File[] files = workspaceDir.listFiles();
        if (files == null) return;

        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (!first) json.append(",");
            json.append("{\"name\":\"").append(file.getName())
                .append("\",\"path\":\"").append(file.getAbsolutePath().replace("\\", "\\\\"))
                .append("\"}");
            first = false;
        }
        json.append("]");

        final String evalCmd = "window.populateRecentFiles('" + json.toString().replace("'", "\\'") + "');";
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(evalCmd, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shellProcess != null) {
            shellProcess.destroy();
        }
    }

    // JavaScript interface bridge class loaded inside Monaco WebView
    private class WebAppInterface {
        @JavascriptInterface
        public void onCursorChanged(final int line, final int column) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusCursor.setText("Ln " + line + ", Col " + column);
                }
            });
        }

        @JavascriptInterface
        public void onLanguageChanged(final String language) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusLanguage.setText("UTF-8   " + language);
                }
            });
        }

        @JavascriptInterface
        public void saveActiveFile(final String content) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (activeFile != null) {
                        try {
                            FileOutputStream fos = new FileOutputStream(activeFile);
                            fos.write(content.getBytes(StandardCharsets.UTF_8));
                            fos.close();
                            Toast.makeText(MainActivity.this, activeFile.getName() + " saved successfully!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void onEditorReady() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateRecentFilesListInWebView();
                }
            });
        }

        @JavascriptInterface
        public void onActiveFileChanged(final String path) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (path == null || path.isEmpty()) {
                        activeFile = null;
                        statusSave.setVisibility(View.GONE);
                        clearFileHighlight();
                    } else {
                        activeFile = new File(path);
                        statusSave.setVisibility(View.VISIBLE);
                        
                        // Find and highlight in the sidebar explorer tree
                        for (int i = 0; i < explorerFileList.getChildCount(); i++) {
                            View child = explorerFileList.getChildAt(i);
                            if (child instanceof TextView) {
                                TextView tv = (TextView) child;
                                if (tv.getText().toString().contains(activeFile.getName())) {
                                    highlightFileSelection(tv);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void onNewFileClicked() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Create a new file programmatically
                    int index = 1;
                    File newFile = new File(workspaceDir, "Untitled-" + index + ".java");
                    while (newFile.exists()) {
                        index++;
                        newFile = new File(workspaceDir, "Untitled-" + index + ".java");
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(newFile);
                        fos.write("// Write your code here...\n".getBytes(StandardCharsets.UTF_8));
                        fos.close();
                        
                        loadWorkspaceFiles();
                        populateRecentFilesListInWebView();
                        loadFileIntoEditor(newFile);
                        
                        Toast.makeText(MainActivity.this, "Created " + newFile.getName(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Creation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void onOpenFileClicked() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Make the Explorer side panel visible and highlight Explorer tab
                    sidebarPanel.setVisibility(View.VISIBLE);
                    sidebarTitle.setText("EXPLORER");
                    
                    contentExplorer.setVisibility(View.VISIBLE);
                    contentSearch.setVisibility(View.GONE);
                    contentSourceControl.setVisibility(View.GONE);
                    contentDebug.setVisibility(View.GONE);
                    contentExtensions.setVisibility(View.GONE);
                    contentAccount.setVisibility(View.GONE);
                    contentSettings.setVisibility(View.GONE);

                    iconExplorer.setBackgroundColor(Color.parseColor("#1e1e1e"));
                    activeTabId = R.id.icon_explorer;
                }
            });
        }

        @JavascriptInterface
        public void openRecentFile(final String path) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(path);
                    if (file.exists()) {
                        loadFileIntoEditor(file);
                        
                        // Set sidebar highlights
                        for (int i = 0; i < explorerFileList.getChildCount(); i++) {
                            View child = explorerFileList.getChildAt(i);
                            if (child instanceof TextView) {
                                TextView tv = (TextView) child;
                                if (tv.getText().toString().contains(file.getName())) {
                                    highlightFileSelection(tv);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void onWalkthroughClicked(final String type) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (type.equals("theme")) {
                        // Open Settings panel and show a Toast guide
                        sidebarPanel.setVisibility(View.VISIBLE);
                        sidebarTitle.setText("SETTINGS");
                        
                        contentExplorer.setVisibility(View.GONE);
                        contentSettings.setVisibility(View.VISIBLE);

                        iconExplorer.setBackgroundColor(Color.TRANSPARENT);
                        iconSettings.setBackgroundColor(Color.parseColor("#1e1e1e"));
                        activeTabId = R.id.icon_settings;

                        Toast.makeText(MainActivity.this, "Use the 'Editor Theme' buttons to switch theme styles!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "VS Code Mobile edition walkthrough: Click status bar to toggle terminal, Ctrl+S to save files, and sidebar to explore!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
