package cn.te0.flutter.intention;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chaly
 */
public class WrapWithObxAction extends WrapWithAction {
    public WrapWithObxAction() {
        super(SnippetType.Obx);
    }

    @Override
    @NotNull
    public String getText() {
        return "Wrap with Obx";
    }
}