package com.example;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class App extends JFrame {

    // Modern VS Code Color Palette
    private static final Color COLOR_BACKGROUND = new Color(0x1e, 0x1e, 0x1e);
    private static final Color COLOR_SIDEBAR = new Color(0x25, 0x25, 0x26);
    private static final Color COLOR_ACTIVITY_BAR = new Color(0x33, 0x33, 0x33);
    private static final Color COLOR_STATUS_BAR = new Color(0x00, 0x7a, 0xcc);
    private static final Color COLOR_CARD_BG = new Color(0x2d, 0x2d, 0x2d);
    private static final Color COLOR_CARD_HOVER = new Color(0x3c, 0x3c, 0x3c);
    private static final Color COLOR_TEXT_PRIMARY = new Color(0xee, 0xee, 0xee);
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x85, 0x85, 0x85);
    private static final Color COLOR_LINK = new Color(0x37, 0x94, 0xff);
    private static final Color COLOR_BADGE_BG = new Color(0x00, 0x4b, 0x72);

    public App() {
        setTitle("my-web-app - Visual Studio Code");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main layout container
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BACKGROUND);

        // 1. Build Top Title/Search Bar
        rootPanel.add(buildTitleBar(), BorderLayout.NORTH);

        // 2. Build Left Navigation (Activity Bar + Sidebar)
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(buildActivityBar(), BorderLayout.WEST);
        rootPanel.add(leftContainer, BorderLayout.WEST);

        // 3. Build Center Editor Area (Tab Bar + Welcome Page Content)
        JPanel editorArea = new JPanel(new BorderLayout());
        editorArea.setBackground(COLOR_BACKGROUND);
        editorArea.add(buildTabBar(), BorderLayout.NORTH);
        
        // Welcome content should be scrollable
        JPanel welcomeContainer = buildWelcomeContent();
        JScrollPane scrollPane = new JScrollPane(welcomeContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        editorArea.add(scrollPane, BorderLayout.CENTER);
        
        rootPanel.add(editorArea, BorderLayout.CENTER);

        // 4. Build Bottom Status Bar
        rootPanel.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    // Top Header mimicking VS Code Title/Search bar
    private JPanel buildTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(COLOR_BACKGROUND);
        titleBar.setPreferredSize(new Dimension(1200, 40));
        titleBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0x2d, 0x2d, 0x2d)));

        // Left: Menu
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        menuPanel.setOpaque(false);
        String[] menus = {"File", "Edit", "Selection", "View", "Go", "..."};
        for (String m : menus) {
            JLabel label = new JLabel(m);
            label.setForeground(COLOR_TEXT_SECONDARY);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            menuPanel.add(label);
        }

        // Center: Navigation & Search
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        centerPanel.setOpaque(false);
        
        // Back/Forward arrows
        JLabel backBtn = new JLabel(" 🡨 ");
        backBtn.setForeground(COLOR_TEXT_SECONDARY);
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel forwardBtn = new JLabel(" 🡪 ");
        forwardBtn.setForeground(COLOR_TEXT_SECONDARY);
        forwardBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        centerPanel.add(backBtn);
        centerPanel.add(forwardBtn);

        // Search field representation
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(COLOR_CARD_BG);
        searchBox.setPreferredSize(new Dimension(400, 24));
        searchBox.setBorder(BorderFactory.createLineBorder(new Color(0x3c, 0x3c, 0x3c), 1));
        
        JLabel searchIcon = new JLabel("  🔍  ");
        searchIcon.setForeground(COLOR_TEXT_SECONDARY);
        JLabel searchText = new JLabel("my-web-app");
        searchText.setForeground(COLOR_TEXT_PRIMARY);
        searchText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.add(searchText, BorderLayout.CENTER);
        centerPanel.add(searchBox);

        titleBar.add(menuPanel, BorderLayout.WEST);
        titleBar.add(centerPanel, BorderLayout.CENTER);

        // Right side layout controls (mockup)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        rightPanel.setOpaque(false);
        JLabel layoutBtn = new JLabel(" 🗖 ");
        layoutBtn.setForeground(COLOR_TEXT_SECONDARY);
        rightPanel.add(layoutBtn);
        titleBar.add(rightPanel, BorderLayout.EAST);

        return titleBar;
    }

    // Leftmost strip containing major sections
    private JPanel buildActivityBar() {
        JPanel activityBar = new JPanel(new BorderLayout());
        activityBar.setBackground(COLOR_ACTIVITY_BAR);
        activityBar.setPreferredSize(new Dimension(50, 800));

        // Top group
        JPanel topGroup = new JPanel();
        topGroup.setLayout(new BoxLayout(topGroup, BoxLayout.Y_AXIS));
        topGroup.setOpaque(false);

        String[] topIcons = {"📁", "🔍", "🌿", "▶", "⊞"};
        for (int i = 0; i < topIcons.length; i++) {
            JLabel btn = new JLabel(topIcons[i], SwingConstants.CENTER);
            btn.setPreferredSize(new Dimension(50, 50));
            btn.setMaximumSize(new Dimension(50, 50));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            // Highlight the explorer tab (first item) as active
            if (i == 0) {
                btn.setForeground(Color.WHITE);
                btn.setBackground(COLOR_BACKGROUND);
                btn.setOpaque(true);
            } else {
                btn.setForeground(COLOR_TEXT_SECONDARY);
            }
            topGroup.add(btn);
            topGroup.add(Box.createVerticalStrut(5));
        }

        // Bottom group
        JPanel bottomGroup = new JPanel();
        bottomGroup.setLayout(new BoxLayout(bottomGroup, BoxLayout.Y_AXIS));
        bottomGroup.setOpaque(false);

        String[] bottomIcons = {"👤", "⚙"};
        for (String icon : bottomIcons) {
            JLabel btn = new JLabel(icon, SwingConstants.CENTER);
            btn.setPreferredSize(new Dimension(50, 50));
            btn.setMaximumSize(new Dimension(50, 50));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            btn.setForeground(COLOR_TEXT_SECONDARY);
            bottomGroup.add(btn);
            bottomGroup.add(Box.createVerticalStrut(5));
        }

        activityBar.add(topGroup, BorderLayout.NORTH);
        activityBar.add(bottomGroup, BorderLayout.SOUTH);

        return activityBar;
    }

    // Top editor tabs bar
    private JPanel buildTabBar() {
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(COLOR_SIDEBAR);
        tabBar.setPreferredSize(new Dimension(1200, 35));

        // Tab: Welcome
        JPanel activeTab = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        activeTab.setBackground(COLOR_BACKGROUND);
        activeTab.setBorder(new MatteBorder(1, 0, 0, 0, COLOR_LINK));

        JLabel logo = new JLabel("⚙");
        logo.setForeground(COLOR_LINK);
        JLabel label = new JLabel("Welcome");
        label.setForeground(COLOR_TEXT_PRIMARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel closeBtn = new JLabel(" × ");
        closeBtn.setForeground(COLOR_TEXT_SECONDARY);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        activeTab.add(logo);
        activeTab.add(label);
        activeTab.add(closeBtn);

        tabBar.add(activeTab);
        return tabBar;
    }

    // Welcome Page content layout
    private JPanel buildWelcomeContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0;

        // Big Header Group
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel mainTitle = new JLabel("Visual Studio Code");
        mainTitle.setForeground(COLOR_TEXT_PRIMARY);
        mainTitle.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        
        JLabel subtitle = new JLabel("Editing evolved");
        subtitle.setForeground(COLOR_TEXT_SECONDARY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        headerPanel.add(mainTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitle);
        headerPanel.add(Box.createVerticalStrut(40));

        panel.add(headerPanel, gbc);

        // Columns Panel (Start vs Walkthroughs)
        gbc.gridy = 1;
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 60, 0));
        columnsPanel.setOpaque(false);

        // Left Column (Start & Recent)
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        JLabel startTitle = new JLabel("Start");
        startTitle.setForeground(COLOR_TEXT_PRIMARY);
        startTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftCol.add(startTitle);
        leftCol.add(Box.createVerticalStrut(15));

        leftCol.add(createLinkItem("📄 New File...", "Create a new text, markdown, or code file"));
        leftCol.add(createLinkItem("📁 Open File...", "Open any file from your computer"));
        leftCol.add(createLinkItem("📂 Open Folder...", "Open a directory to work on a project"));
        leftCol.add(createLinkItem("🌿 Clone Git Repository...", "Clone a remote repository from GitHub, etc."));
        leftCol.add(createLinkItem("🔗 Connect to...", "Connect to remote tunnels or containers"));

        leftCol.add(Box.createVerticalStrut(30));

        JLabel recentTitle = new JLabel("Recent");
        recentTitle.setForeground(COLOR_TEXT_PRIMARY);
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftCol.add(recentTitle);
        leftCol.add(Box.createVerticalStrut(15));

        JLabel recentEmpty = new JLabel("You have no recent folders, open a folder to start.");
        recentEmpty.setForeground(COLOR_TEXT_SECONDARY);
        recentEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftCol.add(recentEmpty);

        columnsPanel.add(leftCol);

        // Right Column (Walkthroughs)
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        JLabel walkTitle = new JLabel("Walkthroughs");
        walkTitle.setForeground(COLOR_TEXT_PRIMARY);
        walkTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightCol.add(walkTitle);
        rightCol.add(Box.createVerticalStrut(15));

        rightCol.add(createWalkthroughCard("Get Started with VS Code", "Customize your editor, learn the basics, and start coding", false));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createWalkthroughCard("Learn the Fundamentals", "Get to know the essential editing features", false));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createWalkthroughCard("GitHub Copilot", "Your AI pair programmer, auto-completing code in real-time", true));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createWalkthroughCard("Get Started with Python Development", "Set up your environment, debug and run Python code", true));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createWalkthroughCard("Get Started with Jupyter Notebooks", "Create interactive, visual notebook files", true));
        rightCol.add(Box.createVerticalStrut(10));
        
        JLabel moreLink = new JLabel("More...");
        moreLink.setForeground(COLOR_LINK);
        moreLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moreLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightCol.add(moreLink);

        columnsPanel.add(rightCol);
        panel.add(columnsPanel, gbc);

        // Footer Startup Checkbox
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 40));
        footerPanel.setOpaque(false);
        JCheckBox startupCheck = new JCheckBox("Show welcome page on startup");
        startupCheck.setSelected(true);
        startupCheck.setForeground(COLOR_TEXT_SECONDARY);
        startupCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        startupCheck.setOpaque(false);
        footerPanel.add(startupCheck);

        panel.add(footerPanel, gbc);

        return panel;
    }

    private JPanel createLinkItem(String text, String desc) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(400, 30));
        
        JLabel link = new JLabel(text);
        link.setForeground(COLOR_LINK);
        link.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                link.setText("<html><u>" + text + "</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                link.setText(text);
            }
        });

        item.add(link, BorderLayout.WEST);
        return item;
    }

    private JPanel createWalkthroughCard(String title, String desc, boolean isUpdated) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3c, 0x3c, 0x3c), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(500, 70));
        card.setPreferredSize(new Dimension(400, 60));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Title row (with optional badge)
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleRow.add(titleLabel, BorderLayout.WEST);

        if (isUpdated) {
            JLabel badge = new JLabel(" Updated ");
            badge.setForeground(Color.WHITE);
            badge.setBackground(COLOR_BADGE_BG);
            badge.setOpaque(true);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
            titleRow.add(badge, BorderLayout.EAST);
        }
        card.add(titleRow, gbc);

        // Desc row
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        JLabel descLabel = new JLabel(desc);
        descLabel.setForeground(COLOR_TEXT_SECONDARY);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        card.add(descLabel, gbc);

        // Hover Effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COLOR_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_CARD_BG);
            }
        });

        return card;
    }

    // Bottom status bar
    private JPanel buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(COLOR_STATUS_BAR);
        statusBar.setPreferredSize(new Dimension(1200, 22));

        // Left Panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leftPanel.setOpaque(false);
        
        JLabel branch = new JLabel(" 🌿 main ");
        branch.setForeground(Color.WHITE);
        branch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        leftPanel.add(branch);

        JLabel sync = new JLabel(" 🗘 ");
        sync.setForeground(Color.WHITE);
        leftPanel.add(sync);

        JLabel issues = new JLabel(" ⓧ 0  ⚠ 0 ");
        issues.setForeground(Color.WHITE);
        issues.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        leftPanel.add(issues);

        // Right Panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        rightPanel.setOpaque(false);

        JLabel info = new JLabel("Ln 1, Col 1  |  UTF-8  |  Java");
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rightPanel.add(info);

        JLabel notification = new JLabel(" 🔔 ");
        notification.setForeground(Color.WHITE);
        rightPanel.add(notification);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize FlatLaf Dark theme
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize FlatLaf");
            }
            
            App frame = new App();
            frame.setVisible(true);
        });
    }
}
