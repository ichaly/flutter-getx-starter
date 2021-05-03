package cn.te0.flutter.setting;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * @author chaly
 */
public class SettingsComponent {
    public JPanel mainPanel;
    public JBTextField viewName;
    public JBTextField logicName;
    public JBTextField stateName;

    public SettingsComponent() {
        viewName = new JBTextField();
        logicName = new JBTextField();
        stateName = new JBTextField();

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("View Name: "), viewName)
            .addLabeledComponent(new JBLabel("Logic Name: "), logicName)
            .addLabeledComponent(new JBLabel("State Name: "), stateName)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
    }
}