package com.loopnow.envconfig.switcher

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

object EnvInitializer : Initializer<String> {

    lateinit var application: Application

    override fun create(context: Context): String {
        application = context.applicationContext as Application
        Env.initPackaged()
        Env.initSavedEnv()
        return "env"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}