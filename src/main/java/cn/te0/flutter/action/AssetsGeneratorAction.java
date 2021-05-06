package cn.te0.flutter.action;

import cn.te0.flutter.helper.TemplateHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.ruiyu.file.FileHelpers;
import com.ruiyu.helper.YamlHelper;
import io.flutter.pub.PubRoot;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chaly
 */
public class AssetsGeneratorAction extends AnAction {
    private static final String ASSETS_ROOT = "assets";
    private static final Splitter SPLITTER = Splitter.on(File.separator).omitEmptyStrings().trimResults();

    Project project;
    String base, last, name;
    Set<String> variant = new HashSet<>();
    Multimap<String, String> assets = ArrayListMultimap.create();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        base = Objects.requireNonNull(project).getBasePath() + File.separator + ASSETS_ROOT;
        if (Objects.requireNonNull(PubRoot.forFile(FileHelpers.getProjectIdeaFile(project))).isFlutterPlugin()) {
            name = Objects.requireNonNull(YamlHelper.getPubSpecConfig(project)).getName();
        }
        getAssets(new File(base));
        updateYaml();
        updateDart();
        project.getBaseDir().refresh(false, true);
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
        } else if (file.isFile()) {
            assets.put(file.getName(), last);
        }
    }

    public void updateYaml() {
        List<String> paths = assets.values().stream().distinct().filter(item -> !variant.contains(item)).sorted().map(s -> "assets" + s + "/").collect(Collectors.toList());
        PubRoot pubRoot = PubRoot.forFile(FileHelpers.getProjectIdeaFile(project));
        String yamlFile = pubRoot.getPubspec().getPath();
        String bakFile = String.format("%s.%s.bak", yamlFile, DateFormatUtils.format(new Date(), "yyyyMMddHHmm"));
        try {
            //备份原文件
            FileReader reader = new FileReader(yamlFile);
            FileWriter writer = new FileWriter(bakFile);
            IOUtils.copy(reader, writer);
            //修改配置文件
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(new FileInputStream(yamlFile));
            ((Map) map.get("flutter")).put("assets", paths);
            yaml.dump(map, new FileWriter(yamlFile));
            reader.close();
            writer.flush();
            writer.close();
            System.err.println(map);
        } catch (Exception e) {
        }
    }

    public void updateDart() {
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
        File file = new File(Objects.requireNonNull(project).getBasePath() + "/lib/gen/res/");
        if (!file.exists()) {
            file.mkdirs();
        }
        Map<String, Object> map = new HashMap();
        map.put("packageName", name);
        map.put("res", tables.rowMap());
        TemplateHelper.getInstance().generator(
            "resources.dart.ftl", file.getAbsolutePath() + "/resources.dart", map
        );
    }
}