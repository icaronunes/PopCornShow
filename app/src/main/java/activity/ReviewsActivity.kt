package activity


import adapter.ReviewsAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdRequest
import domain.Api
import kotlinx.android.synthetic.main.activity_reviews.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes

class ReviewsActivity : BaseActivity() {
    private lateinit var id_filme: String
    //  private var type: String? = null
    private var subscriptions: CompositeSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(Constantes.NOME_FILME)
        id_filme = intent.getStringExtra(Constantes.FILME_ID)

//        if (intent.getStringExtra(Constantes.MEDIATYPE) == "tv-shows") {
//            type = "tv-shows"
//        } else {
//            type = "movies"
//        }

        recycleView_reviews?.layoutManager = LinearLayoutManager(this)
        recycleView_reviews?.itemAnimator = DefaultItemAnimator()
        recycleView_reviews?.setHasFixedSize(true)

        getReviews()

        val dialog = AlertDialog.Builder(this)
                .setIcon(R.drawable.alerta)
                .setPositiveButton(R.string.ok, null)
                .setTitle(R.string.alerta_spoiler)
                .setMessage(R.string.msg_spoiler)
                .create()
        dialog.show()

        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adView.loadAd(adRequest)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getReviews() {

        if (id_filme.isNotEmpty() && false) {
            val inscricao = Api(context = this).reviewsFilme(id_filme)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        recycleView_reviews?.adapter = ReviewsAdapter(this@ReviewsActivity,
                                it.message)
                        textview_reviews_empty?.visibility = View.GONE
                    }, {erro ->
                        Toast.makeText(this@ReviewsActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        textview_reviews_empty?.visibility = View.VISIBLE
                    })


            subscriptions.add(inscricao)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }


//    private inner class TMDVAsync : AsyncTask<Void, Void, Void>() {
//
//        override fun doInBackground(vararg voids: Void): Void? {
//            try {
//                reviewsUflixit = FilmeService.getReviews(id_filme, type)
//
//            } catch (e: Exception) {
//                FirebaseCrash.report(e)
//                runOnUiThread { Toast.makeText(this@ReviewsActivity, R.string.ops, Toast.LENGTH_SHORT).show() }
//            }
//
//            return null
//        }
//
//
//        override fun onPostExecute(aVoid: Void) {
//            super.onPostExecute(aVoid)
//
//            if (reviewsUflixit == null) {
//                textview_reviews_empty!!.visibility = View.VISIBLE
//                return
//            }
//
//            if (!reviewsUflixit!!.isError) {
//                recycleView_reviews!!.adapter = ReviewsAdapter(this@ReviewsActivity,
//                        reviewsUflixit)
//                textview_reviews_empty!!.visibility = View.GONE
//            } else {
//                textview_reviews_empty!!.visibility = View.VISIBLE
//            }
//        }
//    }

}
