package cn.te0.flutter.action;

import cn.te0.flutter.helper.DataService;
import cn.te0.flutter.helper.ViewHelper;
import cn.te0.flutter.ui.NewWidgetDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class ViewAction extends AnAction {
    private Project project;
    private String psiPath;
    private DataService data;

    ViewAction() {
        getTemplatePresentation().setText("New View/Page...");
    }

    /**
     * Overall popup entity
     */
    private JDialog jDialog;
    private JTextField nameTextField;
    private ButtonGroup templateGroup;
    /**
     * Checkbox
     * Use folder：default true
     * Use prefix：default false
     */
    private JCheckBox folderBox, prefixBox, disposeBox;

    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        psiPath = event.getData(PlatformDataKeys.PSI_ELEMENT).toString().split(":")[1];
        initData();
        initView();
    }

    private void initData() {
        data = DataService.getInstance();
        jDialog = new JDialog(new JFrame(), "GetX Template Code Produce");
    }

    private void initView() {
        //Set function button
        Container container = jDialog.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        //Set the main module style: mode, function
        //deal default value
        setModule(container);

        //Setting options: whether to use prefix
        //deal default value
        setCodeFile(container);

        //Generate file name and OK cancel button
        setNameAndConfirm(container);

        //Choose a pop-up style
        setJDialog();
    }

    /**
     * generate  file
     */
    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please input the module name", "Info");
            return;
        }
        dispose();
        String type = templateGroup.getSelection().getActionCommand();
        //deal default value
        data.defaultMode = "Default".equals(type);
        data.useFolder = folderBox.isSelected();
        data.usePrefix = prefixBox.isSelected();
        data.autoDispose = disposeBox.isSelected();
        ViewHelper.getInstance().createView(nameTextField.getText(), psiPath);
    }

    /**
     * Set the overall pop-up style
     */
    private void setJDialog() {
        //The focus is on the current pop-up window,
        // and the focus will not shift even if you click on other areas
        jDialog.setModal(true);
        //Set padding
        ((JPanel) jDialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jDialog.setSize(400, 335);
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
    }

    /**
     * Main module
     */
    private void setModule(Container container) {
        //Two rows and two columns
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(1, 2));
        //Set the main module style：mode, function
        template.setBorder(BorderFactory.createTitledBorder("Select Mode"));
        //default: high cn.te0.fgs.setting
        JRadioButton defaultBtn = new JRadioButton("Default", data.defaultMode);
        defaultBtn.setActionCommand("Default");
        setPadding(defaultBtn, 5, 10);
        JRadioButton highBtn = new JRadioButton("Easy", !data.defaultMode);
        setPadding(highBtn, 5, 10);
        highBtn.setActionCommand("Easy");


        template.add(defaultBtn);
        template.add(highBtn);
        templateGroup = new ButtonGroup();
        templateGroup.add(defaultBtn);
        templateGroup.add(highBtn);

        container.add(template);
        setDivision(container);
    }

    /**
     * Generate file
     */
    private void setCodeFile(Container container) {
        //Select build file
        JPanel file = new JPanel();
        file.setLayout(new GridLayout(2, 2));
        file.setBorder(BorderFactory.createTitledBorder("Select Function"));

        //use folder
        folderBox = new JCheckBox("Use Folder", data.useFolder);
        setMargin(folderBox, 5, 10);
        file.add(folderBox);

        //use prefix
        prefixBox = new JCheckBox("Use Prefix", data.usePrefix);
        setMargin(prefixBox, 5, 10);
        file.add(prefixBox);

        //auto dispose
        disposeBox = new JCheckBox("Auto Dispose", data.autoDispose);
        setMargin(disposeBox, 5, 10);
        file.add(disposeBox);

        container.add(file);
        setDivision(container);
    }

    /**
     * Generate file name and button
     */
    private void setNameAndConfirm(Container container) {
        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("View Name"));
        nameTextField = new JTextField(30);
        nameTextField.addKeyListener(keyListener);
        nameField.add(nameTextField);
        container.add(nameField);

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        //Set bottom spacing
        setDivision(container);

        //OK cancel button
        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);

        JButton ok = new JButton("OK");
        ok.setForeground(JBColor.GREEN);
        ok.addActionListener(actionListener);
        menu.add(cancel);
        menu.add(ok);
        container.add(menu);
    }

    private void setPadding(JRadioButton btn, int top, int bottom) {
        btn.setBorder(BorderFactory.createEmptyBorder(top, 10, bottom, 0));
    }

    private void setMargin(JCheckBox btn, int top, int bottom) {
        btn.setBorder(BorderFactory.createEmptyBorder(top, 10, bottom, 0));
    }

    private void setDivision(Container container) {
        //Separate the spacing between modules
        JPanel margin = new JPanel();
        container.add(margin);
    }

    private void dispose() {
        jDialog.dispose();
    }

    private final KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                save();
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                save();
            }
        }
    };
}