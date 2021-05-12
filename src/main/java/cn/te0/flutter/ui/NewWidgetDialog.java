package cn.te0.flutter.ui;

import cn.te0.flutter.helper.DataService;
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
    private String basePath;
    private Project project;
    private DataService data;
    private NewWidgetForm form;

    public NewWidgetDialog(Project project, String basePath) {
        super(true);
        this.project = project;
        this.basePath = basePath;
        setTitle("GetX Template Code Produce");
        setSize(460, 360);
        form = new NewWidgetForm();
        data = DataService.getInstance();
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
        data.defaultMode = form.isDefaultMode();
        data.useFolder = form.isUseFolder();
        data.usePrefix = form.isUsePrefix();
        data.autoDispose = form.isAuto();
        ViewHelper.getInstance().createView(form.getName(), basePath);
    }
}