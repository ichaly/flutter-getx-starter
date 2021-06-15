package com.ruiyu.jsontodart

import com.ruiyu.utils.toUpperCaseFirstOne
import com.ruiyu.utils.upperTable

class CollectInfo {
    //用户输入的类名
    var userInputClassName = ""
    var userInputJson = ""

    //用户输入的名字转为首个class的名字(文件中的类名)
    fun firstClassName(): String {
        return if (userInputClassName.contains("_")) {
            (upperTable(userInputClassName)).toUpperCaseFirstOne()
        } else {
            (userInputClassName).toUpperCaseFirstOne()
        }
    }

    //用户输入的名字转为首个class的名字(文件中的类名)
    fun firstClassEntityName(): String {
        return if (userInputClassName.contains("_")) {
            upperTable(userInputClassName).toUpperCaseFirstOne()
        } else {
            userInputClassName.toUpperCaseFirstOne()
        }
    }
}