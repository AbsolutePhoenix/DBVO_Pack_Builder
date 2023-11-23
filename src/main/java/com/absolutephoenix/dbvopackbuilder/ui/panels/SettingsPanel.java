package com.absolutephoenix.dbvopackbuilder.ui.panels;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.absolutephoenix.dbvopackbuilder.utils.LogHelper;
import com.github.kokorin.jaffree.ffprobe.Log;
import net.andrewcpu.elevenlabs.ElevenLabs;
import net.andrewcpu.elevenlabs.model.response.GenerationTypeModel;
import net.andrewcpu.elevenlabs.model.user.Subscription;
import net.andrewcpu.elevenlabs.model.voice.Voice;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The SettingsPanel class represents a settings panel in the user interface.
 * This panel is used to configure various settings related to ElevenLabs voice synthesis,
 * pack building, and FOMOD creation for the DBVO Pack Builder application.
 * It provides a graphical interface for the user to input and save their preferences.
 */
public class SettingsPanel extends JPanel implements ComponentListener {

    // ElevenLabs settings components
    private JPanel elevenLabsPanel = new JPanel();
    private JLabel elevenlabsAPILabel = new JLabel("API Key:");
    private JPasswordField elevenLabsAPIField = new JPasswordField();
    private JLabel elevenlabsVoiceLabel = new JLabel("Voice:");
    private JComboBox<String> elevenLabsVoice = new JComboBox<>();

    private JLabel elevenlabsVoiceModelLabel = new JLabel("Voice Model:");
    private JComboBox<String> elevenLabsVoiceModel = new JComboBox<>();
    private JLabel elevenlabsStabilityLabel = new JLabel("Voice Stability:");
    private JSlider elevenlabsStability = new JSlider(0, 100, 50);
    private JLabel elevenlabsClarityLabel = new JLabel("Voice Clarity:");
    private JSlider elevenlabsClarity = new JSlider(0, 100, 50);
    private JLabel elevenlabsStyleLabel = new JLabel("Voice Emotion (Experimental):");
    private JSlider elevenlabsStyle = new JSlider(0, 100, 50);

    public JButton elevenlabsSave = new JButton("SAVE");

    // Pack settings components
    private JPanel PackPanel = new JPanel();
    private JLabel PackNameLabel = new JLabel("Pack Name:");
    private JTextField PackName = new JTextField();
    private JLabel PackIDLabel = new JLabel("Pack ID:");
    private JTextField PackID = new JTextField();
    private JLabel PackVoiceNameLabel = new JLabel("Voice Name:");
    private JTextField PackVoiceName = new JTextField();
    private JCheckBox useLip = new JCheckBox("Generate Lip Data", true);
    private JCheckBox BuildToBSA = new JCheckBox("Build To BSA", false);
    public JButton PackSave = new JButton("SAVE");

    // FOMOD settings components
    private JPanel fomodPanel = new JPanel();
    private JLabel fomodModNameLabel = new JLabel("Mod Name:");
    private JTextField fomodModName = new JTextField();
    private JLabel fomodModAuthorLabel = new JLabel("Author Name:");
    private JTextField fomodModAuthor = new JTextField();
    private JLabel fomodModVersionLabel = new JLabel("Version:");
    private JTextField fomodModVersion = new JTextField();
    private JLabel fomodNexusPageLabel = new JLabel("Nexus URL:");
    private JTextField fomodNexusPage = new JTextField();
    public JButton fomodSave = new JButton("SAVE");

    private JScrollPane elevenLabsScrollPane;
    private JScrollPane packScrollPane;
    private JScrollPane fomodScrollPane;

    /**
     * Constructs a SettingsPanel object and initializes the UI components and their layout.
     */
    public SettingsPanel(){
        this.setLayout(null);
        this.addComponentListener(this);
        elevenLabsSetup();
        packSetup();
        fomodSetup();
        actions();
    }

