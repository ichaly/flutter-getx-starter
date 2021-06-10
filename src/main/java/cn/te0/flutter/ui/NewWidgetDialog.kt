package cn.te0.flutter.ui

import cn.te0.flutter.helper.GetXConfig
import cn.te0.flutter.helper.ViewHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ValidationInfo
import org.apache.commons.lang3.StringUtils
import javax.swing.JComponent

/**
 * @author Chaly
 * new NewWidgetDialog().show();
 */
class NewWidgetDialog(
    private val project: Project,
    private val basePath: String,
) : DialogWrapper(true) {
    private val form: NewWidgetForm

    init {
        title = "GetX Template Code Produce"
        form = NewWidgetForm()
        setSize(460, 360)
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return form.root
    }

    override fun doValidate(): ValidationInfo? {
        val text = form.name
        return if (StringUtils.isNotBlank(text)) {
            null
        } else {
            ValidationInfo("校验不通过")
        }
    }

    override fun doOKAction() {
        super.doOKAction()
        if (StringUtils.isBlank(form.name)) {
            Messages.showInfoMessage(project, "Please input the module name", "Info")
            return
        }
        close(CLOSE_EXIT_CODE)
        //deal default value
        GetXConfig.defaultMode = form.isDefaultMode
        GetXConfig.useFolder = form.isUseFolder
        GetXConfig.usePrefix = form.isUsePrefix
        GetXConfig.autoDispose = form.isAuto
        GetXConfig.isPage = form.isPage
        ViewHelper.getInstance().createView(project, form.name, basePath)
    }
}