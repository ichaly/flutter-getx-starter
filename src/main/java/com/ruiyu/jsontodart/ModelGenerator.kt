package com.ruiyu.jsontodart

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.project.Project
import com.ruiyu.helper.YamlHelper
import com.ruiyu.jsontodart.utils.camelCase
import com.ruiyu.utils.GsonUtil.MapTypeAdapter
import com.ruiyu.utils.JsonUtils
import com.ruiyu.utils.toUpperCaseFirstOne


class ModelGenerator(
    val collectInfo: CollectInfo,
    val project: Project
) {
    var isFirstClass = true
    var allClasses = mutableListOf<ClassDefinition>()

    //parentType 父类型 是list 或者class
    private fun generateClassDefinition(
        className: String,
        parentName: String,
        jsonRawData: Any,
        parentType: String = ""
    ): MutableList<ClassDefinition> {
        val newClassName = parentName + className
        val preName = newClassName
        if (jsonRawData is List<*>) {
            // if first element is an array, start in the first element.
            generateClassDefinition(newClassName, newClassName, jsonRawData[0]!!)
        } else if (jsonRawData is Map<*, *>) {
            val keys = jsonRawData.keys
            //如果是list,就把名字修改成单数
            val classDefinition = ClassDefinition(
                when {
                    "list" == parentType -> {
                        newClassName
                    }
                    isFirstClass -> {//如果是第一个类
                        isFirstClass = false
                        newClassName + collectInfo.modelSuffix().toUpperCaseFirstOne()
                    }
                    else -> {
                        newClassName
                    }
                }
            )
            keys.forEach { key ->
                val typeDef = TypeDefinition.fromDynamic(jsonRawData[key])
                if (typeDef.name == "Class") {
                    typeDef.name = preName + camelCase(key as String)
                }
                if (typeDef.subtype != null && typeDef.subtype == "Class") {
                    typeDef.subtype = preName + camelCase(key as String)
                }
                classDefinition.addField(key as String, typeDef)
            }
            if (allClasses.firstOrNull { cd -> cd == classDefinition } == null) {
                allClasses.add(classDefinition)
            }
            val dependencies = classDefinition.dependencies
            dependencies.forEach { dependency ->
                if (dependency.typeDef.name == "List") {
                    if (((jsonRawData[dependency.name]) as? List<*>)?.isNotEmpty() == true) {
                        val names = (jsonRawData[dependency.name] as List<*>)
                        generateClassDefinition(dependency.className, newClassName, names[0]!!, "list")
                    }
                } else {
                    generateClassDefinition(dependency.className, newClassName, jsonRawData[dependency.name]!!)
                }
            }
        }
        return allClasses
    }

    fun generateDartClassesToString(): String {
        val originalStr = collectInfo.userInputJson.trim()
        val gson = GsonBuilder().registerTypeAdapter(
            object : TypeToken<Map<String, Any>>() {}.type, MapTypeAdapter()
        ).create()
        val jsonRawData = try {
            if (originalStr.startsWith("[")) {
                val list: List<Any> = gson.fromJson(originalStr, object : TypeToken<List<Any>>() {}.type)
                (JsonUtils.jsonMapMCompletion(list) as List<*>).first()
            } else {
                gson.fromJson<Map<String, Any>>(originalStr, object : TypeToken<Map<String, Any>>() {}.type)
            }
        } catch (e: Exception) {
            mutableMapOf<String, Any>()
        }
        val pubSpecConfig = YamlHelper.getPubSpecConfig(project)
        val classContentList = generateClassDefinition(collectInfo.firstClassName(), "", jsonRawData!!)
        val classContent = classContentList.joinToString("\n")
        val stringBuilder = StringBuilder()
        //导包
        stringBuilder.append("import 'package:${pubSpecConfig?.name}/gen/json/base/json_convert_content.dart';")
        stringBuilder.append("\n")
        //说明需要导包json_field.dart
        if (classContent.contains("@JSONField(")) {
            stringBuilder.append("import 'package:${pubSpecConfig?.name}/gen/json/base/json_field.dart';")
            stringBuilder.append("\n")
        }
        stringBuilder.append("\n")
        stringBuilder.append(classContent)
        //生成
        return stringBuilder.toString()
    }
}