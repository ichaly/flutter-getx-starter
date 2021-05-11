package cn.te0.flutter.ui;

import lombok.Data;

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
    private JRadioButton folder;
    private JRadioButton prefix;
    private JTextField nameField;

    public String getName() {
        return nameField.getText();
    }

    public JPanel getRoot() {
        return root;
    }
}