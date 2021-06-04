package com.ruiyu.ui

import com.google.gson.*
import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBEmptyBorder
import com.ruiyu.jsontodart.CollectInfo
import com.ruiyu.setting.Settings
import com.ruiyu.utils.addComponentIntoVerticalBoxAlignmentLeft
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.net.MalformedURLException
import java.net.URL
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.text.JTextComponent

/**
 * Dialog widget relative
 * Created by Seal.wu on 2017/9/21.
 */

object UrlInputValidator : InputValidator {
    override fun checkInput(inputString: String): Boolean = try {
        URL(inputString)
        true
    } catch (e: MalformedURLException) {
        false
    }

    override fun canClose(inputString: String): Boolean = true
}

class MyInputValidator : InputValidator {
    lateinit var document: Document
    override fun checkInput(inputString: String): Boolean {
        return try {
            val classNameLegal = inputString.trim().isNotBlank()
            val jsonElement = JsonParser.parseString(document.text)
            (jsonElement.isJsonObject || jsonElement.isJsonArray) && classNameLegal
        } catch (e: JsonSyntaxException) {
            false
        }
    }

    override fun canClose(inputString: String): Boolean {
        return true
    }
}

val myInputValidator = MyInputValidator()

/**
 * Json input Dialog
 */
open class JsonInputDialog(
    private val project: Project,
    val inputModelBlock: (inputModel: CollectInfo) -> Boolean
) : Messages.InputDialog(
    project,
    "Please input the class name and json string for generating dart bean class",
    "Generate Dart Bean Class Code",
    Messages.getInformationIcon(),
    "",
    myInputValidator
) {
    private lateinit var jsonContentEditor: Editor

    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    init {
        setOKButtonText("Generate")
    }

    override fun createMessagePanel(): JPanel {
        val messagePanel = JPanel(BorderLayout())
        if (myMessage != null) {
            val textComponent = createTextComponent()
            messagePanel.add(textComponent, BorderLayout.NORTH)
        }
        myField = createTextFieldComponent()
        jsonContentEditor = createJsonContentEditor()
        myInputValidator.document = jsonContentEditor.document

        val classNameInputContainer = createLinearLayoutVertical()
        val classNameTitle = JBLabel("Class Name: ")
        classNameTitle.border = JBEmptyBorder(5, 0, 5, 0)
        classNameInputContainer.addComponentIntoVerticalBoxAlignmentLeft(classNameTitle)

        myField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                revalidate()
            }
        })

        val formatButton = JButton("Format")
        formatButton.horizontalAlignment = SwingConstants.CENTER
        formatButton.addActionListener(object : AbstractAction() {
            override fun actionPerformed(p0: ActionEvent?) {
                handleFormatJSONString()
            }
        })
        val settingContainer = JPanel()
        settingContainer.border = JBEmptyBorder(0, 0, 0, 0)
        val boxLayout = BoxLayout(settingContainer, BoxLayout.LINE_AXIS)
        settingContainer.layout = boxLayout
        settingContainer.add(Box.createHorizontalGlue())
        settingContainer.add(myField)
        settingContainer.add(formatButton)

        classNameInputContainer.addComponentIntoVerticalBoxAlignmentLeft(settingContainer)
        classNameInputContainer.preferredSize = JBDimension(520, 56)

        val jsonInputContainer = createLinearLayoutVertical()
        jsonInputContainer.preferredSize = JBDimension(700, 400)
        jsonInputContainer.border = JBEmptyBorder(5, 0, 5, 5)
        val jsonTitle = JBLabel("JSON Text:")
        jsonTitle.border = JBEmptyBorder(5, 0, 5, 0)
        jsonInputContainer.addComponentIntoVerticalBoxAlignmentLeft(jsonTitle)
        jsonInputContainer.addComponentIntoVerticalBoxAlignmentLeft(jsonContentEditor.component)

        val centerContainer = JPanel()
        val centerBoxLayout = BoxLayout(centerContainer, BoxLayout.PAGE_AXIS)
        centerContainer.layout = centerBoxLayout
        centerContainer.addComponentIntoVerticalBoxAlignmentLeft(classNameInputContainer)
        centerContainer.addComponentIntoVerticalBoxAlignmentLeft(jsonInputContainer)
        messagePanel.add(centerContainer, BorderLayout.CENTER)
        messagePanel.add(createCheckBox(), BorderLayout.SOUTH)

        return messagePanel
    }

    override fun createTextFieldComponent(): JTextComponent {
        return JTextField().apply {
            preferredSize = JBDimension(400, 40)
            addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent) {
                    if (e.keyChar == '˚') {
                        e.consume()
                    }
                }
            })
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return if (myField.text?.isEmpty() == false) {
            jsonContentEditor.component
        } else {
            myField
        }
    }

    fun handleFormatJSONString() {
        val currentText = jsonContentEditor.document.text
        if (currentText.isNotEmpty()) {
            try {
                val jsonElement = prettyGson.fromJson(currentText, JsonElement::class.java)
                val formatJSON = prettyGson.toJson(jsonElement)
                jsonContentEditor.document.setText(formatJSON)
            } catch (e: Exception) {
            }
        }
    }

    override fun doOKAction() {
        val collectInfo = CollectInfo().apply {
            userInputClassName = myField.text
            userInputJson = jsonContentEditor.document.text
        }
        if (collectInfo.userInputClassName.isEmpty()) {
            throw Exception("Class name must not null or empty")
        }
        if (collectInfo.userInputJson.isEmpty()) {
            throw Exception("json must not null or empty")
        }
        if (inputModelBlock(collectInfo)) {
            super.doOKAction()
        }
    }

    private fun revalidate() {
        okAction.isEnabled = myInputValidator.checkInput(myField.text)
    }

    private fun createJsonContentEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
            addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
                override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) = revalidate()

                override fun beforeDocumentChange(event: com.intellij.openapi.editor.event.DocumentEvent) = Unit
            })
        }
        val editor = editorFactory.createEditor(document, null, JsonFileType.INSTANCE, false)
        editor.component.apply {
            isEnabled = true
            preferredSize = Dimension(640, 480)
            autoscrolls = true
        }
        val contentComponent = editor.contentComponent
        contentComponent.isFocusable = true
        contentComponent.componentPopupMenu = JPopupMenu().apply {
            add(createPasteFromClipboardMenuItem())
            add(createRetrieveContentFromHttpURLMenuItem())
            add(createLoadFromLocalFileMenu())
        }
        return editor
    }

    private fun createPasteFromClipboardMenuItem() = JMenuItem("Paste from clipboard").apply {
        addActionListener {
            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                jsonContentEditor.document.setText(transferable.getTransferData(DataFlavor.stringFlavor).toString())
            }
        }
    }

    private fun createRetrieveContentFromHttpURLMenuItem() = JMenuItem("Retrieve content from Http URL").apply {
        addActionListener {
            val url = Messages.showInputDialog(null, "Retrieve content from Http URL", null, null, UrlInputValidator)
            val p = DispatchThreadProgressWindow(false, project)
            p.isIndeterminate = true
            p.setRunnable {
                try {
                    val urlContent = URL(url).readText()
                    jsonContentEditor.document.setText(urlContent.replace("\r\n", "\n"))
                } finally {
                    p.stop()
                }
            }
            p.start()
        }
    }

    private fun createLoadFromLocalFileMenu() = JMenuItem("Load from local file").apply {
        addActionListener {
            FileChooser.chooseFile(FileChooserDescriptor(true, false, false, false, false, false), null, null) { file ->
                val content = String(file.contentsToByteArray())
                ApplicationManager.getApplication().runWriteAction {
                    jsonContentEditor.document.setText(content.replace("\r\n", "\n"))
                }
            }
        }
    }
}

fun createLinearLayoutVertical(): JPanel {
    val container = JPanel()
    val boxLayout = BoxLayout(container, BoxLayout.PAGE_AXIS)
    container.layout = boxLayout
    return container
}

fun createCheckBox(): DialogPanel {
    val isOpenNullSafety = ServiceManager.getService(Settings::class.java).isOpenNullSafety == true
    val listCheckBox = mutableListOf<CellBuilder<JBCheckBox>?>(null, null, null)
    return panel {
        row {
            checkBoxGroup(null) {
                listCheckBox[0] =
                    checkBox("null-safety", isOpenNullSafety).apply {
//                        component.isSelected = true
                        component.addItemListener {
                            listCheckBox[1]?.component?.isVisible = component.isSelected
                            ServiceManager.getService(Settings::class.java).isOpenNullSafety = component.isSelected
                        }
                    }
                listCheckBox[1] =
                    checkBox(
                        "null-able",
                        isOpenNullSafety && ServiceManager.getService(Settings::class.java).isOpenNullAble == true
                    ).apply {
                        component.isVisible = isOpenNullSafety
                        component.addItemListener {
                            ServiceManager.getService(Settings::class.java).isOpenNullAble = component.isSelected
                        }
                    }
            }
        }
    }
}