package cn.te0.flutter.helper;

import cn.te0.flutter.utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author chaly
 */
public class ViewHelper {

    private static class SingletonHolder {
        private static final ViewHelper INSTANCE = new ViewHelper();
    }

    private ViewHelper() {
    }

    public static ViewHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void createView(Project project, String name, String folder) {
        String prefix = "";
        String tmp = Utils.toUnderline(name);
        if (GetXConfig.useFolder) {
            folder = folder + "/" + tmp;
        }
        if (GetXConfig.usePrefix) {
            prefix = tmp + "_";
        }
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("Utils", TemplateHelper.useStatic(Utils.class.getName()));
        map.put("name", name);
        map.put("pageName", "Page");
        map.put("viewName", "View");
        map.put("isPage", GetXConfig.isPage);
        map.put("useFolder", GetXConfig.useFolder);
        map.put("usePrefix", GetXConfig.usePrefix);
        map.put("autoDispose", GetXConfig.autoDispose);
        map.put("defaultMode", GetXConfig.defaultMode);

        TemplateHelper.getInstance().generator(
            "getx/weiget.dart.ftl", String.format("%s/%s%s.dart", folder, prefix, GetXConfig.isPage ? "page" : "view"), map
        );
        TemplateHelper.getInstance().generator(
            "getx/logic.dart.ftl", String.format("%s/%slogic.dart", folder, prefix), map
        );
        if (GetXConfig.defaultMode) {
            TemplateHelper.getInstance().generator(
                "getx/state.dart.ftl", String.format("%s/%sstate.dart", folder, prefix), map
            );
        }
        if (GetXConfig.isPage) {
            TemplateHelper.getInstance().generator(
                "getx/binding.dart.ftl", String.format("%s/%sbinding.dart", folder, prefix), map
            );
        }
        Objects.requireNonNull(ProjectUtil.guessProjectDir(project)).refresh(false, true);
    }
}