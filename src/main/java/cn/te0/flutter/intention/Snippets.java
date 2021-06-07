package cn.te0.flutter.intention;

/**
 * @author Chaly
 */
public class Snippets {

    public static final String SUFFIX = "Logic";
    public static final String PREFIX_SELECTION = "Subject";
    public static final String GETX_SNIPPET_KEY = PREFIX_SELECTION + SUFFIX;

    static String getSnippet(SnippetType snippetType, String widget) {
        switch (snippetType) {
            case Obx:
                return snippetObx(widget);
            case GetBuilder:
                return snippetGetBuilder(widget);
            case GetX:
                return snippetGetX(widget);
            default:
                return "";
        }
    }

    private static String snippetObx(String widget) {
        return String.format("Obx(() {\n" +
            "  return %1$s;\n" +
            "})", widget);
    }

    private static String snippetGetBuilder(String widget) {
        return String.format("GetBuilder<%1$s>(\n" +
            "  builder: (%2$s) {\n" +
            "    return %3$s;\n" +
            "  },\n" +
            ")", GETX_SNIPPET_KEY, SUFFIX.toLowerCase(), widget);
    }

    private static String snippetGetX(String widget) {
        return String.format("GetX<%1$s>(\n" +
            "  init: %1$s(),\n" +
            "  initState: (_) {},\n" +
            "  builder: (%2$s) {\n" +
            "    return %3$s;\n" +
            "  },\n" +
            ")", GETX_SNIPPET_KEY, SUFFIX.toLowerCase(), widget);
    }
}