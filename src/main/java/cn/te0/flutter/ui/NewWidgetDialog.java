package cn.te0.flutter.ui;

import cn.te0.flutter.helper.GetXConfig;
import cn.te0.flutter.helper.ViewHelper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Chaly
 * new NewWidgetDialog().show();
 */
public class NewWidgetDialog extends DialogWrapper {
    private final String basePath;
    private final Project project;
    private final NewWidgetForm form;

    public NewWidgetDialog(Project project, String basePath) {
        super(true);
        this.project = project;
        this.basePath = basePath;
        setTitle("GetX Template Code Produce");
        setSize(460, 360);
        form = new NewWidgetForm();
        init();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        return form.getRoot();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String text = form.getName();
        if (StringUtils.isNotBlank(text)) {
            return null;
        } else {
            return new ValidationInfo("校验不通过");
        }
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (StringUtils.isBlank(form.getName())) {
            Messages.showInfoMessage(project, "Please input the module name", "Info");
            return;
        }
        close(CLOSE_EXIT_CODE);
        //deal default value
        GetXConfig.defaultMode = form.isDefaultMode();
        GetXConfig.useFolder = form.isUseFolder();
        GetXConfig.usePrefix = form.isUsePrefix();
        GetXConfig.autoDispose = form.isAuto();
        GetXConfig.isPage = form.isPage();
        ViewHelper.getInstance().createView(project, form.getName(), basePath);
    }
}
