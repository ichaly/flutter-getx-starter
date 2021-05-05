package cn.te0.flutter.action;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chaly
 */
public class AssetsGeneratorAction extends AnAction {
    private static final String ASSETS_ROOT = "assets";
    private static final Splitter splitter = Splitter.on(File.separator).omitEmptyStrings().trimResults();

    String base, last;
    Set<String> variant = new HashSet<>();
    Multimap<String, String> assets = ArrayListMultimap.create();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        base = Objects.requireNonNull(project).getBasePath() + File.separator + ASSETS_ROOT;
        File file = new File(base);
        getAssets(file);
        updateYaml();
        updateDart();
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
        List<String> paths = assets.values().stream().distinct().filter(item -> !variant.contains(item)).sorted().collect(Collectors.toList());
    }

    public void updateDart() {
        Table<String, String, String> tables = TreeBasedTable.create();
        Set<String> names = assets.keySet();
        for (String name : names) {
            Collection<String> paths = assets.get(name);
            for (String path : paths) {
                List<String> list = Lists.newArrayList(splitter.splitToList(path));
                //如果是变体目录
                if (variant.contains(path)) {
                    list.remove(list.size() - 1);
                }
                String row = list.remove(0);
                list.add(name);
                String value = Joiner.on("/").skipNulls().join("assets", null, list.toArray());
                String column = Joiner.on("_").join(list).split("\\.")[0];
                if (tables.get(row, column) == null) {
                    tables.put(row, column, value);
                }
            }
        }
        System.err.println(tables);
    }
}