package cn.te0.flutter.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chaly
 * <extensions defaultExtensionNs="com.intellij">
 *     <!-- secondary:true表示设置在tool window bar最下方 -->
 *     <toolWindow id="customer tool window" anchor="right" secondary="true"
 *                 factoryClass="cn.te0.flutter.ui.NewWidgetToolWindowFactory"/>
 * </extensions>
 */
public class NewWidgetToolWindowFactory implements ToolWindowFactory, Condition<Project> {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        NewWidgetDialog myToolWindow = new NewWidgetDialog();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getRoot(), "自定义tool window", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public boolean value(Project project) {
        return true;
    }
}