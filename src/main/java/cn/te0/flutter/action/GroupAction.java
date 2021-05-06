package cn.te0.flutter.action;

import cn.te0.flutter.utils.Icons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.ruiyu.jsontodart.JsonToDartBeanAction;

/**
 * @author chaly
 */
public class GroupAction extends DefaultActionGroup implements DumbAware {

    public GroupAction() {
        setPopup(true);
        Presentation p = getTemplatePresentation();
        p.setIcon(Icons.LOGO);
        p.setText("Flutter GetX Starter");

        //初始化工程
        add(new InitAction());
        //创建新页面
        add(new ViewAction());
        //创建的实体类
        add(new JsonToDartBeanAction());
    }
}