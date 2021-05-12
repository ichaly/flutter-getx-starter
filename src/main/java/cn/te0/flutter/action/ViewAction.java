package cn.te0.flutter.action;

import cn.te0.flutter.ui.NewWidgetDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import java.util.Objects;


public class ViewAction extends AnAction {
    private Project project;
    private DialogWrapper dialog;

    ViewAction() {
        getTemplatePresentation().setText("New View/Page...");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        dialog = new NewWidgetDialog(project, Objects.requireNonNull(event.getData(PlatformDataKeys.PSI_ELEMENT)).toString().split(":")[1]);
        dialog.show();
    }
}