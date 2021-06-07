package cn.te0.flutter.intention;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chaly
 */
public class WrapWithGetXAction extends WrapWithAction {
    public WrapWithGetXAction() {
        super(SnippetType.GetX);
    }

    @Override
    @NotNull
    public String getText() {
        return "Wrap with GetX";
    }
}