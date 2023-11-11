package com.absolutephoenix.dbvopackbuilder.ui.dialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TopicAddDialog extends JDialog {
    private JTextField modNameField;
    private JTextField espNameField;
    private JTextField filePathField;
    private JButton fileChooserButton;
    private JButton submitButton;
    private JFileChooser fileChooser;

    private TopicAddData topicAddData = null;

    public TopicAddDialog(Frame parent) {
        super(parent, "Add Topic Details", true);
        initComponents();
    }

    private void initComponents() {
        modNameField = new JTextField(20);
        espNameField = new JTextField(20);
        filePathField = new JTextField(20);
        filePathField.setEditable(false);

        int height = filePathField.getPreferredSize().height;

        // Use an ImageIcon to create the icon from an image file
        ImageIcon originalIcon  = new ImageIcon(getClass().getResource("/img/findFile.png"));// Replace with your icon path
        ImageIcon resizedIcon = resizeIcon(originalIcon, height, height);

        fileChooserButton = new JButton(resizedIcon);
        fileChooserButton.setPreferredSize(new Dimension(height, height));
        fileChooserButton.setMaximumSize(new Dimension(height, height));
        fileChooserButton.setMargin(new Insets(0, 0, 0, 0)); // Remove default margins if necessary

        fileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSubmit();
            }
        });

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Topic Files (*.topic)", "topic");
        fileChooser.setFileFilter(filter);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(new JLabel("Mod Name:"));
        mainPanel.add(modNameField);
        mainPanel.add(new JLabel("ESP Name:"));
        mainPanel.add(espNameField);
        mainPanel.add(new JLabel("Topic File:"));
        JPanel filePathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filePathPanel.add(filePathField);
        filePathPanel.add(fileChooserButton);
        mainPanel.add(filePathPanel);

        // Add vertical strut as a spacer
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Adjust the height as needed

        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // This will center the button
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, submitButton.getPreferredSize().height));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel);

        // Determine the height from the text field to use for the button

        // Set the button to be square based on the height of the text field
        fileChooserButton.setPreferredSize(new Dimension(height, height));
        fileChooserButton.setMaximumSize(new Dimension(height, height));

        // To ensure that the file chooser button does not stretch vertically, make it's maximum and preferred sizes the same
        fileChooserButton.setPreferredSize(new Dimension(height, height));
        fileChooserButton.setMaximumSize(fileChooserButton.getPreferredSize());

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
    }

    private ImageIcon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void chooseFile() {
        int returnVal = fileChooser.showOpenDialog(TopicAddDialog.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filePathField.setText(file.getAbsolutePath());
        }
    }

    private void onSubmit() {
        topicAddData = new TopicAddData(
                modNameField.getText(),
                espNameField.getText(),
                new File(filePathField.getText())
        );

        dispose();
    }

    public TopicAddData getTopicAddData() {
        return topicAddData;
    }
}
