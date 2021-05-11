package cn.te0.flutter.ui;

import javax.swing.*;

/**
 * @author Chaly
 */
public class NewWidgetForm {
    private JPanel root;
    private JRadioButton defaultMode;
    private JRadioButton simpleMode;
    private JRadioButton view;
    private JRadioButton page;
    private JCheckBox folder;
    private JCheckBox prefix;
    private JCheckBox auto;
    private JTextField nameField;

    public JPanel getRoot() {
        return root;
    }

    public String getName() {
        return nameField.getText();
    }

    public boolean isDefaultMode() {
        return defaultMode.isSelected();
    }

    public boolean isPage() {
        return page.isSelected();
    }

    public boolean isUseFolder() {
        return folder.isSelected();
    }

    public boolean isUsePrefix() {
        return prefix.isSelected();
    }

    public boolean isAuto() {
        return auto.isSelected();
    }
}