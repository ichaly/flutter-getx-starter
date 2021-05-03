package cn.te0.flutter.setting;

import cn.te0.flutter.helper.DataService;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author chaly
 */
public class SettingsConfigurable implements Configurable {

    private final DataService data = DataService.getInstance();
    private SettingsComponent mSetting;

    @Override
    public String getDisplayName() {
        return "GetX Setting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mSetting = new SettingsComponent();
        return mSetting.mainPanel;
    }

    @Override
    public boolean isModified() {
        boolean modified;
        modified = !mSetting.logicName.getText().equals(data.logicName)
            || !mSetting.stateName.getText().equals(data.stateName)
            || !mSetting.viewName.getText().equals(data.viewName);
        return modified;
    }

    @Override
    public void apply() {
        data.logicName = mSetting.logicName.getText();
        data.stateName = mSetting.stateName.getText();
        data.viewName = mSetting.viewName.getText();
    }

    @Override
    public void reset() {
        mSetting.logicName.setText(data.logicName);
        mSetting.stateName.setText(data.stateName);
        mSetting.viewName.setText(data.viewName);
    }

    @Override
    public void disposeUIResources() {
        mSetting = null;
    }
}