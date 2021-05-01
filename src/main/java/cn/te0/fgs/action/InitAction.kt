package cn.te0.fgs.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.ruiyu.helper.YamlHelper
import com.ruiyu.utils.showNotify

class InitAction : AnAction("JsonToDartBeanAction") {

    init {
        templatePresentation.apply {
            text = "Init"
            description = "Initialize a GetX based Flutter project"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        var project: Project = e.getData(PlatformDataKeys.PROJECT)!!
        val pubSpecConfig = YamlHelper.getPubSpecConfig(project)
        //判断是否是flutter项目
        if (YamlHelper.shouldActivateFor(project)) {
            print("It is flutter project");
        } else {
            project.showNotify("This project is not the flutter project or the flutterJson in pubspec.yaml with the enable set to false")
        }
    }
}