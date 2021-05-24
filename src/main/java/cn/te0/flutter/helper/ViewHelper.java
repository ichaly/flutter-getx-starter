package cn.te0.flutter.helper;

import cn.te0.flutter.utils.Utils;
import com.google.common.base.CaseFormat;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chaly
 */
public class ViewHelper {
    private DataService data;

    private static class SingletonHolder {
        private static ViewHelper instance = new ViewHelper();
    }

    private ViewHelper() {
        data = DataService.getInstance();
    }

    public static ViewHelper getInstance() {
        return SingletonHolder.instance;
    }

    public void createView(String name, String folder) {
        String prefix = "";
        String tmp = Utils.toUnderline(name);
        if (data.useFolder) {
            folder = folder + "/" + tmp;
        }
        if (data.usePrefix) {
            prefix = tmp + "_";
        }
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        Map<String, Object> map = new HashMap();
        map.put("Utils", TemplateHelper.useStatic(Utils.class.getName()));
        map.put("name", name);
        map.put("pageName", "Page");
        map.put("viewName", "View");
        map.put("isPage", DataService.getInstance().isPage);
        map.put("useFolder", DataService.getInstance().useFolder);
        map.put("usePrefix", DataService.getInstance().usePrefix);
        map.put("autoDispose", DataService.getInstance().autoDispose);
        map.put("defaultMode", DataService.getInstance().defaultMode);

        TemplateHelper.getInstance().generator(
            "getx/weiget.dart.ftl", String.format("%s/%s%s.dart", folder, prefix, data.isPage ? "page" : "view"), map
        );
        TemplateHelper.getInstance().generator(
            "getx/logic.dart.ftl", String.format("%s/%slogic.dart", folder, prefix), map
        );
        if (DataService.getInstance().defaultMode) {
            TemplateHelper.getInstance().generator(
                "getx/state.dart.ftl", String.format("%s/%sstate.dart", folder, prefix), map
            );
        }
    }

    private void generateDefault(String name, String folder, String prefixName) {
        generateFile(name, folder, "state.dart", prefixName + "state.dart");
        generateFile(name, folder, "logic.dart", prefixName + "logic.dart");
        if (data.isPage) {
            generateFile(name, folder, "page.dart", prefixName + "page.dart");
            generateFile(name, folder, "binding.dart", prefixName + "binding" + ".dart");
        } else {
            generateFile(name, folder, "view.dart", prefixName + "view.dart");
        }
    }

    private void generateEasy(String name, String folder, String prefixName) {
        generateFile(name, folder, "easy/logic.dart", prefixName + "logic.dart");
        if (data.isPage) {
            generateFile(name, folder, "binding.dart", prefixName + "binding" + ".dart");
            generateFile(name, folder, "easy/page.dart", prefixName + "page.dart");
        } else {
            generateFile(name, folder, "easy/view.dart", prefixName + "view.dart");
        }
    }

    private void generateFile(String name, String filePath, String inputFileName, String outFileName) {
        //content deal
        String content = dealContent(name, inputFileName, outFileName);
        //Write file
        try {
            File folder = new File(filePath);
            // if file doesnt exists, then create it
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(filePath + "/" + outFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String dealContent(String name, String inputFileName, String outFileName) {
        //deal auto dispose
        String autoDispose = "";
        if (data.autoDispose) {
            autoDispose = "auto/";
        }
        //read file
        String content = "";
        try {
            InputStream in = this.getClass().getResourceAsStream("/templates/getx/" + autoDispose + inputFileName);
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        String prefixName = "";
        //Adding a prefix requires modifying the imported class name
        if (data.usePrefix) {
            prefixName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name) + "_";
        }
        content = content.replaceAll("logic.dart", prefixName + "logic.dart");
        content = content.replaceAll("state.dart", prefixName + "state.dart");
        content = content.replaceAll("\\$name", name);
        return content;
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }
        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }
}
