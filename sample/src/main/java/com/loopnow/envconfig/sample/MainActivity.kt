package com.loopnow.envconfig.sample

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.loopnow.envconfig.switcher.Env
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tvEnv).text =
            Env.getEnv(com.loopnow.env.config.sample.schema.R.attr.baseUrl)
        findViewById<TextView>(R.id.tvEnv).setOnClickListener {
            try {
                val componentName =
                    ComponentName(this, "com.loopnow.envconfig.switcher.EnvSwitchActivity")
                startActivity(Intent().setComponent(componentName))
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }
        }
    }
}