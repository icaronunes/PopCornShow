package elenco

import Layout
import activity.BaseActivityAb
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.*
import elenco.adapter.WorksAdapter
import elenco.viewmodel.WorksViewModel
import kotlinx.android.synthetic.main.activity_elenco.adView
import kotlinx.android.synthetic.main.activity_elenco.elenco_recycleview
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import utils.Constant
import utils.enums.EnumTypeMedia
import utils.kotterknife.bindBundle
import utils.patternRecyler

/**
 * Created by icaro on 24/07/16.
 */
class WorksActivity(override var layout: Int = Layout.activity_elenco) : BaseActivityAb() {
	private val model: WorksViewModel by lazy { createViewModel(WorksViewModel::class.java, this) }
	private val title: String by bindBundle(Constant.NAME)
	private val id: Int by bindBundle(Constant.ID, 0)
	private val seasonNumer: Int by bindBundle(Constant.TVSEASONS, -100)
	private val type: EnumTypeMedia by bindBundle(Constant.MEDIATYPE)
	private val typeWork: Int by bindBundle(Constant.WORK)
	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		setAdMob(adView)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = title
		observer()
		model.fetchCredits(type, id, seasonNumer)
	}

	private fun observer() {
		model.credits.observe(this, Observer {
			when (it) {
				is Success -> {
					elenco_recycleview.patternRecyler(false).apply {
						when (typeWork) {
							Constant.ViewTypesIds.CREWS -> {
								elenco_recycleview.adapter =
									WorksAdapter(context, it.result.crew, horizontalLayout = true)
							}
							Constant.ViewTypesIds.CAST -> {
								elenco_recycleview.adapter =
									WorksAdapter(context, it.result.cast, horizontalLayout = true)
							}
							else -> ops()
						}
						model.setLoading(false)
					}
				}
				is Loading -> {
					progress_horizontal.visibility = if (it.loading) View.VISIBLE else View.GONE
				}
			}
		})
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
		}
		return true
	}
}
