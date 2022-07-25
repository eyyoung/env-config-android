package com.loopnow.envconfig.switcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import com.loopnow.envconfig.switcher.databinding.EnvActivitySwitcherBinding

class EnvSwitchActivity : AppCompatActivity() {

    private lateinit var binding: EnvActivitySwitcherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = EnvActivitySwitcherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportFragmentManager.commit {
            replace(R.id.container, SwitchFragment.newInstance())
        }

    }

}