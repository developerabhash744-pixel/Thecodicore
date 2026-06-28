package com.example.thecodicore;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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

    // Global Search & Replace views
    private EditText searchInput;
    private EditText replaceInput;
    private Button btnSearchSubmit;
    private Button btnReplaceAll;
    private LinearLayout searchResultsContainer;

    // Source Control views
    private EditText gitCommitMessage;
    private Button btnGitCommit;
    private TextView gitStatusInfo;

    // Debug & AI Views
    private Button btnRunCode;
    private TextView aiChatOutput;
    private EditText aiChatInput;
    private Button btnAiSend;

    // Extensions buttons
    private Button btnExtJava;
    private Button btnExtHtml;

    // Account dynamic text
    private TextView accountUsername;
    private TextView accountEmail;

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

        // Search & Replace Views
        searchInput = findViewById(R.id.search_input);
        replaceInput = findViewById(R.id.replace_input);
        btnSearchSubmit = findViewById(R.id.btn_search_submit);
        btnReplaceAll = findViewById(R.id.btn_replace_all);
        searchResultsContainer = findViewById(R.id.search_results_container);

        // Git views
        gitCommitMessage = findViewById(R.id.git_commit_message);
        btnGitCommit = findViewById(R.id.btn_git_commit);
        gitStatusInfo = findViewById(R.id.git_status_info);

        // Debug & AI views
        btnRunCode = findViewById(R.id.btn_run_code);
        aiChatOutput = findViewById(R.id.ai_chat_output);
        aiChatInput = findViewById(R.id.ai_chat_input);
        btnAiSend = findViewById(R.id.btn_ai_send);

        // Extensions views
        btnExtJava = findViewById(R.id.btn_ext_java);
        btnExtHtml = findViewById(R.id.btn_ext_html);

        // Account views
        accountUsername = findViewById(R.id.account_username);
        accountEmail = findViewById(R.id.account_email);

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

        // 7. Command Palette Button click listener
        findViewById(R.id.btn_command_palette).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("window.showCommandPalette();", null);
            }
        });

        // 8. Global Search & Replace Action bindings
        btnSearchSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGlobalSearch();
            }
        });

        btnReplaceAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGlobalReplace();
            }
        });

        // 9. Git Source Control commit action
        btnGitCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGitCommitAndPush();
            }
        });

        // 10. Run Code & Compile action
        btnRunCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compileAndRunActiveFile();
            }
        });

        // 11. AI Send action
        btnAiSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAiAssistantMessage();
            }
        });

        // 12. Extensions installer actions
        setupExtensionButton(btnExtJava, "Java Boilerplate Expansion");
        setupExtensionButton(btnExtHtml, "Web Boilerplate Expand");

        // 13. Configure Live Shell Terminal
        btnCloseTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalDivider.setVisibility(View.GONE);
                terminalPanel.setVisibility(View.GONE);
            }
        });

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

        // Start local interactive shell process and load Account dynamic profile info
        startInteractiveShell();
        loadDynamicProfileDetails();
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

    // Dynamic Global Search Logic
    private void performGlobalSearch() {
        searchResultsContainer.removeAllViews();
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query!", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] files = workspaceDir.listFiles();
        if (files == null) return;

        int totalMatches = 0;

        for (final File file : files) {
            if (file.isDirectory()) continue;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String line;
                int lineNumber = 0;
                boolean fileHeaderAdded = false;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.contains(query)) {
                        totalMatches++;
                        if (!fileHeaderAdded) {
                            TextView header = new TextView(this);
                            header.setText("📄 " + file.getName());
                            header.setTextColor(Color.parseColor("#ffffff"));
                            header.setTextSize(11);
                            header.setPadding(0, 10, 0, 4);
                            searchResultsContainer.addView(header);
                            fileHeaderAdded = true;
                        }

                        final int matchLine = lineNumber;
                        TextView result = new TextView(this);
                        result.setText("    L" + lineNumber + ": " + line.trim());
                        result.setTextColor(Color.parseColor("#858585"));
                        result.setTextSize(11);
                        result.setPadding(0, 4, 0, 4);
                        result.setClickable(true);
                        result.setFocusable(true);
                        
                        result.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadFileIntoEditor(file);
                                webView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.evaluateJavascript(
                                            "window.editor.revealLineInCenter(" + matchLine + "); " +
                                            "window.editor.setPosition({lineNumber: " + matchLine + ", column: 1});", 
                                            null
                                        );
                                    }
                                }, 300);
                            }
                        });

                        searchResultsContainer.addView(result);
                    }
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (totalMatches == 0) {
            TextView noMatches = new TextView(this);
            noMatches.setText("No search matches found.");
            noMatches.setTextColor(Color.parseColor("#858585"));
            noMatches.setTextSize(12);
            searchResultsContainer.addView(noMatches);
        }
    }

    // Dynamic Global Search and Replace Logic
    private void performGlobalReplace() {
        String query = searchInput.getText().toString().trim();
        final String replacement = replaceInput.getText().toString();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query!", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] files = workspaceDir.listFiles();
        if (files == null) return;

        int totalReplacements = 0;

        for (File file : files) {
            if (file.isDirectory()) continue;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                boolean modified = false;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(query)) {
                        line = line.replace(query, replacement);
                        modified = true;
                        totalReplacements++;
                    }
                    sb.append(line).append("\n");
                }
                reader.close();

                if (modified) {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, "Replaced " + totalReplacements + " occurrences across files!", Toast.LENGTH_LONG).show();
        
        loadWorkspaceFiles();
        if (activeFile != null) {
            loadFileIntoEditor(activeFile);
        }
        performGlobalSearch();
    }

    // Git commit & push integration
    private void performGitCommitAndPush() {
        String msg = gitCommitMessage.getText().toString().trim();
        if (msg.isEmpty()) {
            Toast.makeText(this, "Please enter a commit message!", Toast.LENGTH_SHORT).show();
            return;
        }

        terminalDivider.setVisibility(View.VISIBLE);
        terminalPanel.setVisibility(View.VISIBLE);
        terminalInput.requestFocus();

        submitTerminalCommand("git add .");
        submitTerminalCommand("git commit -m \"" + msg + "\"");
        submitTerminalCommand("git push");

        gitCommitMessage.setText("");
        Toast.makeText(this, "Commit & Push commands submitted to Terminal!", Toast.LENGTH_LONG).show();
    }

    private void updateGitStatusSidebar() {
        if (shellOutputStream == null) return;
        gitStatusInfo.setText(
            "▼ Changes\n" +
            "  U app/src/main/assets/index.html\n" +
            "  M app/src/main/java/MainActivity.java\n\n" +
            "Ready to commit changes."
        );
    }

    // Code compilation and run logic inside shell
    private void compileAndRunActiveFile() {
        if (activeFile == null) {
            Toast.makeText(this, "No active file open to run!", Toast.LENGTH_SHORT).show();
            return;
        }

        terminalDivider.setVisibility(View.VISIBLE);
        terminalPanel.setVisibility(View.VISIBLE);
        terminalInput.requestFocus();

        String name = activeFile.getName();
        if (name.endsWith(".java")) {
            submitTerminalCommand("javac " + name);
            submitTerminalCommand("java " + name.substring(0, name.lastIndexOf('.')));
            Toast.makeText(this, "Compiling and executing " + name + "...", Toast.LENGTH_SHORT).show();
        } else if (name.endsWith(".js")) {
            submitTerminalCommand("node " + name);
            Toast.makeText(this, "Executing JavaScript via Node...", Toast.LENGTH_SHORT).show();
        } else {
            submitTerminalCommand("cat " + name);
            Toast.makeText(this, "File read submitted to terminal", Toast.LENGTH_SHORT).show();
        }
    }

    // AI Copilot Code Generator logic
    private void processAiAssistantMessage() {
        String prompt = aiChatInput.getText().toString().trim();
        if (prompt.isEmpty()) return;

        aiChatOutput.append("\n\nYou: " + prompt);
        aiChatInput.setText("");

        String response = "";
        String lowercase = prompt.toLowerCase();

        if (lowercase.contains("java class")) {
            response = "Copilot: Here is your Java class boilerplate:\n\n```java\npublic class Temp {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}\n```";
        } else if (lowercase.contains("for loop") || lowercase.contains("loop")) {
            response = "Copilot: Standard loop:\n\n```java\nfor (int i = 0; i < 10; i++) {\n    // Code here\n}\n```";
        } else if (lowercase.contains("bubble sort") || lowercase.contains("sort")) {
            response = "Copilot: Bubble Sort method:\n\n```java\nvoid bubbleSort(int[] arr) {\n    int n = arr.length;\n    for (int i = 0; i < n-1; i++)\n        for (int j = 0; j < n-i-1; j++)\n            if (arr[j] > arr[j+1]) {\n                int temp = arr[j];\n                arr[j] = arr[j+1];\n                arr[j+1] = temp;\n            }\n}\n```";
        } else {
            response = "Copilot: I have analyzed your workspace files. Let me know if you want me to write code snippets, fix compiling errors, or explain logic.";
        }

        aiChatOutput.append("\n" + response);
    }

    // Extension buttons installer triggers
    private void setupExtensionButton(final Button button, final String extName) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("Install")) {
                    button.setText("Uninstall");
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#424242")));
                    Toast.makeText(MainActivity.this, extName + " installed successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    button.setText("Install");
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#007acc")));
                    Toast.makeText(MainActivity.this, extName + " uninstalled.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Load active profile name from local git or settings config
    private void loadDynamicProfileDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Try executing git config commands to extract profile details
                    Process processName = Runtime.getRuntime().exec("git config user.name");
                    BufferedReader rName = new BufferedReader(new InputStreamReader(processName.getInputStream()));
                    final String name = rName.readLine();
                    rName.close();

                    Process processEmail = Runtime.getRuntime().exec("git config user.email");
                    BufferedReader rEmail = new BufferedReader(new InputStreamReader(processEmail.getInputStream()));
                    final String email = rEmail.readLine();
                    rEmail.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (name != null && !name.trim().isEmpty()) {
                                accountUsername.setText(name);
                            }
                            if (email != null && !email.trim().isEmpty()) {
                                accountEmail.setText(email);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

                    if (tabId == R.id.icon_source_control) {
                        updateGitStatusSidebar();
                    }
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

        @JavascriptInterface
        public void triggerSidebarAction(final String action) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action.equals("toggle")) {
                        if (sidebarPanel.getVisibility() == View.VISIBLE) {
                            sidebarPanel.setVisibility(View.GONE);
                        } else {
                            sidebarPanel.setVisibility(View.VISIBLE);
                        }
                    } else if (action.equals("settings")) {
                        iconSettings.performClick();
                    } else if (action.equals("terminal")) {
                        toggleTerminal();
                    } else if (action.equals("git")) {
                        iconSourceControl.performClick();
                    } else if (action.equals("clearTerm")) {
                        terminalText.setText("sh-4.4$ ");
                    }
                }
            });
        }
    }
}
