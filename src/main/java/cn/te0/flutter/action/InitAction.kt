package cn.te0.flutter.action

import cn.te0.flutter.helper.ViewHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.ruiyu.file.FileHelpers
import com.ruiyu.helper.YamlHelper
import com.ruiyu.utils.showNotify
import io.flutter.pub.PubRoot

class InitAction : AnAction() {

    init {
        templatePresentation.apply {
            text = "Initialize Project"
            description = "Initialize a GetX based Flutter project templates"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.getData(PlatformDataKeys.PROJECT)!!
        YamlHelper.getPubSpecConfig(project)
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
                lib?.run {
                    //初始化插件生成的文件目录
                    findChild("gen") ?: createChildDirectory(this, "gen").run {
                        findChild("json") ?: createChildDirectory(this, "json")
                        findChild("res") ?: createChildDirectory(this, "res")
                    }
                    //初始化app目录
                    findChild("app") ?: createChildDirectory(this, "app").run {
                        findChild("base") ?: createChildDirectory(this, "base")
                        findChild("utils") ?: createChildDirectory(this, "utils")
                        findChild("entity") ?: createChildDirectory(this, "entity")
                        findChild("routes") ?: createChildDirectory(this, "routes")
                        findChild("views") ?: createChildDirectory(this, "views").run {
                            //创建一个home的默认模块
                            ViewHelper.getInstance().createView("Home", path)
                        }
                    }
                    //初始化main.dart
                }
            }
            project.baseDir.refresh(false, true)
        }
    }
}