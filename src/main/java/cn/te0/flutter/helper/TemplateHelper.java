package cn.te0.flutter.helper;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.netty.util.CharsetUtil;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author chaly
 */
public class TemplateHelper {
    Configuration config;

    private static class SingletonHolder {
        private static final TemplateHelper INSTANCE = new TemplateHelper();
    }

    private TemplateHelper() {
        config = new Configuration(Configuration.VERSION_2_3_31);
        config.setDefaultEncoding(CharsetUtil.UTF_8.name());
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
        config.setClassForTemplateLoading(TemplateHelper.class, "/templates/asset");
    }

    public static TemplateHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void generator(String template, String target, Map<String, ?> data, String... extra) {
        try (
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), CharsetUtil.UTF_8.name()))
        ) {
            config.setSharedVariable("extra", extra);
            Template tpl = config.getTemplate(template, CharsetUtil.UTF_8.name());
            tpl.process(data, out);
            out.flush();
        } catch (Exception ignored) {
        }
    }
}