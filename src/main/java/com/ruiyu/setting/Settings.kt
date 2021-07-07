package com.ruiyu.setting

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "FlutterJsonBeanFactorySettings", storages = [(Storage("FlutterJsonBeanFactorySettings.xml"))])
data class Settings(
    var modelSuffix: String,
    var isOpenNullSafety: Boolean?,
    var isOpenNullAble: Boolean?,
    var isInnerClass: Boolean?,
    var isAutoBackupPubspec: Boolean = true
) : PersistentStateComponent<Settings> {

    constructor() : this(
        "entity", null, null,null
    )

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
