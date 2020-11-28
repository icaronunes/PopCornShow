package configuracao

import activity.BaseActivity
import android.R.id
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string

class SettingsActivity(override var layout: Int = Layout.settings_acitivity) : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.settings_acitivity)
		setUpToolBar()
		supportActionBar!!.setTitle(string.configuracoes)
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		fragmentManager.beginTransaction()
			.replace(R.id.fragment_settings, SettingsFragment(), "TRE")
			.commit()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == id.home) {
			finish()
		}
		return true
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		return true
	}

	companion object {
		const val PREF_IDIOMA_PADRAO = "pref_idioma_padrao"
		const val PREF_NOTIFICACAO = "pref_notificacao"
		const val PREF_SAVE_CONEXAO = "pref_save_conexao"
	}
}