package com.example.thecodicore;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

    // Terminal views
    private View terminalDivider;
    private LinearLayout terminalPanel;
    private TextView terminalText;
    private EditText terminalInput;
    private ScrollView terminalScroll;
    private TextView btnCloseTerminal;

    // Explorer file text views
    private TextView fileMainActivity;
    private TextView fileBuildGradle;

    private WebView webView;
    private int activeTabId = R.id.icon_explorer;
    private int fontSize = 14;

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

        terminalDivider = findViewById(R.id.terminal_divider);
        terminalPanel = findViewById(R.id.terminal_panel);
        terminalText = findViewById(R.id.terminal_text);
        terminalInput = findViewById(R.id.terminal_input);
        terminalScroll = findViewById(R.id.terminal_scroll);
        btnCloseTerminal = findViewById(R.id.btn_close_terminal);

        fileMainActivity = findViewById(R.id.file_main_activity);
        fileBuildGradle = findViewById(R.id.file_build_gradle);

        // 3. Configure Click Listeners for Sidebar Icons
        setupTab(iconExplorer, R.id.icon_explorer, "EXPLORER", contentExplorer);
        setupTab(iconSearch, R.id.icon_search, "SEARCH", contentSearch);
        setupTab(iconSourceControl, R.id.icon_source_control, "SOURCE CONTROL", contentSourceControl);
        setupTab(iconDebug, R.id.icon_debug, "RUN AND DEBUG", contentDebug);
        setupTab(iconExtensions, R.id.icon_extensions, "EXTENSIONS", contentExtensions);
        setupTab(iconAccount, R.id.icon_account, "ACCOUNT", contentAccount);
        setupTab(iconSettings, R.id.icon_settings, "SETTINGS", contentSettings);

        // 4. Configure File Explorer Loading
        fileMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFileIntoEditor("MainActivity.java");
                highlightFileSelection(fileMainActivity);
            }
        });

        fileBuildGradle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFileIntoEditor("build.gradle");
                highlightFileSelection(fileBuildGradle);
            }
        });

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

        // 6. Configure Collapsible Terminal Panel
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

        // Terminal commands processor
        terminalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String cmd = terminalInput.getText().toString().trim();
                    processTerminalCommand(cmd);
                    terminalInput.setText("");
                    return true;
                }
                return false;
            }
        });
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

    private void loadFileIntoEditor(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("mock/" + filename)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            // Escape content safely for JavaScript call
            String escaped = sb.toString()
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");

            webView.evaluateJavascript("window.setEditorValue('" + escaped + "', '" + filename + "');", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlightFileSelection(TextView selected) {
        fileMainActivity.setTextColor(Color.parseColor("#858585"));
        fileBuildGradle.setTextColor(Color.parseColor("#858585"));
        selected.setTextColor(Color.parseColor("#3794ff")); // VS Code Link Highlight Color
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

    private void processTerminalCommand(String cmd) {
        StringBuilder response = new StringBuilder();
        response.append("sh-4.4$ ").append(cmd).append("\n");

        if (cmd.equalsIgnoreCase("help")) {
            response.append("Available commands:\n")
                .append("  help          - Show this guide\n")
                .append("  git status    - Check Git repository changes status\n")
                .append("  java-run      - Compile and run active project\n")
                .append("  clear         - Clear terminal screen\n");
        } else if (cmd.equalsIgnoreCase("git status")) {
            response.append("On branch main\n")
                .append("Your branch is up to date with 'origin/main'.\n\n")
                .append("Changes not staged for commit:\n")
                .append("  (use \"git add <file>...\" to update what will be committed)\n")
                .append("  (use \"git restore <file>...\" to discard changes)\n")
                .append("\tmodified:   app/src/main/java/com/example/thecodicore/MainActivity.java\n\n")
                .append("no changes added to commit (use \"git add\" and/or \"git commit -a\")\n");
        } else if (cmd.equalsIgnoreCase("java-run")) {
            response.append("$ ./gradlew assembleDebug\n")
                .append("Starting a Gradle Daemon, 1 incompatible Daemon could not be reused...\n")
                .append("> Task :app:compileDebugJavaWithJavac SUCCESS\n")
                .append("> Task :app:processDebugResources SUCCESS\n")
                .append("> Task :app:assembleDebug SUCCESS\n\n")
                .append("BUILD SUCCESSFUL in 3s\n")
                .append("Running MainActivity on simulator...\n")
                .append("System.out.println(\"Hello, World!\");\n")
                .append("Thecodicore Application started successfully!\n");
        } else if (cmd.equalsIgnoreCase("clear")) {
            terminalText.setText("sh-4.4$ ");
            return;
        } else if (cmd.isEmpty()) {
            // Just append newline for empty return
        } else {
            response.append("sh: command not found: ").append(cmd).append("\n");
        }

        terminalText.append(response.toString());
        
        // Auto scroll to bottom
        terminalScroll.post(new Runnable() {
            @Override
            public void run() {
                terminalScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    // JavaScript interface to receive calls from Monaco Editor Web App
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
    }
}
