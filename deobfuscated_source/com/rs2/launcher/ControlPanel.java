/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.launcher;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.launcher.ConsolePrintStream;
import com.rs2.launcher.ControlPanelWindowCloseListener;
import com.rs2.launcher.MapHorizontalScrollListener;
import com.rs2.launcher.MapVerticalScrollListener;
import com.rs2.launcher.WorldMapPanel;
import com.rs2.model.World;
import com.rs2.util.FileUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ControlPanel
extends JFrame
implements ActionListener {
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JPanel serverSettingsPanel;
    private JPanel connectionSettingsPanel;
    private JPanel skillSettingsPanel;
    private JPanel creditsPanel;
    private static JButton startServerButton;
    private static JButton restartServerButton;
    static JButton shutdownServerButton;
    private static JButton sendServerMessageButton;
    private static JButton defaultsButton;
    private static JLabel serverStatusLabel;
    private static JLabel usersOnlineLabel;
    private static JLabel serverNameStatusLabel;
    private static JLabel runtimeLabel;
    private static JTextField serverMessageField;
    private static JTextField serverNameField;
    private static JTextField maxPlayersField;
    private static JTextField xpRateField;
    private static JTextField startPositionField;
    private static JTextField respawnPositionField;
    private static JCheckBox duelingEnabledCheckbox;
    private static JCheckBox adminInteractionsCheckbox;
    private static JCheckBox freeToPlayOnlyContentCheckbox;
    private static JCheckBox itemSpawningCheckbox;
    private static JCheckBox funPkCheckbox;
    private static JCheckBox pkWorldCheckbox;
    private static JRadioButton noLoginRestrictionButton;
    private static JRadioButton p2pLoginRestrictionButton;
    private static JRadioButton modLoginRestrictionButton;
    private static JRadioButton adminLoginRestrictionButton;
    private static JButton setSettingsButton;
    private static JTextField serverPortField;
    private static JTextField clientVersionField;
    private static JCheckBox mysqlEnabledCheckbox;
    private static JTextField dbDriverField;
    private static JTextField dbUrlField;
    private static JTextField dbUserField;
    private static JTextField dbPasswordField;
    private static JCheckBox rsaEnabledCheckbox;
    private static JTextField rsaModulusField;
    private static JTextField rsaPrivateExponentField;
    private static JCheckBox debugModeCheckbox;
    private static JCheckBox hiscoresEnabledCheckbox;
    private static JCheckBox developModeCheckbox;
    private static JCheckBox woodcuttingEnabledCheckbox;
    private static JCheckBox thievingEnabledCheckbox;
    private static JCheckBox smithingEnabledCheckbox;
    private static JCheckBox slayerEnabledCheckbox;
    private static JCheckBox runecraftingEnabledCheckbox;
    private static JCheckBox prayerEnabledCheckbox;
    private static JCheckBox miningEnabledCheckbox;
    private static JCheckBox herbloreEnabledCheckbox;
    private static JCheckBox fletchingEnabledCheckbox;
    private static JCheckBox fishingEnabledCheckbox;
    private static JCheckBox firemakingEnabledCheckbox;
    private static JCheckBox farmingEnabledCheckbox;
    private static JCheckBox craftingEnabledCheckbox;
    private static JCheckBox cookingEnabledCheckbox;
    private static JCheckBox agilityEnabledCheckbox;
    private JCheckBox showPlayerNamesCheckbox;
    private Font controlFont = new Font("Calibri", 0, 12);
    static WorldMapPanel worldMapPanel;
    int mapScale;
    private JFrame worldMapFrame;
    private JPanel worldMapContainerPanel;
    private JScrollPane worldMapScrollPane;
    int mapScrollX;
    int mapScrollY;
    private static String serverStatusHtml;
    private static int displayedMaxPlayers;
    private static int displayedOnlinePlayers;
    private static int displayedModeratorCount;
    private static int displayedAdminCount;
    private static String displayedServerName;
    private static int displayedRuntimeMinutes;
    private static int selectedLoginRestrictionMode;

    static {
        worldMapPanel = new WorldMapPanel();
        serverStatusHtml = "Offline";
        displayedMaxPlayers = 2000;
        displayedOnlinePlayers = 0;
        displayedModeratorCount = 0;
        displayedAdminCount = 0;
        displayedServerName = "Orion";
        displayedRuntimeMinutes = 0;
        selectedLoginRestrictionMode = 0;
    }

    public ControlPanel() {
        Serializable serializable;
        this.mapScale = ControlPanel.worldMapPanel.mapScale;
        this.worldMapFrame = new JFrame();
        try {
            Server.loadConfig();
        }
        catch (Exception iOException) {
            serializable = iOException;
            iOException.printStackTrace();
        }
        this.setTitle("EasyRS2 Control Panel Build #" + ServerSettings.cacheVersion + " [" + ServerSettings.serverVersion + "]");
        ServerSettings.serverEmulatorName = "EasyRS2";
        this.setSize(440, 370);
        this.setResizable(false);
        this.worldMapFrame.setTitle("LIVE World Map");
        this.worldMapFrame.setBounds(100, 100, 415, 400);
        this.worldMapFrame.setResizable(true);
        this.worldMapContainerPanel = new JPanel();
        this.worldMapContainerPanel.setLayout(new BorderLayout());
        this.worldMapContainerPanel.add(worldMapPanel);
        worldMapPanel.setOpaque(false);
        this.worldMapContainerPanel.setBackground(Color.black);
        this.worldMapContainerPanel.setPreferredSize(new Dimension(ControlPanel.worldMapPanel.mapWidthPixels, ControlPanel.worldMapPanel.mapHeightPixels));
        this.worldMapScrollPane = new JScrollPane(this.worldMapContainerPanel, 22, 32);
        this.worldMapScrollPane.setPreferredSize(new Dimension(512, 512));
        serializable = new JMenuBar();
        this.worldMapScrollPane.getHorizontalScrollBar().addAdjustmentListener(new MapHorizontalScrollListener(this));
        this.worldMapScrollPane.getVerticalScrollBar().addAdjustmentListener(new MapVerticalScrollListener(this));
        this.worldMapFrame.getContentPane().add((Component)this.worldMapScrollPane, "Center");
        this.worldMapFrame.getContentPane().add((Component)serializable, "North");
        this.showPlayerNamesCheckbox = new JCheckBox("Show names");
        this.showPlayerNamesCheckbox.setFont(this.controlFont);
        this.showPlayerNamesCheckbox.addActionListener(this);
        ((Container)serializable).add(this.showPlayerNamesCheckbox);
        this.mapScrollX = 3217;
        this.mapScrollY = 3218;
        this.worldMapScrollPane.getViewport().setViewPosition(new Point((this.mapScrollX - 1536 - 64) * this.mapScale, (10432 - this.mapScrollY - 64) * this.mapScale));
        this.showPlayerNamesCheckbox.setSelected(true);
        ControlPanel.worldMapPanel.showPlayerNames = this.showPlayerNamesCheckbox.isSelected();
        worldMapPanel.repaint();
        this.setBackground(Color.gray);
        serializable = new JPanel();
        ((Container)serializable).setLayout(new BorderLayout());
        this.getContentPane().add((Component)serializable);
        Object object = this;
        this.mainPanel = new JPanel();
        ((ControlPanel)object).mainPanel.setLayout(null);
        Object object2 = serverStatusHtml.equals("Online") ? "<font color=green>" : "<font color=red>";
        serverStatusLabel = new JLabel("<html>Server Status: <b>" + (String)object2 + serverStatusHtml);
        serverStatusLabel.setFont(((ControlPanel)object).controlFont);
        serverStatusLabel.setBounds(10, 15, 250, 20);
        ((ControlPanel)object).mainPanel.add(serverStatusLabel);
        usersOnlineLabel = new JLabel("<html>Users Online: " + displayedOnlinePlayers + "/" + displayedMaxPlayers + " <font color=blue>(" + displayedModeratorCount + " Mods, <font color=orange>" + displayedAdminCount + " Admins)");
        usersOnlineLabel.setFont(((ControlPanel)object).controlFont);
        usersOnlineLabel.setBounds(10, 40, 250, 20);
        ((ControlPanel)object).mainPanel.add(usersOnlineLabel);
        object2 = new JLabel("<html>Server Emulator: <font color=blue>EasyRS2");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 65, 250, 20);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        serverNameStatusLabel = new JLabel("<html>Server Name: <font color=#1589FF>" + displayedServerName);
        serverNameStatusLabel.setFont(((ControlPanel)object).controlFont);
        serverNameStatusLabel.setBounds(10, 90, 250, 20);
        ((ControlPanel)object).mainPanel.add(serverNameStatusLabel);
        object2 = new JLabel("Server Message:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 115, 250, 20);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        serverMessageField = new JTextField();
        serverMessageField.setBounds(10, 140, 150, 20);
        ((ControlPanel)object).mainPanel.add(serverMessageField);
        startServerButton = new JButton("Start Server");
        startServerButton.setFont(((ControlPanel)object).controlFont);
        startServerButton.setBounds(280, 15, 125, 21);
        ((ControlPanel)object).mainPanel.add(startServerButton);
        startServerButton.addActionListener((ActionListener)object);
        restartServerButton = new JButton("Restart Server");
        restartServerButton.setFont(((ControlPanel)object).controlFont);
        restartServerButton.setBounds(280, 40, 125, 21);
        ((ControlPanel)object).mainPanel.add(restartServerButton);
        restartServerButton.addActionListener((ActionListener)object);
        restartServerButton.setEnabled(false);
        object2 = displayedRuntimeMinutes / 60 > 0 ? String.valueOf(displayedRuntimeMinutes / 60) + " hours " + displayedRuntimeMinutes % 60 + " mins" : String.valueOf(displayedRuntimeMinutes) + " mins";
        runtimeLabel = new JLabel("<html>Runtime: <font color=#1589FF>" + (String)object2);
        runtimeLabel.setFont(((ControlPanel)object).controlFont);
        runtimeLabel.setBounds(280, 65, 250, 20);
        ((ControlPanel)object).mainPanel.add(runtimeLabel);
        object2 = new JCheckBox("Restart every");
        ((Component)object2).setBounds(259, 90, 95, 21);
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((JCheckBox)object2).addActionListener((ActionListener)object);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        ((Component)object2).setEnabled(false);
        object2 = new JTextField();
        ((Component)object2).setBounds(355, 90, 30, 20);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        object2 = new JLabel("min");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(386, 90, 30, 20);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        shutdownServerButton = new JButton("Shutdown Server");
        shutdownServerButton.setFont(((ControlPanel)object).controlFont);
        shutdownServerButton.setBounds(280, 140, 125, 21);
        ((ControlPanel)object).mainPanel.add(shutdownServerButton);
        shutdownServerButton.addActionListener((ActionListener)object);
        shutdownServerButton.setEnabled(false);
        sendServerMessageButton = new JButton("Send");
        sendServerMessageButton.setFont(((ControlPanel)object).controlFont);
        sendServerMessageButton.setBounds(170, 140, 65, 21);
        ((ControlPanel)object).mainPanel.add(sendServerMessageButton);
        sendServerMessageButton.addActionListener((ActionListener)object);
        sendServerMessageButton.setEnabled(false);
        object2 = new JLabel("Console:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 165, 250, 20);
        ((ControlPanel)object).mainPanel.add((Component)object2);
        object2 = new JTextArea(20, 30);
        JScrollPane jScrollPane = new JScrollPane((Component)object2, 22, 31);
        ((Component)object2).setForeground(new Color(0, 0, 0));
        ((Component)object2).setBackground(new Color(255, 255, 255));
        System.setOut(new ConsolePrintStream((JTextArea)object2, jScrollPane));
        jScrollPane.setBounds(10, 190, 400, 110);
        ((Component)object2).setEnabled(false);
        ((ControlPanel)object).mainPanel.add(jScrollPane);
        object = this;
        this.serverSettingsPanel = new JPanel();
        ((ControlPanel)object).serverSettingsPanel.setLayout(null);
        object2 = new JLabel("Server Name:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 15, 80, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        serverNameField = new JTextField();
        serverNameField.setBounds(95, 15, 155, 20);
        ((ControlPanel)object).serverSettingsPanel.add(serverNameField);
        serverNameField.setEnabled(false);
        object2 = new JLabel("Max Players:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 40, 80, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        maxPlayersField = new JTextField();
        maxPlayersField.setBounds(95, 40, 155, 20);
        ((ControlPanel)object).serverSettingsPanel.add(maxPlayersField);
        maxPlayersField.setEnabled(false);
        object2 = new JLabel("XP Rate:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 65, 80, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        xpRateField = new JTextField();
        xpRateField.setBounds(95, 65, 155, 20);
        ((ControlPanel)object).serverSettingsPanel.add(xpRateField);
        object2 = new JLabel("Starting Pos:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 90, 80, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        startPositionField = new JTextField();
        startPositionField.setBounds(95, 90, 155, 20);
        ((ControlPanel)object).serverSettingsPanel.add(startPositionField);
        startPositionField.setEnabled(false);
        object2 = new JLabel("Respawn Pos:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 115, 80, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        respawnPositionField = new JTextField();
        respawnPositionField.setBounds(95, 115, 155, 20);
        ((ControlPanel)object).serverSettingsPanel.add(respawnPositionField);
        respawnPositionField.setEnabled(false);
        freeToPlayOnlyContentCheckbox = new JCheckBox("F2P only content");
        freeToPlayOnlyContentCheckbox.setBounds(250, 15, 170, 21);
        freeToPlayOnlyContentCheckbox.setFont(((ControlPanel)object).controlFont);
        freeToPlayOnlyContentCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(freeToPlayOnlyContentCheckbox);
        freeToPlayOnlyContentCheckbox.setEnabled(false);
        duelingEnabledCheckbox = new JCheckBox("Enable Dueling");
        duelingEnabledCheckbox.setBounds(250, 40, 170, 21);
        duelingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        duelingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(duelingEnabledCheckbox);
        duelingEnabledCheckbox.setEnabled(false);
        adminInteractionsCheckbox = new JCheckBox("Allow admin interactions");
        adminInteractionsCheckbox.setBounds(250, 65, 170, 21);
        adminInteractionsCheckbox.setFont(((ControlPanel)object).controlFont);
        adminInteractionsCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(adminInteractionsCheckbox);
        adminInteractionsCheckbox.setEnabled(false);
        itemSpawningCheckbox = new JCheckBox("Item spawning");
        itemSpawningCheckbox.setBounds(250, 90, 170, 21);
        itemSpawningCheckbox.setFont(((ControlPanel)object).controlFont);
        itemSpawningCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(itemSpawningCheckbox);
        itemSpawningCheckbox.setEnabled(false);
        funPkCheckbox = new JCheckBox("Fun PK");
        funPkCheckbox.setBounds(250, 115, 170, 21);
        funPkCheckbox.setFont(((ControlPanel)object).controlFont);
        funPkCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(funPkCheckbox);
        funPkCheckbox.setEnabled(false);
        pkWorldCheckbox = new JCheckBox("PK World");
        pkWorldCheckbox.setBounds(250, 140, 170, 21);
        pkWorldCheckbox.setFont(((ControlPanel)object).controlFont);
        pkWorldCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).serverSettingsPanel.add(pkWorldCheckbox);
        pkWorldCheckbox.setEnabled(false);
        object2 = new JLabel("<html><b>Login Restrictions");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(80, 140, 150, 20);
        ((ControlPanel)object).serverSettingsPanel.add((Component)object2);
        noLoginRestrictionButton = new JRadioButton("None");
        noLoginRestrictionButton.setFont(((ControlPanel)object).controlFont);
        noLoginRestrictionButton.setSelected(true);
        noLoginRestrictionButton.setBounds(10, 165, 60, 20);
        ((ControlPanel)object).serverSettingsPanel.add(noLoginRestrictionButton);
        noLoginRestrictionButton.addActionListener((ActionListener)object);
        noLoginRestrictionButton.setEnabled(false);
        p2pLoginRestrictionButton = new JRadioButton("P2P");
        p2pLoginRestrictionButton.setFont(((ControlPanel)object).controlFont);
        p2pLoginRestrictionButton.setBounds(70, 165, 50, 20);
        ((ControlPanel)object).serverSettingsPanel.add(p2pLoginRestrictionButton);
        p2pLoginRestrictionButton.addActionListener((ActionListener)object);
        p2pLoginRestrictionButton.setEnabled(false);
        modLoginRestrictionButton = new JRadioButton("Mod");
        modLoginRestrictionButton.setFont(((ControlPanel)object).controlFont);
        modLoginRestrictionButton.setBounds(120, 165, 50, 20);
        ((ControlPanel)object).serverSettingsPanel.add(modLoginRestrictionButton);
        modLoginRestrictionButton.addActionListener((ActionListener)object);
        modLoginRestrictionButton.setEnabled(false);
        adminLoginRestrictionButton = new JRadioButton("Admin");
        adminLoginRestrictionButton.setFont(((ControlPanel)object).controlFont);
        adminLoginRestrictionButton.setBounds(175, 165, 60, 20);
        ((ControlPanel)object).serverSettingsPanel.add(adminLoginRestrictionButton);
        adminLoginRestrictionButton.addActionListener((ActionListener)object);
        adminLoginRestrictionButton.setEnabled(false);
        object2 = new ButtonGroup();
        ((ButtonGroup)object2).add(noLoginRestrictionButton);
        ((ButtonGroup)object2).add(p2pLoginRestrictionButton);
        ((ButtonGroup)object2).add(modLoginRestrictionButton);
        ((ButtonGroup)object2).add(adminLoginRestrictionButton);
        setSettingsButton = new JButton("Set");
        setSettingsButton.setBounds(145, 280, 60, 20);
        ((ControlPanel)object).serverSettingsPanel.add(setSettingsButton);
        setSettingsButton.addActionListener((ActionListener)object);
        defaultsButton = new JButton("Defaults");
        defaultsButton.setBounds(210, 280, 85, 20);
        ((ControlPanel)object).serverSettingsPanel.add(defaultsButton);
        defaultsButton.addActionListener((ActionListener)object);
        object = this;
        this.connectionSettingsPanel = new JPanel();
        ((ControlPanel)object).connectionSettingsPanel.setLayout(null);
        object2 = new JLabel("Port Number:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 15, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        serverPortField = new JTextField();
        serverPortField.setBounds(95, 15, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(serverPortField);
        object2 = new JLabel("Client Version:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 40, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        clientVersionField = new JTextField();
        clientVersionField.setBounds(95, 40, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(clientVersionField);
        mysqlEnabledCheckbox = new JCheckBox("MySQL Enabled");
        mysqlEnabledCheckbox.setBounds(10, 65, 155, 21);
        mysqlEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        mysqlEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).connectionSettingsPanel.add(mysqlEnabledCheckbox);
        object2 = new JLabel("DB Driver:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 90, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        dbDriverField = new JTextField();
        dbDriverField.setBounds(95, 90, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(dbDriverField);
        object2 = new JLabel("DB URL:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 115, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        dbUrlField = new JTextField();
        dbUrlField.setBounds(95, 115, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(dbUrlField);
        object2 = new JLabel("DB User:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 140, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        dbUserField = new JTextField();
        dbUserField.setBounds(95, 140, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(dbUserField);
        object2 = new JLabel("DB Pass:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 165, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        dbPasswordField = new JTextField();
        dbPasswordField.setBounds(95, 165, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(dbPasswordField);
        hiscoresEnabledCheckbox = new JCheckBox("Hiscores Enabled");
        hiscoresEnabledCheckbox.setBounds(260, 15, 155, 21);
        hiscoresEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        hiscoresEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).connectionSettingsPanel.add(hiscoresEnabledCheckbox);
        rsaEnabledCheckbox = new JCheckBox("RSA Enabled");
        rsaEnabledCheckbox.setBounds(10, 190, 155, 21);
        rsaEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        rsaEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).connectionSettingsPanel.add(rsaEnabledCheckbox);
        object2 = new JLabel("RSA Key1:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 215, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        rsaModulusField = new JTextField();
        rsaModulusField.setBounds(95, 215, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(rsaModulusField);
        object2 = new JLabel("RSA Key2:");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 240, 80, 20);
        ((ControlPanel)object).connectionSettingsPanel.add((Component)object2);
        rsaPrivateExponentField = new JTextField();
        rsaPrivateExponentField.setBounds(95, 240, 155, 20);
        ((ControlPanel)object).connectionSettingsPanel.add(rsaPrivateExponentField);
        debugModeCheckbox = new JCheckBox("Debug Mode");
        debugModeCheckbox.setBounds(260, 40, 155, 21);
        debugModeCheckbox.setFont(((ControlPanel)object).controlFont);
        debugModeCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).connectionSettingsPanel.add(debugModeCheckbox);
        developModeCheckbox = new JCheckBox("Develop Mode");
        developModeCheckbox.setBounds(260, 65, 155, 21);
        developModeCheckbox.setFont(((ControlPanel)object).controlFont);
        developModeCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).connectionSettingsPanel.add(developModeCheckbox);
        object = this;
        this.skillSettingsPanel = new JPanel();
        ((ControlPanel)object).skillSettingsPanel.setLayout(null);
        object2 = new JLabel("<html><b>Enabled");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(10, 15, 80, 20);
        ((ControlPanel)object).skillSettingsPanel.add((Component)object2);
        woodcuttingEnabledCheckbox = new JCheckBox("Woodcutting");
        woodcuttingEnabledCheckbox.setBounds(20, 40, 95, 21);
        woodcuttingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        woodcuttingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(woodcuttingEnabledCheckbox);
        thievingEnabledCheckbox = new JCheckBox("Thieving");
        thievingEnabledCheckbox.setBounds(20, 65, 95, 21);
        thievingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        thievingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(thievingEnabledCheckbox);
        smithingEnabledCheckbox = new JCheckBox("Smithing");
        smithingEnabledCheckbox.setBounds(20, 90, 95, 21);
        smithingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        smithingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(smithingEnabledCheckbox);
        slayerEnabledCheckbox = new JCheckBox("Slayer");
        slayerEnabledCheckbox.setBounds(20, 115, 95, 21);
        slayerEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        slayerEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(slayerEnabledCheckbox);
        runecraftingEnabledCheckbox = new JCheckBox("Runecrafting");
        runecraftingEnabledCheckbox.setBounds(20, 140, 95, 21);
        runecraftingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        runecraftingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(runecraftingEnabledCheckbox);
        prayerEnabledCheckbox = new JCheckBox("Prayer");
        prayerEnabledCheckbox.setBounds(20, 165, 95, 21);
        prayerEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        prayerEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(prayerEnabledCheckbox);
        miningEnabledCheckbox = new JCheckBox("Mining");
        miningEnabledCheckbox.setBounds(20, 190, 95, 21);
        miningEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        miningEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(miningEnabledCheckbox);
        herbloreEnabledCheckbox = new JCheckBox("Herblore");
        herbloreEnabledCheckbox.setBounds(20, 215, 95, 21);
        herbloreEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        herbloreEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(herbloreEnabledCheckbox);
        fletchingEnabledCheckbox = new JCheckBox("Fletching");
        fletchingEnabledCheckbox.setBounds(20, 240, 95, 21);
        fletchingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        fletchingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(fletchingEnabledCheckbox);
        fishingEnabledCheckbox = new JCheckBox("Fishing");
        fishingEnabledCheckbox.setBounds(20, 265, 95, 21);
        fishingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        fishingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(fishingEnabledCheckbox);
        object2 = new JLabel("<html><b>Enabled");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(110, 15, 80, 20);
        ((ControlPanel)object).skillSettingsPanel.add((Component)object2);
        firemakingEnabledCheckbox = new JCheckBox("Firemaking");
        firemakingEnabledCheckbox.setBounds(120, 40, 95, 21);
        firemakingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        firemakingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(firemakingEnabledCheckbox);
        farmingEnabledCheckbox = new JCheckBox("Farming");
        farmingEnabledCheckbox.setBounds(120, 65, 95, 21);
        farmingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        farmingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(farmingEnabledCheckbox);
        craftingEnabledCheckbox = new JCheckBox("Crafting");
        craftingEnabledCheckbox.setBounds(120, 90, 95, 21);
        craftingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        craftingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(craftingEnabledCheckbox);
        cookingEnabledCheckbox = new JCheckBox("Cooking");
        cookingEnabledCheckbox.setBounds(120, 115, 95, 21);
        cookingEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        cookingEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(cookingEnabledCheckbox);
        agilityEnabledCheckbox = new JCheckBox("Agility");
        agilityEnabledCheckbox.setBounds(120, 140, 95, 21);
        agilityEnabledCheckbox.setFont(((ControlPanel)object).controlFont);
        agilityEnabledCheckbox.addActionListener((ActionListener)object);
        ((ControlPanel)object).skillSettingsPanel.add(agilityEnabledCheckbox);
        object = this;
        this.creditsPanel = new JPanel();
        ((ControlPanel)object).creditsPanel.setLayout(null);
        object2 = new JLabel("<html><b>EasyRS2</b> was created by <b><font color=blue>Mige</b>");
        ((Component)object2).setFont(((ControlPanel)object).controlFont);
        ((Component)object2).setBounds(110, 15, 200, 20);
        ((ControlPanel)object).creditsPanel.add((Component)object2);
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab("Main", this.mainPanel);
        this.tabbedPane.addTab("Credits", this.creditsPanel);
        ((Container)serializable).add(this.tabbedPane, "Center");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new ControlPanelWindowCloseListener(this));
        this.worldMapFrame.setLocation(this.getX() + this.getWidth() + 5, this.getY());
        if (FileUtil.exists("data/settings.dat")) {
            try {
                object = new DataInputStream(new BufferedInputStream(new FileInputStream("data/settings.dat")));
                displayedServerName = ((DataInputStream)object).readUTF();
                displayedMaxPlayers = ((DataInputStream)object).readUnsignedShort();
                ServerSettings.serverName = displayedServerName;
                ServerSettings.maxPlayers = displayedMaxPlayers;
                serverNameField.setText(displayedServerName);
                maxPlayersField.setText("" + displayedMaxPlayers);
                xpRateField.setText("" + ((DataInputStream)object).readDouble());
                startPositionField.setText(((DataInputStream)object).readUTF());
                respawnPositionField.setText(((DataInputStream)object).readUTF());
                freeToPlayOnlyContentCheckbox.setSelected(((DataInputStream)object).readBoolean());
                duelingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                adminInteractionsCheckbox.setSelected(((DataInputStream)object).readBoolean());
                itemSpawningCheckbox.setSelected(((DataInputStream)object).readBoolean());
                funPkCheckbox.setSelected(((DataInputStream)object).readBoolean());
                pkWorldCheckbox.setSelected(((DataInputStream)object).readBoolean());
                byte by = ((DataInputStream)object).readByte();
                selectedLoginRestrictionMode = by;
                if (by == 0) {
                    noLoginRestrictionButton.setSelected(true);
                }
                if (selectedLoginRestrictionMode == 1) {
                    p2pLoginRestrictionButton.setSelected(true);
                }
                if (selectedLoginRestrictionMode == 2) {
                    modLoginRestrictionButton.setSelected(true);
                }
                if (selectedLoginRestrictionMode == 3) {
                    adminLoginRestrictionButton.setSelected(true);
                }
                serverPortField.setText("" + ((DataInputStream)object).readInt());
                clientVersionField.setText("" + ((DataInputStream)object).readShort());
                mysqlEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                dbDriverField.setText(((DataInputStream)object).readUTF());
                dbUrlField.setText(((DataInputStream)object).readUTF());
                dbUserField.setText(((DataInputStream)object).readUTF());
                dbPasswordField.setText(((DataInputStream)object).readUTF());
                rsaEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                rsaModulusField.setText(((DataInputStream)object).readUTF());
                rsaPrivateExponentField.setText(((DataInputStream)object).readUTF());
                hiscoresEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                debugModeCheckbox.setSelected(((DataInputStream)object).readBoolean());
                developModeCheckbox.setSelected(((DataInputStream)object).readBoolean());
                woodcuttingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                thievingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                smithingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                slayerEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                runecraftingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                prayerEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                miningEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                herbloreEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                fletchingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                fishingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                firemakingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                farmingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                craftingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                cookingEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                agilityEnabledCheckbox.setSelected(((DataInputStream)object).readBoolean());
                ((FilterInputStream)object).close();
                object2 = serverNameField.getText();
                ServerSettings.serverName = (String)object2;
                object2 = maxPlayersField.getText();
                ServerSettings.maxPlayers = Integer.parseInt((String)object2);
                xpRateField.getText();
                String[] startPositionParts = startPositionField.getText().split(",");
                ServerSettings.startX = Integer.parseInt(startPositionParts[0]);
                ServerSettings.startY = Integer.parseInt(startPositionParts[1]);
                ServerSettings.startPlane = Integer.parseInt(startPositionParts[2]);
                String[] respawnPositionParts = respawnPositionField.getText().split(",");
                ServerSettings.respawnX = Integer.parseInt(respawnPositionParts[0]);
                ServerSettings.respawnY = Integer.parseInt(respawnPositionParts[1]);
                ServerSettings.respawnPlane = Integer.parseInt(respawnPositionParts[2]);
                ServerSettings.duelingDisabled = !duelingEnabledCheckbox.isSelected();
                ServerSettings.adminInteractionsAllowed = adminInteractionsCheckbox.isSelected();
                ServerSettings.itemSpawningEnabled = itemSpawningCheckbox.isSelected();
                ServerSettings.funPkEnabled = funPkCheckbox.isSelected();
                ServerSettings.pkWorldEnabled = pkWorldCheckbox.isSelected();
                ServerSettings.loginRestrictionMode = selectedLoginRestrictionMode;
                ControlPanel.refreshStatusDisplay();
                object2 = serverPortField.getText();
                ServerSettings.serverPort = Integer.parseInt((String)object2);
                object2 = clientVersionField.getText();
                ServerSettings.clientBuild = Integer.parseInt((String)object2);
                ServerSettings.clientBuild = ServerSettings.cacheVersion;
                ServerSettings.mysqlPlayerSaveEnabled = mysqlEnabledCheckbox.isSelected();
                if (mysqlEnabledCheckbox.isSelected()) {
                    object2 = dbDriverField.getText();
                    ServerSettings.mysqlDriverClass = (String)object2;
                    object2 = dbUrlField.getText();
                    ServerSettings.mysqlJdbcUrl = (String)object2;
                    object2 = dbUserField.getText();
                    ServerSettings.mysqlUsername = (String)object2;
                    object2 = dbPasswordField.getText();
                    ServerSettings.mysqlPassword = (String)object2;
                }
                ServerSettings.rsaEnabled = rsaEnabledCheckbox.isSelected();
                if (rsaEnabledCheckbox.isSelected()) {
                    object2 = rsaModulusField.getText();
                    ServerSettings.rsaModulusString = (String)object2;
                    object2 = rsaPrivateExponentField.getText();
                    ServerSettings.rsaPrivateExponentString = (String)object2;
                    ServerSettings.rsaModulus = new BigInteger(ServerSettings.rsaModulusString);
                    ServerSettings.rsaPrivateExponent = new BigInteger(ServerSettings.rsaPrivateExponentString);
                }
                ServerSettings.controlPanelHiscoresEnabled = hiscoresEnabledCheckbox.isSelected();
                ServerSettings.debugModeEnabled = debugModeCheckbox.isSelected();
                ServerSettings.developModeEnabled = developModeCheckbox.isSelected();
                ControlPanel.refreshStatusDisplay();
                ServerSettings.woodcuttingEnabled = woodcuttingEnabledCheckbox.isSelected();
                ServerSettings.thievingEnabled = thievingEnabledCheckbox.isSelected();
                ServerSettings.smithingEnabled = smithingEnabledCheckbox.isSelected();
                ServerSettings.slayerEnabled = slayerEnabledCheckbox.isSelected();
                ServerSettings.runecraftingEnabled = runecraftingEnabledCheckbox.isSelected();
                ServerSettings.prayerEnabled = prayerEnabledCheckbox.isSelected();
                ServerSettings.miningEnabled = miningEnabledCheckbox.isSelected();
                ServerSettings.herbloreEnabled = herbloreEnabledCheckbox.isSelected();
                ServerSettings.fletchingEnabled = fletchingEnabledCheckbox.isSelected();
                ServerSettings.fishingEnabled = fishingEnabledCheckbox.isSelected();
                ServerSettings.firemakingEnabled = firemakingEnabledCheckbox.isSelected();
                ServerSettings.farmingEnabled = farmingEnabledCheckbox.isSelected();
                ServerSettings.craftingEnabled = craftingEnabledCheckbox.isSelected();
                ServerSettings.cookingEnabled = cookingEnabledCheckbox.isSelected();
                ServerSettings.agilityEnabled = agilityEnabledCheckbox.isSelected();
                ControlPanel.refreshStatusDisplay();
                ControlPanel.refreshStatusDisplay();
            }
            catch (Exception exception) {}
        }
        this.worldMapFrame.setVisible(true);
    }

    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.setVisible(true);
        String classResourceName = controlPanel.getClass().getName().replace('.', '/');
        String classResourcePath = controlPanel.getClass().getResource("/" + classResourceName + ".class").toString();
        String launcherJarPath = null;
        if (classResourcePath.startsWith("jar:")) {
            String[] pathParts = classResourcePath.split("/");
            int pathPartCount = pathParts.length;
            int n = 0;
            while (n < pathPartCount) {
                String pathPart = pathParts[n];
                if (pathPart.contains("!")) {
                    launcherJarPath = pathPart.substring(0, pathPart.length() - 1);
                    break;
                }
                ++n;
            }
        }
        ServerSettings.launcherJarPath = launcherJarPath;
    }

    public static void refreshStatusDisplay() {
        if (startServerButton == null || restartServerButton == null || shutdownServerButton == null || sendServerMessageButton == null || serverStatusLabel == null || usersOnlineLabel == null || serverNameStatusLabel == null || runtimeLabel == null) {
            return;
        }
        if (Server.serverStatus == 2) {
            serverStatusHtml = "<font color=green>Online";
            startServerButton.setEnabled(false);
            shutdownServerButton.setEnabled(true);
        }
        if (Server.serverStatus == 1) {
            serverStatusHtml = "<font color=black>Starting up...";
            startServerButton.setEnabled(false);
            shutdownServerButton.setEnabled(true);
            sendServerMessageButton.setEnabled(false);
        }
        if (Server.serverStatus == 3) {
            serverStatusHtml = "<font color=black>Shutting down...";
            startServerButton.setEnabled(false);
            restartServerButton.setEnabled(false);
            shutdownServerButton.setEnabled(false);
        }
        if (Server.serverStatus == 0) {
            serverStatusHtml = "<font color=red>Offline";
            startServerButton.setEnabled(true);
            restartServerButton.setEnabled(false);
            shutdownServerButton.setEnabled(false);
            sendServerMessageButton.setEnabled(false);
        }
        serverStatusLabel.setText("<html>Server Status: <b>" + serverStatusHtml);
        displayedRuntimeMinutes = Server.runtimeMinutes;
        displayedServerName = ServerSettings.serverName;
        serverNameStatusLabel.setText("<html>Server Name: <font color=#1589FF>" + displayedServerName);
        String string = displayedRuntimeMinutes / 60 > 0 ? String.valueOf(displayedRuntimeMinutes / 60) + " hours " + displayedRuntimeMinutes % 60 + " mins" : String.valueOf(displayedRuntimeMinutes) + " mins";
        runtimeLabel.setText("<html>Runtime: <font color=#1589FF>" + string);
        displayedOnlinePlayers = Server.onlinePlayerCount;
        displayedMaxPlayers = ServerSettings.maxPlayers;
        displayedModeratorCount = Server.moderatorPlayerCount;
        displayedAdminCount = Server.adminPlayerCount;
        usersOnlineLabel.setText("<html>Users Online: " + displayedOnlinePlayers + "/" + displayedMaxPlayers + " <font color=blue>(" + displayedModeratorCount + " Mods, <font color=orange>" + displayedAdminCount + " Admins)");
        worldMapPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "Start Server") {
            Server.main(new String[]{""});
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Shutdown Server") {
            if (World.getPlayerCount() > 0 && World.getNonBotPlayerCount() == 0) {
                World.logoutBotsAndScheduleShutdown(true);
            } else {
                Server.scheduleShutdown(false);
            }
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Send") {
            String serverMessage = serverMessageField.getText();
            Server.broadcastServerMessage(serverMessage);
        }
        if (actionEvent.getActionCommand() == "Map") {
            this.worldMapFrame.setVisible(true);
        }
        if (actionEvent.getActionCommand() == "Show names") {
            ControlPanel.worldMapPanel.showPlayerNames = this.showPlayerNamesCheckbox.isSelected();
            worldMapPanel.repaint();
        }
    }

    static /* synthetic */ JTabbedPane getTabbedPane(ControlPanel controlPanel) {
        return controlPanel.tabbedPane;
    }
}

