package cn.te0.fgs.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class InitAction : AnAction("JsonToDartBeanAction"){

    init {
        templatePresentation.apply {
            text = "Init"
            description = "Initialize a GetX based Flutter project"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}