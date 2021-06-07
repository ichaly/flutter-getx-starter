package cn.te0.flutter.intention;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chaly
 */
public class WrapWithGetBuilderAction extends WrapWithAction {
    public WrapWithGetBuilderAction() {
        super(SnippetType.GetBuilder);
    }

    @Override
    @NotNull
    public String getText() {
        return "Wrap with GetBuilder";
    }
}