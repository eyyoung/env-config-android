package com.loopnow.envconfig.switcher

import android.content.Context
import android.content.pm.PackageManager
import org.json.JSONObject
import java.lang.IllegalArgumentException

fun Context.getTargetProviderFilter(): String? {
    return packageManager.getApplicationInfo(
        packageName,
        PackageManager.GET_META_DATA
    ).metaData.getString("com.loopnow.envconfig.target.filter")
}

object Env {

    const val PREF_ENV = "env"

    val envMap = mutableMapOf<String, String>()
    val attrKey = mutableMapOf<Int, String>()
    val keyAttr = mutableMapOf<String, Int>()

    fun getEnv(attr: Int): String {
        return envMap[attrKey[attr]] ?: throw IllegalArgumentException("Env not exist")
    }

    fun changeEnv(env: String) {
        val context = EnvInitializer.application
        val envJsonString = getJsonFromAssets(env)
        changeEnvByJson(envJsonString)
    }

    fun getJsonFromAssets(env: String): String {
        val context = EnvInitializer.application
        return context
            .assets
            .open("env/${env}.json")
            .bufferedReader()
            .use {
                it.readText()
            }
    }

    fun changeEnvByJson(envJsonString: String) {
        val context = EnvInitializer.application
        val jsonObject = JSONObject(envJsonString)
        jsonObject.keys().forEach {
            envMap[it] = jsonObject.optString(it)
        }
        context.getSharedPreferences(PREF_ENV, Context.MODE_PRIVATE).edit()
            .apply {
                putString("env", JSONObject(envMap as Map<*, *>).toString())
            }
            .apply()
    }

    fun getEnvList(): List<String> {
        return EnvInitializer.application
            .assets
            .list("env")
            ?.toList()
            ?.map {
                it.replace(".json", "")
            }
            ?: emptyList()
    }

    fun initPackaged(deletePref: Boolean = false) {
        val context = EnvInitializer.application
        val obtainStyledAttributes = context.theme.obtainStyledAttributes(
            com.loopnow.envconfig.schema.R.style.EnvironmentValue,
            com.loopnow.envconfig.schema.R.styleable.EnvironmentVariableSchema
        )
        val indexCount = obtainStyledAttributes.indexCount
        (0 until indexCount).forEach {
            val peekValue = obtainStyledAttributes.peekValue(it)
            val id = com.loopnow.envconfig.schema.R.styleable.EnvironmentVariableSchema[it]
            val resourceName =
                context.resources.getResourceName(id)
                    .replace("${context.packageName}:attr/", "")
            Env.attrKey[id] = resourceName
            Env.keyAttr[resourceName] = id
            Env.envMap[resourceName] = peekValue.string.toString()
        }
        if (deletePref) {
            context.getSharedPreferences(PREF_ENV, Context.MODE_PRIVATE)
                .edit()
                .remove("env")
                .apply()
        }
    }

    fun initSavedEnv(): String? {
        val context = EnvInitializer.application
        return context.getSharedPreferences(PREF_ENV, Context.MODE_PRIVATE)
            .getString("env", null)
            ?.apply {
                val jsonObject = JSONObject(this)
                jsonObject.keys().forEach {
                    envMap[it] = jsonObject.optString(it)
                }
            }
    }

}