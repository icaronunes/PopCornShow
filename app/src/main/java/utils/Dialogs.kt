package utils

import adapter.BaseSearchAdapter
import android.app.Activity
import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.WindowManager.*
import androidx.appcompat.app.AlertDialog.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import busca.SearchMultiActivity
import domain.search.SearchMulti
import ifValid
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object Dialogs {
	fun dialogSearch(activity: Activity, icon: MenuItem?) {
		val view = activity.layoutInflater.inflate(R.layout.layout_search_multi, null)
		val dialog = Builder(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
			.setView(view)
			.setCancelable(true)
			.create()
		dialog.setOnDismissListener { _: DialogInterface? ->
			activity.window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
			icon?.icon?.alpha = 255
		}
		icon.ifValid { icon!!.icon.alpha = 10 }

		dialog.show()
		dialog.findViewById<SearchView>(R.id.layout_search_multi_search)?.apply {
			setIconifiedByDefault(false)
			isFocusable = true
			isIconified = false
			requestFocusFromTouch()
			setOnCloseListener {
				dialog.dismiss()
				false
			}
			setOnQueryTextListener(object : OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					icon?.icon?.alpha = 255
					activity.startActivity(
						Intent(activity, SearchMultiActivity::class.java).apply {
							putExtra(SearchManager.QUERY, query)
						}
					)
					return false
				}

				override fun onQueryTextChange(newText: String): Boolean {
					if (newText.isEmpty() || newText.length < 2) return false

					GlobalScope.launch(Dispatchers.Main + SupervisorJob() + CoroutineExceptionHandler { _, e ->
						Handler(Looper.getMainLooper()).post {
							activity.makeToast(R.string.ops)
						}
					}) {
						val search: SearchMulti = Api(activity).getTmdbSearch(newText)
						dialog.findViewById<RecyclerView>(R.id.layout_search_multi_recycler)
							?.apply {
								setHasFixedSize(true)
								layoutManager = LinearLayoutManager(activity)
								adapter = BaseSearchAdapter(activity, search)
							}
					}
					return false
				}
			})
		}
	}
}