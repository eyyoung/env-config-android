package com.loopnow.envconfig.switcher

import android.app.Application
import android.content.Context

object EnvInitializer {

    lateinit var application: Application

    fun init(context: Context): String {
        application = context.applicationContext as Application
        Env.initPackaged()
        Env.initSavedEnv()
        return "env"
    }

}