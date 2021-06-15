package cn.te0.flutter.action

import cn.te0.flutter.ui.NewWidgetDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiElement

class ViewAction : AnAction() {
    init {
        templatePresentation.text = "New View/Page..."
    }

    override fun actionPerformed(event: AnActionEvent) {
        val basePath: PsiElement? = event.getData(PlatformDataKeys.PSI_ELEMENT)
        NewWidgetDialog(
            event.project!!, basePath.toString().split(":".toRegex()).toTypedArray()[1]
        ).show()
    }
}