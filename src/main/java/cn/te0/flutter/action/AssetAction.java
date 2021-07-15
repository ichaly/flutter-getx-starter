package cn.te0.flutter.action;

import cn.te0.flutter.helper.TemplateHelper;
import cn.te0.flutter.helper.YamlHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.ruiyu.utils.ExtensionsKt;
import com.ruiyu.utils.GsonUtil;
import io.flutter.pub.PubRoot;
import io.flutter.utils.FlutterModuleUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.internal.StringUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chaly
 */
public class AssetAction extends AnAction {
    private static final String ASSETS_ROOT = "assets";
    private static final Splitter SPLITTER = Splitter.on(File.separator).omitEmptyStrings().trimResults();

    Project project;
    String base, last, name;
    Set<String> variant = new HashSet<>();
    Multimap<String, String> assets = ArrayListMultimap.create();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        Module[] modules = FlutterModuleUtils.getModules(project);
        for (Module module : modules) {
            if (FlutterModuleUtils.isFlutterModule(module)) {
                VirtualFile parent = module.getModuleFile().getParent();
                base = parent.getPath() + File.separator + ASSETS_ROOT;
                if (PubRoot.forFile(parent.findChild("lib")).isFlutterPlugin()) {
                    name = YamlHelper.getName(project);
                }
                getAssets(new File(base));
                updateYaml(module);
                updateDart(module);
            }
        }

        Objects.requireNonNull(ProjectUtil.guessProjectDir(project)).refresh(false, true);
    }

    public void getAssets(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            last = file.getAbsolutePath().replace(base, "");
            // 2.0x 3.0x 等多分辨率变体目录不处理
            if (file.getName().matches("^[1-9](\\.\\d)x$")) {
                variant.add(last);
            }
            //处理下级目录
            for (File f : Arrays.stream(Objects.requireNonNull(file.listFiles((f, n) -> {
                // 忽略 MacOS 中的 .DS_Store 文件
                return !".DS_Store".equals(n);
            }))).sorted((f1, f2) -> {
                // 重新排序，文件排在目录前面。先处理文件，然后处理下级目录，方便处理资源变体
                if (f1.isFile() && f2.isDirectory()) {
                    return -1;
                } else if (f1.isDirectory() && f2.isFile()) {
                    return 1;
                }
                return 0;
            }).collect(Collectors.toList())) {
                getAssets(f);
            }
        } else if (file.isFile() && !StringUtil.isBlank(last)) {
            assets.put(file.getName(), last);
        }
    }

    public void updateYaml(Module module) {
        List<String> paths = assets.values().stream().distinct().filter(item -> !variant.contains(item)).sorted().map(s -> "assets" + s + "/").collect(Collectors.toList());
        List<Map<String, Object>> fonts = assets.keys().stream().filter(item -> item.toLowerCase(Locale.ROOT).endsWith(".ttf")).sorted().map(s -> {
            String family = s.replace(".ttf", "").split("-")[0];
            List<String> directory = assets.get(s).stream().distinct().collect(Collectors.toList());
            return new HashMap<String, Object>() {{
                put("family", family);
                put("fonts", Lists.newArrayList(
                    new HashMap<String, Object>() {{
                        for (String value : directory) {
                            put("asset", String.format("%s%s/%s", ASSETS_ROOT, value, s));
                        }
                    }}
                ));
            }};
        }).collect(Collectors.toList());
        String yamlFile = module.getModuleFile().getParent().getPath() + "/pubspec.yaml";
        YamlHelper.updateYaml(yamlFile, map -> {
            Map flutter = ((Map) map.get("flutter"));
            flutter.put("assets", paths);
            flutter.put("fonts", fonts);
        });
    }

    @SneakyThrows
    public void updateDart(Module module) {
        Table<String, String, String> tables = TreeBasedTable.create();
        Set<String> files = assets.keySet();
        for (String file : files) {
            Collection<String> paths = assets.get(file);
            for (String path : paths) {
                List<String> list = Lists.newArrayList(SPLITTER.splitToList(path));
                //如果是变体目录
                if (variant.contains(path)) {
                    list.remove(list.size() - 1);
                }
                String row = list.remove(0);
                list.add(file);
                String value = Joiner.on("/").skipNulls().join("assets", row, list.toArray());
                String column = Joiner.on("_").join(list).split("\\.")[0];
                if (tables.get(row, column) == null) {
                    tables.put(row, column, value);
                }
            }
        }
        File file = new File(module.getModuleFile().getParent().getPath() + "/lib/gen/res/");
        if (!file.exists()) {
            file.mkdirs();
        }
        //处理iconfont图标
        Map<String, Map<String, String>> res = tables.rowMap();
        Map<String, String> icons = new HashMap<>();
        Map<String, String> icon = res.remove("icon");
        if (icon != null) {
            for (Map.Entry<String, String> entry : icon.entrySet()) {
                String json = FileUtils.readFileToString(new File(base.replace(ASSETS_ROOT, "") + entry.getValue()), Charset.defaultCharset());
                Map<String, Object> map = GsonUtil.fromJson(json, new TypeToken<>() {
                });
                List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("glyphs");
                for (Map<String, Object> m : list) {
                    icons.put(m.get("font_class").toString(), String.format("0x%s", m.get("unicode")));
                }
            }
        }
        res = Maps.newHashMap(res);
        res.put("icon", icons);
        //准备生成模板
        Map<String, Object> map = new HashMap();
        map.put("packageName", name);
        map.put("res", res);
        TemplateHelper.getInstance().generator(
            "asset/resources.dart.ftl", file.getAbsolutePath() + "/resources.dart", map
        );
        Objects.requireNonNull(ProjectUtil.guessProjectDir(project)).refresh(false, true);
        ExtensionsKt.showNotify(project, "Assets reference has been updated successfully.");
    }
}
