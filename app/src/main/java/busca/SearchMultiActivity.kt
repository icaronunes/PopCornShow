package busca

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import applicaton.BaseViewModel.BaseRequest
import br.com.icaro.filme.R.layout
import domain.search.SearchMulti
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.search_layout.adView
import kotlinx.android.synthetic.main.search_layout.recycleView_search
import kotlinx.android.synthetic.main.search_layout.swipeToRefresh
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.BaseActivityKt
import utils.Constantes
import utils.UtilsApp.isNetWorkAvailable
import utils.enums.EnumTypeMedia.MOVIE
import utils.enums.EnumTypeMedia.PERSON
import utils.enums.EnumTypeMedia.TV

/**
 * Created by icaro on 08/07/16.
 */
class SearchMultiActivity : BaseActivityKt() {

    private val recyclerView: RecyclerView by lazy {
            recycleView_search.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
        }
    }
    private val model: SearchMultiModelView by lazy {
        createViewModel(SearchMultiModelView::class.java, this)
    }
    private var query = ""
    private var pagina = 1

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.search_layout)
        setUpToolBar()
        setupNavDrawer()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        query = intent.getStringExtra(SearchManager.QUERY)
        supportActionBar!!.title = query
        if (Intent.ACTION_VIEW == intent.action) { //TODO pra que server isso?
            val intent: Intent
            when {
                getIntent().data.lastPathSegment.equals(MOVIE.type, ignoreCase = true) -> {
                    intent = Intent(this, MovieDetailsActivity::class.java)
                    val id = getIntent().extras.getString(SearchManager.EXTRA_DATA_KEY).toInt() //ID
                    intent.putExtra(Constantes.FILME_ID, id)
                    intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)
                    finish()
                    return
                }
                getIntent().data.lastPathSegment.equals(TV.type, ignoreCase = true) -> {
                    val id = getIntent().extras.getString(SearchManager.EXTRA_DATA_KEY).toInt() //ID
                    intent = Intent(this, TvShowActivity::class.java)
                    intent.putExtra(Constantes.TVSHOW_ID, id)
                    intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)
                    finish()
                    return
                }
                getIntent().data.lastPathSegment.equals(PERSON.type, ignoreCase = true) -> {
                    intent = Intent(this, PersonActivity::class.java)
                    val id = getIntent().extras.getString(SearchManager.EXTRA_DATA_KEY).toInt() //ID
                    intent.putExtra(Constantes.PERSON_ID, id)
                    intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)
                    finish()
                    return
                }
            }
        } else {
            if (isNetWorkAvailable(baseContext)) {
                model.fetchData(query)
            }
            swipeToRefresh.setOnRefreshListener(onRefreshListener())
        }

        observers()
    }

    private fun observers() {
        model.response.observe(this, Observer {
            when (it) {
                is BaseRequest.Success -> {
                    fillRecycler(it.result as SearchMulti)
                }
                is BaseRequest.Failure -> {
                    Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
                }
                is BaseRequest.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun fillRecycler(it: SearchMulti) {
        recyclerView.adapter = SearchAdapter(this@SearchMultiActivity, it)
    }

    private fun onRefreshListener(): OnRefreshListener {
        return OnRefreshListener {
            //TODO adicionar infinityScroll
            if (isNetWorkAvailable(this@SearchMultiActivity)) {
                model.fetchData(query)
            } else {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAdMob(adView)
    }

//    private inner class TMDVAsync : AsyncTask<Void?, Void?, MutableList<Multi>?>() {
//        override fun onPreExecute() {
//            swipeRefreshLayout!!.isEnabled = false
//        }
//
//        protected override fun doInBackground(vararg voids: Void): MutableList<Multi>? {
//            if (!query.isEmpty()) {
//                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@SearchMultiActivity)
//                val idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
//                try {
//                    return if (idioma_padrao) {
//                        val tmdbSearch = FilmeService.getTmdbSearch()
//                        val movieResultsPage = tmdbSearch.searchMulti(query,
//                            getLocale() + "en,null", pagina)
//                        movieResultsPage.results
//                    } else {
//                        val tmdbSearch = FilmeService.getTmdbSearch()
//                        val movieResultsPage = tmdbSearch.searchMulti(query,
//                            "en,null", pagina)
//                        movieResultsPage.results
//                    }
//                } catch (e: Exception) {
//                    Crashlytics.logException(e)
//                    runOnUiThread { Toast.makeText(this@SearchMultiActivity, string.ops, Toast.LENGTH_SHORT).show() }
//                }
//            }
//            return null
//        }
//
//        override fun onPostExecute(movieDbs: MutableList<Multi>?) {
//            swipeRefreshLayout!!.isEnabled = true
//            if (movieDbs != null && pagina != 1) {
//                val x: List<Multi>? = movieDbList
//                movieDbList = movieDbs
//                for (movie in x!!) {
//                    movieDbList!!.add(movie)
//                }
//                pagina++
//            } else {
//                movieDbList = movieDbs
//            }
//            if (movieDbList != null && movieDbList!!.size > 0) { // TODO: 09/01/17 pode ser null? - vai dar erro?
//                swipeRefreshLayout!!.isRefreshing = false
//                recyclerView!!.adapter = SearchAdapter(this@SearchMultiActivity, movieDbList!!)
//                swipeRefreshLayout!!.isEnabled = true
//                pagina++
//                progressBar!!.visibility = View.GONE
//            } else {
//                progressBar!!.visibility = View.GONE
//                text_search_empty!!.visibility = View.VISIBLE
//            }
//        }
//    }
}