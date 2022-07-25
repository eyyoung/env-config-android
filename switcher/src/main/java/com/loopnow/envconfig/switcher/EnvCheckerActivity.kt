package com.loopnow.envconfig.switcher

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopnow.envconfig.switcher.databinding.EnvActivityEnvCheckerBinding
import org.json.JSONObject


class EnvCheckerActivity : AppCompatActivity() {

    private lateinit var targetAuth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = EnvActivityEnvCheckerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val queryIntentContentProviders =
            packageManager.queryIntentContentProviders(
                Intent("com.loopnow.envconfig"),
                PackageManager.GET_META_DATA
            )
        queryIntentContentProviders.first()?.let {
            targetAuth = it.providerInfo.authority
            binding.tvTargetProcess.text = "Target Process: \n${it.providerInfo.packageName}"
        }
        val cursor = contentResolver.query(
            Uri.parse("content://${targetAuth}/env"), null, null, null,
            null
        )?.use {
            val columnIndex = it.getColumnIndex("env")
            it.moveToFirst()
            val result = it.getString(columnIndex)
            val spacesToIndentEachLevel = 4
            binding.etConfig.setText(JSONObject(result).toString(spacesToIndentEachLevel))
        }
        binding.btnApply.setOnClickListener {
            val update = contentResolver.update(
                Uri.parse("content://${targetAuth}/env"),
                ContentValues().apply {
                    put("env", binding.etConfig.text.toString())
                },
                null,
                null,
            )
            if (update == 0) {
                Toast.makeText(
                    this,
                    R.string.env_change_success, Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnClear.setOnClickListener {
            val delete = contentResolver.delete(
                Uri.parse("content://${targetAuth}/env"), null, null
            )
            if (delete == 0) {
                Toast.makeText(
                    this,
                    R.string.env_change_success, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}