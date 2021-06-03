package cn.te0.flutter.helper;


import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
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
    private static final Version VERSION_2_3_31 = new Version(2, 3, 31);

    Configuration config;
    private final static BeansWrapper WRAPPER = new BeansWrapper(VERSION_2_3_31);
    private final static TemplateHashModel STATICS = WRAPPER.getStaticModels();

    private static class SingletonHolder {
        private static final TemplateHelper INSTANCE = new TemplateHelper();
    }

    private TemplateHelper() {
        config = new Configuration(VERSION_2_3_31);
        config.setDefaultEncoding(CharsetUtil.UTF_8.name());
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setObjectWrapper(new DefaultObjectWrapper(VERSION_2_3_31));
        config.setClassForTemplateLoading(TemplateHelper.class, "/templates");
    }

    public static TemplateHashModel useStatic(String packageName) {
        try {
            return (TemplateHashModel) STATICS.get(packageName);
        } catch (TemplateModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TemplateHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void generator(String template, String target, Map<String, Object> data) {
        try (
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), CharsetUtil.UTF_8.name()))
        ) {
            Template tpl = config.getTemplate(template, CharsetUtil.UTF_8.name());
            tpl.process(data, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}