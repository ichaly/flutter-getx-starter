package cn.te0.flutter.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Chaly
 * new NewWidgetDialogWrapper().show();
 */
public class NewWidgetDialog extends DialogWrapper {
    private NewWidgetForm dialog;

    public NewWidgetDialog() {
        super(true);
        setTitle("GetX Template Code Produce");
        setSize(460,360);
        dialog = new NewWidgetForm();
        init();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        return dialog.getRoot();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String text = dialog.getName();
        if (StringUtils.isNotBlank(text)) {
            return null;
        } else {
            return new ValidationInfo("校验不通过");
        }
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        new Timer("Show Balloon", false).schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 500);
    }
}