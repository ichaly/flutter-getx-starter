package cn.te0.flutter.helper;


import cn.te0.flutter.utils.Utils;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
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
    private final static BeansWrapper WRAPPER = new BeansWrapper(Configuration.VERSION_2_3_31);
    private final static TemplateHashModel STATICS = WRAPPER.getStaticModels();

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

    private static TemplateHashModel useStatic(String packageName) {
        try {
            TemplateHashModel fileStatics = (TemplateHashModel) STATICS.get(packageName);
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
            data.put("Utils", useStatic(Utils.class.getName()));

            data.put("isPage", DataService.getInstance().isPage);
            data.put("useFolder", DataService.getInstance().useFolder);
            data.put("usePrefix", DataService.getInstance().usePrefix);
            data.put("autoDispose", DataService.getInstance().autoDispose);
            data.put("defaultMode", DataService.getInstance().defaultMode);

            tpl.process(data, out);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
