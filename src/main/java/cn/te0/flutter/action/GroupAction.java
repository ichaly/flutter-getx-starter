package cn.te0.flutter.action;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.IconLoader;
import com.ruiyu.jsontodart.JsonToDartBeanAction;

import javax.swing.*;

/**
 * @author chaly
 */
public class GroupAction extends DefaultActionGroup implements DumbAware {
    private static final Icon LOGO = IconLoader.getIcon("/icons/logo.svg", GroupAction.class);

    public GroupAction() {
        setPopup(true);
        Presentation p = getTemplatePresentation();
        p.setIcon(LOGO);
        p.setText("Flutter GetX Starter");

        //初始化工程
        add(new InitAction());
        //创建新页面
        add(new ViewAction());
        //创建的实体类
        add(new JsonToDartBeanAction());
    }
}