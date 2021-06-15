package com.ruiyu.jsontodart

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.psi.DartFile
import com.ruiyu.beanfactory.FlutterBeanFactoryAction
import com.ruiyu.file.FileHelpers
import com.ruiyu.helper.YamlHelper
import com.ruiyu.setting.Settings
import com.ruiyu.ui.JsonInputDialog
import com.ruiyu.utils.*

class JsonToDartBeanAction : AnAction("JsonToDartBeanAction") {

    init {
        templatePresentation.apply {
            text = "Dart Bean From JSON..."
            description = "Generate dart bean class File from JSON"
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return

        val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)
        val directory = when (navigatable) {
            is PsiDirectory -> navigatable
            is PsiFile -> navigatable.containingDirectory
            else -> {
                val root = ModuleRootManager.getInstance(module)
                root.sourceRoots.asSequence().mapNotNull {
                    PsiManager.getInstance(project).findDirectory(it)
                }.firstOrNull()
            }
        } ?: return
        try {
            JsonInputDialog(project) { collectInfo ->
                //生成dart文件的内容
                val classes = ModelGenerator(collectInfo, project).generateDartClasses()
                //用户设置的后缀
                val suffix = ServiceManager.getService(Settings::class.java).state.modelSuffix.toLowerCase()
                //文件是否存在的校验,如果包含那么就提示
                for (clazz in classes) {
                    val fileName = generateDartClassFileName(clazz.name)
                    if (FileHelpers.containsDirectoryFile(directory, "$fileName.dart")) {
                        project.showErrorMessage("The $fileName.dart already exists")
                        return@JsonInputDialog false
                    }
                    if (FileHelpers.containsProjectFile(project, "$fileName.dart")) {
                        project.showErrorMessage("$fileName.dart already exists in other package")
                        return@JsonInputDialog false
                    }
                }
                //文件生成
                val isInnerClass = ServiceManager.getService(Settings::class.java).isInnerClass == true
                for (clazz in classes) {
                    val classContent = if (isInnerClass) classes.joinToString("\n") else clazz.toString()
                    val dependencies: List<Dependency> = if (isInnerClass) listOf() else clazz.dependencies
                    if (!generateDartClassFile(clazz.name, classContent, project, directory, dependencies)) {
                        return@JsonInputDialog false
                    }
                    //如果是内部类则循环一次就够了
                    if (isInnerClass) {
                        break
                    }
                }
                //生成helper辅助类
                FlutterBeanFactoryAction.generateAllFile(project)
                project.showNotify("Dart Data Class file generated successful")
                true
            }.show()
        } catch (e: Exception) {
            project.showErrorMessage(e.message!!)
        }
    }

    private fun generateDartClassFileName(className: String): String {
        //用户设置的后缀
        val suffix = ServiceManager.getService(Settings::class.java).state.modelSuffix.toLowerCase()
        return if (!className.contains("_")) {
            (className + suffix.toUpperCaseFirstOne()).upperCharToUnderLine()
        } else {
            (className + "_" + suffix)
        }
    }

    private fun generateDartClassFile(
        className: String,
        classContent: String,
        project: Project,
        directory: PsiDirectory,
        dependencies: List<Dependency> = listOf()
    ): Boolean {
        val fileName = generateDartClassFileName(className)
        val sb = StringBuilder()
        val pubSpecConfig = YamlHelper.getPubSpecConfig(project)
        //导包
        sb.append("import 'package:${pubSpecConfig?.name}/gen/json/base/json_convert_content.dart';").append("\n")
        //说明需要导包json_field.dart
        if (classContent.contains("@JSONField(")) {
            sb.append("import 'package:${pubSpecConfig?.name}/gen/json/base/json_field.dart';").append("\n")
        }
        //导入依赖包
        for (dependency in dependencies) {
            val packageName: String = directory.virtualFile.path.substringAfter("${project.name}/lib/")
            sb.append("import 'package:${pubSpecConfig?.name}/$packageName/")
                .append(generateDartClassFileName(dependency.className)).append(".dart';").append("\n")
        }
        sb.append("\n").append(classContent)
        //生成文件
        project.executeCouldRollBackAction {
            val file = PsiFileFactory.getInstance(project).createFileFromText(
                "$fileName.dart", DartFileType.INSTANCE, sb.toString()
            ) as DartFile
            directory.add(file)
        }
        return true
    }
}