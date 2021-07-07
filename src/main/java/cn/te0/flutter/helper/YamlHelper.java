package cn.te0.flutter.helper;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.ruiyu.helper.PubSpecConfig;
import com.ruiyu.setting.Settings;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author chaly
 */
public class YamlHelper {
    public static void updateYaml(String path, YamlHandler handler) {
        String backup = String.format("%s.%s.bak", path, DateFormatUtils.format(new Date(), "yyyyMMddHHmm"));
        try (
            FileReader reader = new FileReader(path);
            FileWriter writer = new FileWriter(backup);
        ) {
            //备份原文件
            boolean isAutoBackupPubspec = ServiceManager.getService(Settings.class).isAutoBackupPubspec();
            if(isAutoBackupPubspec){
                IOUtils.copy(reader, writer);
            }
            //修改配置文件
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(new FileInputStream(path));
            if (handler != null) {
                handler.handle(map);
            }
            yaml.dump(map, new FileWriter(path));
        } catch (Exception ignored) {
        }
    }

    public static PubSpecConfig getPubSpecConfig(Project project) {
        return com.ruiyu.helper.YamlHelper.getPubSpecConfig(project);
    }

    public static String getName(Project project) {
        return Objects.requireNonNull(getPubSpecConfig(project)).getName();
    }

    public static boolean shouldActivateFor(Project project) {
        return com.ruiyu.helper.YamlHelper.shouldActivateFor(project);
    }

    public static interface YamlHandler {
        void handle(Map<String, Object> map);
    }
}
