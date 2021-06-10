package cn.te0.flutter.action

import cn.te0.flutter.helper.GetXConfig
import cn.te0.flutter.helper.TemplateHelper
import cn.te0.flutter.helper.ViewHelper
import cn.te0.flutter.helper.YamlHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.ruiyu.file.FileHelpers
import com.ruiyu.utils.showNotify
import io.flutter.pub.PubRoot
import java.io.File
import java.net.URLDecoder
import java.util.jar.JarFile

class InitAction : AnAction() {

    init {
        templatePresentation.apply {
            text = "Initialize Project"
            description = "Initialize a GetX based Flutter project templates"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project!!
        //判断是否是flutter项目
        if (YamlHelper.shouldActivateFor(project)) {
            generateStructure(project)
        } else {
            project.showNotify("This project is not the flutter project or the flutterJson in pubspec.yaml with the enable set to false")
        }
    }

    //初始化项目框架结构
    private fun generateStructure(project: Project) {
        ApplicationManager.getApplication().runWriteAction {
            PubRoot.forFile(FileHelpers.getProjectIdeaFile(project))?.run {
                //初始化资源文件目录
                root.findChild("assets") ?: root.createChildDirectory(this, "assets").run {
                    findChild("image") ?: createChildDirectory(this, "image")//图片资源
                    findChild("font") ?: createChildDirectory(this, "font")//字体资源
                    findChild("json") ?: createChildDirectory(this, "json")//Json资源
                    findChild("anim") ?: createChildDirectory(this, "anim")//动画资源
                    findChild("i18n") ?: createChildDirectory(this, "i18n")//国际化资源
                }
                //添加常用依赖
                YamlHelper.updateYaml(
                    pubspec.path
                ) { map: Map<String?, Any?> ->
                    (map.get("dependencies") as HashMap<String, Any>).run {
                        putIfAbsent("get", "^4.1.4")
                        putIfAbsent("lottie", "^1.0.1")
                        putIfAbsent("crypto", "^3.0.1")
                        putIfAbsent("convert", "^3.0.0")
                        putIfAbsent("fluttertoast", "^8.0.7")
                        putIfAbsent("flutter_screenutil", "^5.0.0+2")
                        putIfAbsent("dio_cookie_manager", "^2.0.0")
                    }
                }
                lib?.run {
                    //初始化插件生成的文件目录
                    findChild("gen") ?: createChildDirectory(this, "gen").run {
                        findChild("json") ?: createChildDirectory(this, "json")
                        findChild("res") ?: createChildDirectory(this, "res")
                    }
                    //初始化app目录
                    findChild("app") ?: createChildDirectory(this, "app").run {
                        //添加通用工具类
                        val jf = JarFile(
                            URLDecoder.decode(
                                InitAction::class.java.getResource("/templates")?.file
                                    ?.replace("!/templates", "")
                                    ?.replace("file:", "")
                            )
                        )
                        for (entry in jf.entries()) {
                            if (!entry.name.startsWith("templates/getx/common/")) {
                                continue
                            }
                            val target = this.path + entry.name.replace("templates/getx", "")
                            if (entry.isDirectory) {
                                val file = File(target)
                                if (!file.exists()) {
                                    file.mkdirs()
                                }
                                continue
                            }
                            val map: Map<String?, Any?> = HashMap()
                            TemplateHelper.getInstance().generator(
                                entry.name.replace("templates/", ""), target, map
                            )
                        }
                        //初始化目录结构
                        findChild("entity") ?: createChildDirectory(this, "entity")
                        findChild("views") ?: createChildDirectory(this, "views")
                        findChild("pages") ?: createChildDirectory(this, "pages").run {
                            //创建一个默认的Home页面
                            GetXConfig.isPage = true
                            GetXConfig.autoDispose = true
                            ViewHelper.getInstance().createView(project, "Home", path)
                        }
                    }
                    //初始化main.dart
                }
            }
            project.showNotify("Project is initialized successfully.")
        }
    }
}