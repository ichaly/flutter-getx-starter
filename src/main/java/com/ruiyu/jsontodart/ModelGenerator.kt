package com.ruiyu.jsontodart

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.project.Project
import com.ruiyu.jsontodart.utils.camelCase
import com.ruiyu.utils.GsonUtil.MapTypeAdapter
import com.ruiyu.utils.JsonUtils


class ModelGenerator(
    val collectInfo: CollectInfo,
    val project: Project
) {
    var allClasses = mutableListOf<ClassDefinition>()

    //parentType 父类型 是list 或者class
    private fun generateClassDefinition(
        className: String,
        jsonRawData: Any,
    ): MutableList<ClassDefinition> {
        if (jsonRawData is List<*>) {
            // if first element is an array, start in the first element.
            generateClassDefinition(className, jsonRawData[0]!!)
        } else if (jsonRawData is Map<*, *>) {
            val keys = jsonRawData.keys
            //如果是list,就把名字修改成单数
            val classDefinition = ClassDefinition(className)
            keys.forEach { key ->
                val typeDef = TypeDefinition.fromDynamic(jsonRawData[key])
                if (typeDef.name == "Class") {
                    typeDef.name = camelCase(key as String)
                }
                if (typeDef.subtype != null && typeDef.subtype == "Class") {
                    typeDef.subtype = camelCase(key as String)
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
                        generateClassDefinition(dependency.className, names[0]!!)
                    }
                } else {
                    generateClassDefinition(dependency.className, jsonRawData[dependency.name]!!)
                }
            }
        }
        return allClasses
    }

    fun generateDartClasses(): List<ClassDefinition> {
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
        return generateClassDefinition(collectInfo.firstClassName(), jsonRawData!!)
    }
}