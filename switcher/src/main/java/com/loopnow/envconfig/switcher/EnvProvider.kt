package com.loopnow.envconfig.switcher

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject
import kotlin.system.exitProcess

class EnvProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val env = JSONObject(Env.envMap.toMap()).toString()
        return MatrixCursor(arrayOf("env")).apply {
            addRow(arrayOf(env))
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        values ?: return null
        Env.changeEnv(values.getAsString("env"))
        Handler(Looper.getMainLooper()).postDelayed({
            Runtime.getRuntime().exit(0);
        }, 500)
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Env.initPackaged(true)
        Handler(Looper.getMainLooper()).postDelayed({
            Runtime.getRuntime().exit(0);
        }, 500)
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        values ?: return 0
        val env = values.getAsString("env")
        Log.d("EnvConfig", "Env Provider change env $env")
        Env.changeEnvByJson(env)
        Handler(Looper.getMainLooper()).postDelayed({
            Runtime.getRuntime().exit(0);
        }, 500)
        return 0
    }
}