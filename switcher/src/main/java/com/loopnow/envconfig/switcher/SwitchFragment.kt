package com.loopnow.envconfig.switcher

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopnow.envconfig.switcher.databinding.EnvFragmentSwithcerBinding
import com.loopnow.envconfig.switcher.databinding.EnvItemEnvBinding

class SwitchFragment : Fragment() {

    companion object {
        fun newInstance(): SwitchFragment {
            return SwitchFragment()
        }
    }

    private lateinit var targetProcess: String
    private lateinit var targetAuth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EnvFragmentSwithcerBinding.inflate(layoutInflater, container, false)
        val assets = requireContext().assets
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val envList = (assets
            .list("env")
            ?.toList()
            ?.map {
                it.replace(".json", "")
            }
            ?: emptyList())
        binding.rv.adapter = EnvAdapter(envList)
            .apply {
                clickListener = { env ->
                    val contentResolver = requireContext().contentResolver
                    val update = contentResolver.update(
                        Uri.parse("content://${targetAuth}/env"),
                        ContentValues().apply {
                            put("env", getJsonFromAssets(requireContext(), env))
                        },
                        null,
                        null,
                    )
                    if (update == 0) {
                        Toast.makeText(
                            requireContext(),
                            R.string.env_change_success, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        val queryIntentContentProviders =
            requireContext().packageManager.queryIntentContentProviders(
                Intent(requireContext().getTargetProviderFilter()),
                PackageManager.GET_META_DATA
            )
        queryIntentContentProviders.first()?.let {
            targetAuth = it.providerInfo.authority
            targetProcess = it.providerInfo.packageName
        }
        return binding.root
    }

    private fun getJsonFromAssets(context: Context, env: String): String {
        return context.assets
            .open("env/${env}.json")
            .bufferedReader()
            .use {
                it.readText()
            }
    }

    private class EnvHolder(val itemEnvBinding: EnvItemEnvBinding) :
        RecyclerView.ViewHolder(itemEnvBinding.root) {

        fun bind(env: String) {
            itemEnvBinding.tvEnv.text = env
        }

    }

    private class EnvAdapter(val list: List<String>) : RecyclerView.Adapter<EnvHolder>() {

        var clickListener: ((env: String) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvHolder {
            return EnvHolder(
                EnvItemEnvBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: EnvHolder, position: Int) {
            holder.bind(list[position])
            holder.itemView.setOnClickListener {
                clickListener?.invoke(list[position])
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.add("Check")
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(requireContext(), EnvCheckerActivity::class.java))
        return true
    }

}