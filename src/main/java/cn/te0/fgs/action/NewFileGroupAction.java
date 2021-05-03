package cn.te0.fgs.action;

import cn.te0.fgs.utils.Icons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.ruiyu.jsontodart.JsonToDartBeanAction;

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
        //创建新页面
        add(new NewViewAction());
        //创建的实体类
        add(new JsonToDartBeanAction());
    }
}