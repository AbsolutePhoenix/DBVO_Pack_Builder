package com.absolutephoenix.dbvopackbuilder.ui.panels;

import com.absolutephoenix.dbvopackbuilder.config.ConfigManager;
import com.absolutephoenix.dbvopackbuilder.handlers.TopicHandler;
import com.absolutephoenix.dbvopackbuilder.reference.GlobalVariables;
import com.absolutephoenix.dbvopackbuilder.ui.MainWindow;
import com.absolutephoenix.dbvopackbuilder.utils.*;
import net.andrewcpu.elevenlabs.ElevenLabs;
import net.andrewcpu.elevenlabs.model.voice.Voice;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class PackGeneratorPanel extends JPanel implements ComponentListener {

    public JButton loadTopicFile = new JButton("Load Topic Files");
    public JButton removeButton = new JButton("Remove Selected");
    public JButton generateButton = new JButton("Build Pack");

    public JButton browseOnline = new JButton("<html><center>Browse<br>Community Topics</center></html>");
    public JButton contributeOnline = new JButton("<html><center>Contribute<br>Community Topics</center></html>");

    private DefaultTableModel tableModel;
    public JTable fileTable = new JTable();
    JScrollPane scrollPane = new JScrollPane();
    JLabel characterCountLabel = new JLabel("Approx Chars: : 0");

    JLabel progressLabel = new JLabel("<html>&nbsp;&nbsp;Current Mod: Not started.<br>&nbsp;&nbsp;Overall: Not started.</html>");

    public int totalCharacterCount = 0;
    public boolean isBuilding = false;

    public PackGeneratorPanel(){
        this.setLayout(null);
        this.addComponentListener(this);
        add(loadTopicFile);
        add(removeButton);
        add(characterCountLabel);
        add(generateButton);
        add(browseOnline);
        add(contributeOnline);
        tableSetup();
        progressLabel.setBorder(BorderFactory.createBevelBorder(0));
        add(progressLabel);
        actions();
    }

    public void tableSetup(){

        String[] columnNames = {"Characters", "Mod Name", "ESP Name", "Category", "Topic File"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 4;
            }
        };
        fileTable.setModel(tableModel);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        fileTable.getColumnModel().getColumn(4).setPreferredWidth(1256 - 510 - 160 - 30);

        String[] categories = {"Modders Resources", "Utilities", "Bug Fixes", "Patches", "Gameplay", "Immersion", "Skills and Leveling", "Combat", "Followers and Companions", "Creatures and Mounts", "Races and Classes", "NPCs", "Quests and Adventures", "Collectables", "Treasure Hunts", "Puzzles", "Crafting", "Items and Objects", "Player homes", "Weapons and Armour", "Guilds and Factions", "Miscellaneous"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        TableColumn categoryColumn = fileTable.getColumnModel().getColumn(3); // Adjust the index as per your table structure
        categoryColumn.setCellEditor(new DefaultCellEditor(categoryComboBox));

        scrollPane.setBorder(BorderFactory.createBevelBorder(0));
        scrollPane.setViewportView(fileTable);

        add(scrollPane);


    }
    public void actions(){

        loadTopicFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Topic Files", "topic"));
            fileChooser.setCurrentDirectory(new File("topics"));

            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    String fileContent;
                    int charCount = 0;
                    try {
                        try {
                            fileContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                        } catch (MalformedInputException ex) {
                            // If UTF-8 fails, try another encoding, for example ISO-8859-1
                            fileContent = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
                        }
                        charCount = fileContent.length();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Failed to read the file: " + file.getName() + "\n" + ex.getMessage(),
                                "File Read Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        //noinspection CallToPrintStackTrace
                        ex.printStackTrace();
                    }
                    totalCharacterCount += charCount;
                    characterCountLabel.setText("Approx Chars: " + totalCharacterCount);
                    tableModel.addRow(new Object[]{charCount, "", "", "Miscellaneous", file.getAbsolutePath()});
                    LogHelper.info(file.getName() + " has been added to the compile.");
                }
                saveTableData();
            }
        });
        removeButton.addActionListener(e -> {
            // Get the selected rows
            int[] selectedRows = fileTable.getSelectedRows();

            if (selectedRows.length > 0) {
                // Convert the view indices to model indices and sort them in reverse
                for (int i = 0; i < selectedRows.length; i++) {
                    selectedRows[i] = fileTable.convertRowIndexToModel(selectedRows[i]);
                }
                Arrays.sort(selectedRows);

                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    // Subtract the character count of the row being removed from the total
                    int modelRow = selectedRows[i];
                    int charCountToRemove = (int) tableModel.getValueAt(modelRow, 0);
                    totalCharacterCount -= charCountToRemove;
                    characterCountLabel.setText("Approx Chars: " + totalCharacterCount);
                    LogHelper.redInfo(new File(tableModel.getValueAt(modelRow, 3).toString()).getName() + " have been removed from the compile.");
                    tableModel.removeRow(modelRow);
                }
                saveTableData();
            } else {
                // Optionally, show a message when no rows are selected
                JOptionPane.showMessageDialog(MainWindow.instance, "Please select at least one row to remove.");
            }
        });
        browseOnline.addActionListener(e -> {
            TopicBrowserPanel topicBrowserPanel = new TopicBrowserPanel(MainWindow.instance, this);
            topicBrowserPanel.setVisible(true);
        });

        generateButton.addActionListener(e -> {
            if(!isBuilding) {
                DefaultTableModel model = (DefaultTableModel) fileTable.getModel();
                PackBuild.createModpackJson();
                boolean canRun = true;

                for (int x = 0; x < model.getRowCount(); x++) {
                    if (((String) model.getValueAt(x, 1)).isEmpty())
                        canRun = false;
                }

                if (canRun) {
                    isBuilding = true;
                    fileTable.setEnabled(false);
                    loadTopicFile.setEnabled(false);
                    removeButton.setEnabled(false);
                    MainWindow.instance.settingsPanel.elevenlabsSave.setEnabled(false);
                    MainWindow.instance.settingsPanel.PackSave.setEnabled(false);
                    generateButton.setText("Stop Building");
                    new Thread(() -> {
                        int totalMods = model.getRowCount();
                        int modsProcessed = 0;

                        while (model.getRowCount() > 0 && isBuilding) {
                            String currentModName = (String) model.getValueAt(0, 1);
                            if(!isBuilding)
                                break;
                            List<String> ReadyToProcess = TopicHandler.initialTopicProcessing((String) model.getValueAt(0, 4), (String) model.getValueAt(0, 1));
                            if (ReadyToProcess == null) {
                                LogHelper.warn("Skipping: " + new File((String) model.getValueAt(0, 4)).getName() + " has lines that exceed the file path limits of Windows.");
                            } else {
                                List<String> voiceStrings = TopicHandler.getVoiceGenStrings(ReadyToProcess);
                                List<String> fileStrings = TopicHandler.getFileNames(ReadyToProcess);
                                Voice useableVoice = Voice.getVoice(ConfigManager.getSetting().getElevenLabsVoiceID(), true);
                                LogHelper.notice("BEGINNING GENERATION OF: " + model.getValueAt(0, 1));
                                if (voiceStrings.size() == fileStrings.size()) {
                                    double stability = (double) ConfigManager.getSetting().getElevenLabsStability() / 100.0d;
                                    double similarity = (double) ConfigManager.getSetting().getElevenLabsClarity() / 100.0d;
                                    double style = (double) ConfigManager.getSetting().getElevenLabsStyle() / 100.0d;

                                    int totalLinesInCurrentMod = voiceStrings.size();
                                    for (int x = 0; x < totalLinesInCurrentMod; x++) {
                                        if(!isBuilding)
                                            break;
                                        if(!ElevenLabsFileHandling.saveStreamAsMp3(useableVoice, stability, similarity, style, voiceStrings.get(x), fileStrings.get(x))){
                                            isBuilding = false;
                                            break;
                                        }else {
                                            AudioManipulation.convertMP3ToWAV(fileStrings.get(x));
                                            if (ConfigManager.getSetting().getPackGenerateLip().equals("true")) {
                                                LipGen.generate(fileStrings.get(x), voiceStrings.get(x));
                                            }
                                            AudioManipulation.convertWaveToXMW(fileStrings.get(x));
                                            AudioManipulation.convertXwmAndLipToFuz(fileStrings.get(x));
                                            try {
                                                //noinspection BusyWait
                                                Thread.sleep(10);
                                            } catch (InterruptedException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                            PackBuild.copyFileToPackFolder(fileStrings.get(x), (String) model.getValueAt(0, 1));
                                            int currentModProgress = (int) (((double) (x + 1) / totalLinesInCurrentMod) * 100);
                                            int overallProgress = (int) (((double) modsProcessed / totalMods) * 100);
                                            SwingUtilities.invokeLater(() -> progressLabel.setText("<html>&nbsp;&nbsp;Processing " + currentModName + ": " + currentModProgress + "% complete.<br>&nbsp;&nbsp;Overall: " + overallProgress + "% complete.</html>"));
                                        }
                                    }
                                } else {
                                    LogHelper.error("Unable to continue. Data was processed wrong. Please try again.");
                                }
                            }
                            if(isBuilding) {
                                if (ConfigManager.getSetting().getPackBuildToBSA().equals("true"))
                                    PackBuild.bsaPack((String) model.getValueAt(0, 1));
                                model.removeRow(0);
                                saveTableData();
                                modsProcessed++;
                                SwingUtilities.invokeLater(() -> progressLabel.setText("<html>&nbsp;&nbsp;Current Mod: 100% complete.<br>&nbsp;&nbsp;Overall: 100% complete.</html>"));
                            }else {
                                SwingUtilities.invokeLater(() -> progressLabel.setText("<html>&nbsp;&nbsp;Current Mod: Canceled.<br>&nbsp;&nbsp;Overall: Canceled.</html>"));

                            }

                        }
                        try {
                            if(isBuilding){
                                PackBuild.bsaPack("AIO");
                            }
                            GlobalVariables.subscription = ElevenLabs.getUserAPI().getSubscription();
                            MainWindow.instance.setTitle("DBVO Pack Builder - " + GlobalVariables.subscription.getCharacterCount() + "/" + GlobalVariables.subscription.getCharacterLimit());
                        } catch (NullPointerException ignore) {

                        }
                        fileTable.setEnabled(true);
                        loadTopicFile.setEnabled(true);
                        removeButton.setEnabled(true);
                        MainWindow.instance.settingsPanel.elevenlabsSave.setEnabled(true);
                        MainWindow.instance.settingsPanel.PackSave.setEnabled(true);
                        generateButton.setText("Build Pack");
                        if(isBuilding) {
                            LogHelper.notice("GENERATION HAS BEEN COMPLETED SUCCESSFULLY!");
                            Desktop desktop = Desktop.getDesktop();
                            File folder = new File("build");
                            try {
                                desktop.open(folder);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            isBuilding = false;
                        }else {
                            LogHelper.notice("GENERATION HAS BEEN CANCELED!");
                        }
                    }).start();
                } else {
                    LogHelper.warn("You needs to have the mod name filled out for each topic file.");
                    isBuilding = false;
                }
            }else {
                isBuilding = false;
            }
        });

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int column = e.getColumn();

                if (column == 1 || column == 2 || column == 3) {
                    saveTableData();
                }
            }
        });
    }

    public void saveTableData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("config/tableData.dbvo"))) {
            oos.writeObject(new Vector<>(tableModel.getDataVector()));
            LogHelper.info("Table data saved successfully.");
        } catch (IOException e) {
            LogHelper.error("Error saving table data: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked") // Suppress the unchecked warning
    public void loadTableData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("config/tableData.dbvo"))) {
            Vector<?> dataVector = (Vector<?>) ois.readObject();
            totalCharacterCount = 0; // Reset total character count

            boolean needsConversion = !dataVector.isEmpty() && ((Vector<?>) dataVector.get(0)).size() == 4; // Check if conversion is needed

            for (Object row : dataVector) {
                Vector<Object> rowData = (Vector<Object>) row; // Cast to Vector<Object>
                if (needsConversion) {
                    // Insert a default value for the "Category" column at the correct position
                    rowData.insertElementAt("Miscellaneous", 3); // Insert an empty string or a default value
                }
                tableModel.addRow(rowData);
                totalCharacterCount += (int) rowData.get(0);
            }
            characterCountLabel.setText("Approx Chars: " + totalCharacterCount); // Update label

            if (needsConversion) {
                saveTableData(); // Save the data in the new format
            }

            LogHelper.info("Table data loaded successfully.");
        } catch (FileNotFoundException e) {
            LogHelper.info("No saved table data found.");
        } catch (IOException | ClassNotFoundException e) {
            LogHelper.error("Error loading table data: " + e.getMessage());
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        loadTopicFile.setBounds(10, 10, 140, 25);
        removeButton.setBounds(10, loadTopicFile.getY() + loadTopicFile.getHeight() + 20, 140, 25);
        browseOnline.setBounds(10, removeButton.getY() + removeButton.getHeight() + 20, 140, 50);
        contributeOnline.setBounds(10, browseOnline.getY() + browseOnline.getHeight() + 20, 140, 50);

        characterCountLabel.setBounds(10, getHeight() - 25 - 10, 140, 25);
        generateButton.setBounds(10, getHeight() - 25 - 10 - 25, 140, 25);
        scrollPane.setBounds(160, 10, getWidth() - 10 - 160, getHeight() - 60);
        progressLabel.setBounds(scrollPane.getX(), scrollPane.getY() + scrollPane.getHeight() + 5 , scrollPane.getWidth(), 40);
    }

    public void componentMoved(ComponentEvent e) {

    }
    public void componentShown(ComponentEvent e) {

    }
    public void componentHidden(ComponentEvent e) {

    }
}
