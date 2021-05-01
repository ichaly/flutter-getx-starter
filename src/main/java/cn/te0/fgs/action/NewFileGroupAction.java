package cn.te0.fgs.action;

import cn.te0.fgs.utils.Icons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;

/**
 * @author chaly
 */
public class NewFileGroupAction extends DefaultActionGroup implements DumbAware {

    public NewFileGroupAction() {
        setPopup(true);
        Presentation p = getTemplatePresentation();
        p.setIcon(Icons.LOGO);
        p.setText("Flutter GetX Starter");

        //初始化工程
        add(new InitAction());
    }
}
