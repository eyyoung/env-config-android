package com.loopnow.envconfig.sample

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.loopnow.envconfig.switcher.Env

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tvEnv).text =
            Env.getEnv(com.loopnow.envconfig.switchsample.R.attr.baseUrl)
        findViewById<TextView>(R.id.tvEnv).setOnClickListener {
            val componentName =
                ComponentName(this, "com.loopnow.envconfig.switcher.EnvSwitchActivity")
            startActivity(Intent().setComponent(componentName))
        }
    }
}