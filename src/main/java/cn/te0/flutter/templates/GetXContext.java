package cn.te0.flutter.templates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chaly
 */
public class GetXContext extends TemplateContextType {
    protected GetXContext() {
        super("FLUTTER", "Flutter");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        return templateActionContext.getFile().getName().endsWith(".dart");
    }
}