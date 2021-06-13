package com.ruiyu.jsontodart

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
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
import com.ruiyu.ui.JsonInputDialog
import com.ruiyu.utils.executeCouldRollBackAction
import com.ruiyu.utils.showErrorMessage
import com.ruiyu.utils.showNotify

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
                //文件名字
                val fileName = collectInfo.transformInputClassNameToFileName()
                when {
                    //如果包含那么就提示
                    FileHelpers.containsDirectoryFile(directory, "$fileName.dart") -> {
                        project.showErrorMessage("The $fileName.dart already exists")
                        false
                    }
                    FileHelpers.containsProjectFile(project, "$fileName.dart") -> {
                        project.showErrorMessage("$fileName.dart already exists in other package")
                        false
                    }
                    else -> {
                        //生成dart文件的内容
                        val generatorClassContent = ModelGenerator(collectInfo, project).generateDartClassesToString()
                        generateDartDataClassFile(fileName, generatorClassContent, project, directory)
                        FlutterBeanFactoryAction.generateAllFile(project)
                        project.showNotify("Dart Data Class file generated successful")
                        true
                    }
                }
            }.show()
        } catch (e: Exception) {
            project.showErrorMessage(e.message!!)
        }
    }

    private fun generateDartDataClassFile(
        fileName: String,
        classCodeContent: String,
        project: Project?,
        directory: PsiDirectory
    ) {
        project.executeCouldRollBackAction {
            val file = PsiFileFactory.getInstance(project).createFileFromText(
                "$fileName.dart", DartFileType.INSTANCE, classCodeContent
            ) as DartFile
            directory.add(file)
        }
    }
}