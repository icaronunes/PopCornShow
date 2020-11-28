package desenvolvimento

import activity.BaseActivity
import android.R.id
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import desenvolvimento.adapter.DesenvolvimentoAdapater
import utils.kotterknife.findView
import utils.patternRecyler

/**
 * Created by icaro on 18/12/16.
 */
class Desenvolvimento(override var layout: Int = Layout.desenvolvimento_layout) : BaseActivity() {

	val recyclerView: RecyclerView by findView(R.id.recycleView_desenvolvimento)

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setTitle(string.ajuda_desenvolvimento)

		recyclerView.patternRecyler(false).apply {
			val bibliotecas = resources.getStringArray(array.bibliotecas)
			adapter = DesenvolvimentoAdapater(context, bibliotecas)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (id.home == item.itemId) {
			finish()
		}
		return true
	}
}