    /**
     * Initializes and configures the ElevenLabs settings panel with a GridBagLayout.
     * This method sets up the UI components related to the ElevenLabs API settings,
     * including the input field for the API key, voice selection, and sliders for voice
     * stability and clarity. It also populates the fields with saved settings from
     * the ConfigManager and updates the enabled state of the components based on the user's
     * subscription status.
     */
    public void elevenLabsSetup() {
        // Set layout for the ElevenLabs settings panel and create a titled border.
        elevenLabsPanel.setLayout(new GridBagLayout());

        // GridBag constraints for component layout.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adding components to the panel with constraints for layout.
        elevenLabsPanel.add(elevenlabsAPILabel, gbc);
        elevenLabsPanel.add(elevenLabsAPIField, gbc);
        elevenLabsPanel.add(elevenlabsVoiceLabel, gbc);
        elevenLabsVoice.setEnabled(false); // Disabled initially, enabled based on subscription status.
        elevenLabsPanel.add(elevenLabsVoice, gbc);
        elevenLabsPanel.add(elevenlabsVoiceModelLabel, gbc);
        elevenLabsVoiceModel.setEnabled(false);
        elevenLabsPanel.add(elevenLabsVoiceModel, gbc);
        elevenLabsPanel.add(elevenlabsStabilityLabel, gbc);
        elevenlabsStability.setEnabled(false); // Disabled initially, enabled based on subscription status.
        elevenLabsPanel.add(elevenlabsStability, gbc);
        elevenLabsPanel.add(elevenlabsClarityLabel, gbc);
        elevenlabsClarity.setEnabled(false); // Disabled initially, enabled based on subscription status.
        elevenLabsPanel.add(elevenlabsClarity, gbc);
        elevenLabsPanel.add(elevenlabsStyleLabel, gbc);
        elevenlabsStyle.setEnabled(false); // Disabled initially, enabled based on subscription status.
        elevenLabsPanel.add(elevenlabsStyle, gbc);

        // Load saved ElevenLabs API key from ConfigManager and set it in the API field.
        elevenLabsAPIField.setText(ConfigManager.getSetting().getElevenLabsAPIKey());

        // Update the ElevenLabs API key with the value from the password field.
        ElevenLabs.setApiKey(new String(elevenLabsAPIField.getPassword()));

        // Check the subscription status and enable components accordingly.
        if(ElevenLabs.getUserAPI().getSubscription().getTier() != null) {
            GlobalVariables.subscription = ElevenLabs.getUserAPI().getSubscription();

            // Populate the voice JComboBox with available voices from ElevenLabs.
            List<String> voices = new ArrayList<>();
            for(Voice voice : ElevenLabs.getVoiceAPI().getVoices()) {
                voices.add(voice.getName());
            }
            elevenLabsVoice.setModel(new DefaultComboBoxModel<>(voices.toArray(new String[0])));

            List<String> models = new ArrayList<>();
            for(GenerationTypeModel model :  ElevenLabs.getModelsAPI().getAvailableModels()){
                models.add(model.getName());
            }
            elevenLabsVoiceModel.setModel(new DefaultComboBoxModel<>(models.toArray(new String[0])));

            if(ElevenLabs.getUserAPI().getSubscription().getTier() != null) {
                elevenLabsVoice.setEnabled(true);
                elevenLabsVoice.setSelectedItem(ConfigManager.getSetting().getElevenLabsVoice());
                elevenLabsVoiceModel.setEnabled(true);
                elevenLabsVoiceModel.setSelectedItem(ConfigManager.getSetting().getElevenLabsVoiceModel());
                elevenlabsStability.setEnabled(true);
                elevenlabsStability.setValue(ConfigManager.getSetting().getElevenLabsStability());
                elevenlabsClarity.setEnabled(true);
                elevenlabsClarity.setValue(ConfigManager.getSetting().getElevenLabsClarity());
                elevenlabsStyle.setEnabled(true);
                elevenlabsStyle.setValue(ConfigManager.getSetting().getElevenLabsStyle());
            }
            elevenlabsStabilityLabel.setText("Voice Stabilit: " + elevenlabsStability.getValue());
            elevenlabsClarityLabel.setText("Voice Clarity: " + elevenlabsClarity.getValue());
            elevenlabsStyleLabel.setText("Voice Emotion (Experimental): " + elevenlabsStyle.getValue());

        }

        // Add the ElevenLabs settings panel to the parent container.
        elevenLabsScrollPane = new JScrollPane(elevenLabsPanel);
        elevenLabsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        elevenLabsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        elevenLabsScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(0), "ElevenLabs Settings"));
        add(elevenLabsScrollPane);
        add(elevenlabsSave);
    }

    /**
     * Sets up the panel for pack-related settings within the user interface.
     * This method initializes the pack settings panel with GridBagLayout and adds
     * the components for pack name, pack ID, voice name, and an option to build to BSA.
     * It also populates the fields with the saved settings from the ConfigManager.
     */
    public void packSetup() {
        // Configure the layout and border for the pack settings panel.
        PackPanel.setLayout(new GridBagLayout());

        // Create and configure GridBagConstraints for component placement.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component in its own row.
        gbc.weightx = 1; // Expand horizontally, fill space.
        gbc.weighty = 0; // Do not expand vertically.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally.
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left side.
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components.

        // Add the pack name label and text field to the panel.
        PackPanel.add(PackNameLabel, gbc);
        PackPanel.add(PackName, gbc);

        // Add the pack ID label and text field to the panel.
        PackPanel.add(PackIDLabel, gbc);
        PackPanel.add(PackID, gbc);

        // Add the voice name label and text field to the panel.
        PackPanel.add(PackVoiceNameLabel, gbc);
        PackPanel.add(PackVoiceName, gbc);

        // Add the build to BSA checkbox to the panel.
        PackPanel.add(useLip, gbc);
        PackPanel.add(BuildToBSA, gbc);

        PackPanel.add(new JLabel(" "), gbc);
        PackPanel.add(new JLabel(" "), gbc);

        // Set the text fields with the current configuration settings.
        PackName.setText(ConfigManager.getSetting().getPackName());
        PackID.setText(ConfigManager.getSetting().getPackID());
        PackVoiceName.setText(ConfigManager.getSetting().getPackVoiceName());

        // Check the configuration for the "Build to BSA" setting and set the checkbox accordingly.
        if(ConfigManager.getSetting().getPackBuildToBSA().equals("true")) {
            BuildToBSA.setSelected(true);
        }
        if(ConfigManager.getSetting().getPackGenerateLip().equals("true")) {
            useLip.setSelected(true);
        }

        // Add the complete pack settings panel to the parent container.
        packScrollPane = new JScrollPane(PackPanel);
        packScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        packScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        packScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(0), "Pack Settings"));
        add(packScrollPane);
        add(PackSave);
    }

    /**
     * Initializes and configures the FOMOD settings panel for the application.
     * This method sets up the layout, adds labeled text fields for mod name, author,
     * version, and Nexus page URL, and applies previously saved settings to each field.
     * It also provides a save button for persisting any changes made.
     */
    public void fomodSetup() {
        // Set the panel layout and define the border with a title.
        fomodPanel.setLayout(new GridBagLayout());

        // Initialize GridBagConstraints for component placement within the panel.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Place each component in a new row.
        gbc.weightx = 1; // Allow horizontal expansion to fill space.
        gbc.weighty = 0; // Do not expand vertically.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill the space horizontally.
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left side.
        gbc.insets = new Insets(5, 5, 5, 5); // Set padding around components.

        // Add the mod name label and text field to the panel.
//        fomodModName.setEnabled(false);
//        fomodPanel.add(fomodModNameLabel, gbc);
//        fomodPanel.add(fomodModName, gbc);
//
//        // Add the mod author label and text field to the panel.
//        fomodModAuthor.setEnabled(false);
//        fomodPanel.add(fomodModAuthorLabel, gbc);
//        fomodPanel.add(fomodModAuthor, gbc);
//
//        // Add the mod version label and text field to the panel.
//        fomodModVersion.setEnabled(false);
//        fomodPanel.add(fomodModVersionLabel, gbc);
//        fomodPanel.add(fomodModVersion, gbc);
//
//        // Add the Nexus page URL label and text field to the panel.
//        fomodNexusPage.setEnabled(false);
//        fomodPanel.add(fomodNexusPageLabel, gbc);
//        fomodPanel.add(fomodNexusPage, gbc);
        Font font = new Font("IMPACT", Font.PLAIN, 28);
        JLabel comingSoon = new JLabel("FOMOD GENERATION COMING SOON");
        comingSoon.setFont(font);
        fomodPanel.add(comingSoon, gbc);
//        fomodPanel.add(new JLabel(" "), gbc);


        // Populate text fields with saved settings from the configuration manager.
        fomodModName.setText(ConfigManager.getSetting().getFomodModName());
        fomodModAuthor.setText(ConfigManager.getSetting().getFomodAuthorName());
        fomodModVersion.setText(ConfigManager.getSetting().getFomodVersion());
        fomodNexusPage.setText(ConfigManager.getSetting().getFomodNexusURL());

        // Add the complete FOMOD settings panel to the parent container.
        fomodScrollPane = new JScrollPane(fomodPanel);
        fomodScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fomodScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fomodScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(0), "FOMOD Settings"));

        add(fomodScrollPane);
        fomodSave.setEnabled(false);
        add(fomodSave);
    }


    /**
     * Configures actions for UI components. This method defines and attaches action listeners
     * to the save buttons for each settings section, handling user input and saving settings.
     */
    public void actions(){
        elevenlabsSave.addActionListener(e -> {
            ConfigManager.getSetting().setElevenLabsAPIKey(new String(elevenLabsAPIField.getPassword()));
            try {
                ConfigManager.getSetting().setElevenLabsVoice(elevenLabsVoice.getSelectedItem().toString());
            }catch (NullPointerException a){
                ConfigManager.getSetting().setElevenLabsVoice("");
            }
            try {
                ConfigManager.getSetting().setElevenLabsVoiceModel(elevenLabsVoiceModel.getSelectedItem().toString());
            }catch (NullPointerException a){
                ConfigManager.getSetting().setElevenLabsVoiceModel("");
            }
            for(Voice voice : ElevenLabs.getVoiceAPI().getVoices()) {
                if(voice.getName().equals(Objects.requireNonNull(elevenLabsVoice.getSelectedItem()).toString()))
                    ConfigManager.getSetting().setElevenLabsVoiceID(voice.getVoiceId());
            }

            for(GenerationTypeModel model : ElevenLabs.getModelsAPI().getAvailableModels()) {
                if(model.getName().equals(Objects.requireNonNull(elevenLabsVoiceModel.getSelectedItem()).toString()))
                    ConfigManager.getSetting().setElevenLabsVoiceModelID(model.getModelId());
            }

            ConfigManager.getSetting().setElevenLabsStability(elevenlabsStability.getValue());
            ConfigManager.getSetting().setElevenLabsClarity(elevenlabsClarity.getValue());
            ConfigManager.getSetting().setElevenLabsStyle(elevenlabsStyle.getValue());

            ConfigManager.getSetting().saveProperties(false);
            LogHelper.info("ElevenLabs Settings have been saved.");
        });

        elevenLabsAPIField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                handleChanges();
            }
            public void removeUpdate(DocumentEvent e) {
                handleChanges();

            }
            public void changedUpdate(DocumentEvent e) {

            }

            public void handleChanges(){
                if(new String(elevenLabsAPIField.getPassword()).length() == 32) {
                    ElevenLabs.setApiKey(new String(elevenLabsAPIField.getPassword()));
                    if (ElevenLabs.getUserAPI().getSubscription().getTier() != null) {
                        GlobalVariables.subscription = ElevenLabs.getUserAPI().getSubscription();

                        List<String> voices = new ArrayList<String>();
                        for (Voice voice : ElevenLabs.getVoiceAPI().getVoices()) {
                            voices.add(voice.getName());
                        }

                        List<String> models = new ArrayList<String>();
                        for (GenerationTypeModel model : ElevenLabs.getModelsAPI().getAvailableModels()) {
                            models.add(model.getName());
                        }
                        elevenLabsVoice.setEnabled(true);
                        elevenLabsVoice.setModel(new DefaultComboBoxModel<>(voices.toArray(new String[0])));
                        elevenLabsVoiceModel.setEnabled(true);
                        elevenLabsVoiceModel.setModel(new DefaultComboBoxModel<>(models.toArray(new String[0])));
                        elevenlabsStability.setEnabled(true);
                        elevenlabsClarity.setEnabled(true);
                        elevenlabsStyle.setEnabled(true);
                        LogHelper.info("Found Eleven Labs Subscription. Enabling Options");

                        try {
                            MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.subscription.getCharacterCount() + "/" + GlobalVariables.subscription.getCharacterLimit());
                        }catch (NullPointerException ignore){
                            MainWindow.instance.setTitle("DBVO Pack Builder");

                        }
                    } else {
                        elevenLabsVoice.setEnabled(false);
                        elevenLabsVoice.setModel(new DefaultComboBoxModel<>());
                        elevenLabsVoiceModel.setEnabled(false);
                        elevenLabsVoiceModel.setModel(new DefaultComboBoxModel<>());
                        elevenlabsStability.setEnabled(false);
                        elevenlabsStability.setValue(50);
                        elevenlabsStyle.setEnabled(false);
                        elevenlabsStyle.setValue(0);
                        elevenlabsClarity.setEnabled(false);
                        elevenlabsClarity.setValue(50);
                        MainWindow.instance.setTitle("DBVO Pack Builder");
                    }
                }else{
                    elevenLabsVoice.setEnabled(false);
                    elevenLabsVoice.setModel(new DefaultComboBoxModel<>());
                    elevenLabsVoiceModel.setEnabled(false);
                    elevenLabsVoiceModel.setModel(new DefaultComboBoxModel<>());
                    elevenlabsStability.setEnabled(false);
                    elevenlabsStability.setValue(50);
                    elevenlabsStyle.setEnabled(false);
                    elevenlabsStyle.setValue(0);
                    elevenlabsClarity.setEnabled(false);
                    elevenlabsClarity.setValue(50);
                    MainWindow.instance.setTitle("DBVO Pack Builder");
                }
            }
        });

        PackSave.addActionListener(e -> {
            ConfigManager.getSetting().setPackName(PackName.getText());
            ConfigManager.getSetting().setPackID(PackID.getText());
            ConfigManager.getSetting().setPackVoiceName(PackVoiceName.getText());
            if(useLip.isSelected()) ConfigManager.getSetting().setPackGenerateLip("true");
            else ConfigManager.getSetting().setPackGenerateLip("false");
            if(BuildToBSA.isSelected()) ConfigManager.getSetting().setPackBuildToBSA("true");
            else ConfigManager.getSetting().setPackBuildToBSA("false");
            ConfigManager.getSetting().saveProperties(false);
            LogHelper.info("Pack Settings have been saved.");

        });

        fomodSave.addActionListener(e -> {
            ConfigManager.getSetting().setFomodModName(fomodModName.getText());
            ConfigManager.getSetting().setFomodAuthorName(fomodModAuthor.getText());
            ConfigManager.getSetting().setFomodVersion(fomodModVersion.getText());
            ConfigManager.getSetting().setFomodNexusURL(fomodNexusPage.getText());
            ConfigManager.getSetting().saveProperties(false);
            LogHelper.info("FOMOD Settings have been saved.");

        });
        elevenlabsStability.addChangeListener(e -> {
            elevenlabsStabilityLabel.setText("Voice Stability: " + elevenlabsStability.getValue());
        });

        elevenlabsClarity.addChangeListener(e -> {
            elevenlabsClarityLabel.setText("Voice Stability: " + elevenlabsClarity.getValue());
        });

        elevenlabsStyle.addChangeListener(e -> {
            elevenlabsStyleLabel.setText("Voice Emotion (Experimental): " + elevenlabsStyle.getValue());
        });

    }

    @Override
    public void componentResized(ComponentEvent e) {
        elevenLabsScrollPane.setBounds(10, 10, getWidth() / 3 - 20, getHeight() - 60);
        elevenlabsSave.setBounds(elevenLabsScrollPane.getX(), elevenLabsScrollPane.getY() + elevenLabsScrollPane.getHeight() + 10, elevenLabsScrollPane.getWidth(), 25);
        packScrollPane.setBounds(elevenLabsScrollPane.getX() + elevenLabsScrollPane.getWidth() + 20, 10, getWidth() / 3 - 20, getHeight() - 60);
        PackSave.setBounds(packScrollPane.getX(), packScrollPane.getY() + packScrollPane.getHeight() + 10, packScrollPane.getWidth(), 25);
        fomodScrollPane.setBounds(packScrollPane.getX() + packScrollPane.getWidth() + 20, 10, getWidth() / 3 - 20, getHeight() - 60);
        fomodSave.setBounds(fomodScrollPane.getX(), fomodScrollPane.getY() + fomodScrollPane.getHeight() + 10, fomodScrollPane.getWidth(), 25);

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
