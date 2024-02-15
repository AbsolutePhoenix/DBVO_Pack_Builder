package com.absolutephoenix.dbvopackbuilder.ui.panels;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class TopicBrowserPanel extends JDialog {
    private JTable modListTable;
    private JTable selectedModsTable;
    private DefaultTableModel modListModel;
    private DefaultTableModel selectedModsModel;
    private PackGeneratorPanel packGeneratorPanel;

    public TopicBrowserPanel(Frame owner, PackGeneratorPanel packGeneratorPanel) {
        super(owner, "Topic Browser", true);
        this.packGeneratorPanel = packGeneratorPanel;
        initializeComponents();
        setSize(1200, 720);
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Initialize tables and models
        modListModel = new DefaultTableModel(new Object[]{"Characters", "Mod Name", "ESP Name", "Category"}, 0);
        modListTable = new JTable(modListModel);

        selectedModsModel = new DefaultTableModel(new Object[]{"Characters", "Mod Name", "ESP Name", "Category"}, 0);
        selectedModsTable = new JTable(selectedModsModel);

        // Load mod list from FTP
        loadModList();

        // Buttons for moving rows between tables
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton addButton = new JButton(">>");
        JButton removeButton = new JButton("<<");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRow(modListTable, modListModel, selectedModsTable, selectedModsModel);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRow(selectedModsTable, selectedModsModel, modListTable, modListModel);
            }
        });
        buttonPanel.add(removeButton);
        buttonPanel.add(addButton);

        // Layout
        add(new JScrollPane(modListTable), BorderLayout.WEST);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(selectedModsTable), BorderLayout.EAST);

        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                updatePackGeneratorPanel();
            }
        });
    }

    private void loadModList() {

    }

    private void moveRow(JTable fromTable, DefaultTableModel fromModel, JTable toTable, DefaultTableModel toModel) {
        int selectedRow = fromTable.getSelectedRow();
        if (selectedRow >= 0) {
            Vector<?> rowData = (Vector<?>) fromModel.getDataVector().get(selectedRow);
            toModel.addRow(rowData);
            fromModel.removeRow(selectedRow);
        }
    }

    private void updatePackGeneratorPanel() {
    }
}
