package temporada

import activity.BaseActivity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import applicaton.PopCornApplication
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.Api
import domain.EpisodesItem
import domain.FilmeService
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import episodio.EpsodioActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale
import kotlinx.android.synthetic.main.temporada_layout.recycleView_temporada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 26/08/16.
 */
class TemporadaActivity : BaseActivity(), TemporadaAdapter.TemporadaOnClickListener {
    // TODO adicionar ViewModel
    private var temporada_id: Int = 0
    private var temporada_position: Int = 0
    private var nome_temporada: String? = null
    private var serie_id: Int = 0
    private var color: Int = 0
    private var tvSeason: TvSeasons? = null
    private var seguindo: Boolean = false
    private var seasons: UserSeasons? = null
    private var mAuth: FirebaseAuth? = null
    private var myRef: DatabaseReference? = null
    private var postListener: ValueEventListener? = null
    private var subscription: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temporada_layout)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()
        supportActionBar?.title = " "
        recycleView_temporada.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
        }

        mAuth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().getReference("users")
        subscription = CompositeSubscription()

        if (UtilsApp.isNetWorkAvailable(this)) {
            getDados()
        } else {
            snack()
        }
    }

    private fun snack() {
        Snackbar.make(recycleView_temporada, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    getDados()
                } else {
                    snack()
                }
            }.show()
    }

    fun getExtras() {
        if (intent.action == null) {
            temporada_id = intent.getIntExtra(Constantes.TEMPORADA_ID, 0)
            temporada_position = intent.getIntExtra(Constantes.TEMPORADA_POSITION, 0)
            serie_id = intent.getIntExtra(Constantes.TVSHOW_ID, 0)
            nome_temporada = intent.getStringExtra(Constantes.NOME)
            color = intent.getIntExtra(Constantes.COLOR_TOP, resources.getColor(R.color.red))
        } else {
            temporada_id = Integer.parseInt(intent.getStringExtra(Constantes.TEMPORADA_ID))
            temporada_position = Integer.parseInt(intent.getStringExtra(Constantes.TEMPORADA_POSITION))
            serie_id = Integer.parseInt(intent.getStringExtra(Constantes.TVSHOW_ID))
            nome_temporada = intent.getStringExtra(Constantes.NOME)
            color = Integer.parseInt(intent.getStringExtra(Constantes.COLOR_TOP))
        }
    }

    override fun onClickVerTemporada(position: Int) {
        if (seasons != null && seasons!!.userEps!![position].isAssistido) {
            removeWatch(position)
        } else {
            if (isAssistidoAnteriores(position)) {
                watchBefore(position)
            } else {
                addEpWatch(position)
            }
        }
    }

    override fun onClickTemporada(position: Int) {
        startActivity(Intent(this@TemporadaActivity, EpsodioActivity::class.java).apply {
            putExtra(Constantes.TVSHOW_ID, serie_id)
            putExtra(Constantes.POSICAO, position)
            putExtra(Constantes.TEMPORADA_POSITION, temporada_position)
            putExtra(Constantes.TVSEASONS, tvSeason)
            putExtra(Constantes.COLOR_TOP, color)
            putExtra(Constantes.SEGUINDO, seguindo)
        })
    }

    override fun onClickTemporadaNota(view: View?, ep: EpisodesItem, position: Int, userEp: UserEp?) {
        var date: Date? = null

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            ep.airDate?.let { date = sdf.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (UtilsApp.verificaLancamento(date) && mAuth?.currentUser != null && seguindo) {
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(mAuth!!.currentUser!!.uid)
                .child("seguindo")
                .child(serie_id.toString())
                .child("seasons")
                .child(temporada_position.toString())

            val alertDialog = Dialog(this@TemporadaActivity)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.dialog_custom_rated)

            val naoVisto = alertDialog.findViewById<Button>(R.id.cancel_rated)

            if (userEp != null) {
                if (!userEp.isAssistido) {
                    naoVisto.visibility = View.INVISIBLE
                }
            } else {
                naoVisto.visibility = View.INVISIBLE
            }

            alertDialog.findViewById<TextView>(R.id.rating_title).let {
                it.text = ep.name
            }
            val ratingBar = alertDialog.findViewById<RatingBar>(R.id.ratingBar_rated).apply {
                rating = userEp?.nota!!
            }
            val width = resources.getDimensionPixelSize(R.dimen.popup_width)
            val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

            alertDialog.window!!.setLayout(width, height)
            alertDialog.show()

            naoVisto.setOnClickListener {

                if (seguindo) {

                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/userEps/$position/assistido"] = false
                    childUpdates["/visto/"] = false
                    childUpdates["/userEps/$position/nota"] = 0
                    databaseReference.updateChildren(childUpdates) { databaseError, databaseReference1 ->
                        if (databaseError == null) {
                            databaseReference1
                                .child("userEps")
                                .child(position.toString())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        val userEp1 = dataSnapshot
                                            .getValue(UserEp::class.java)
                                        if (userEp1 != null) {
                                            (recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(userEp1, position)
                                        } else {
                                            Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                    }
                                })
                        } else {
                            Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                alertDialog.dismiss()
            }

            alertDialog.findViewById<Button>(R.id.ok_rated).setOnClickListener {

                val childUpdates = HashMap<String, Any>()
                childUpdates["/userEps/$position/assistido"] = true
                childUpdates["/visto"] = temporadaTodaAssistida(position)
                childUpdates["/userEps/$position/nota"] = ratingBar.rating
                databaseReference.updateChildren(childUpdates) { databaseError, databaseReference12 ->
                    if (databaseError == null) {
                        databaseReference12
                            .child("userEps")
                            .child(position.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val userEp12 = dataSnapshot.getValue(UserEp::class.java)
                                    if (userEp12 != null) {
                                        (recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(userEp12, position)
                                        setnotaIMDB(position, ratingBar.rating.toInt())
                                    } else {
                                        Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                    } else {
                        Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog.dismiss()
            }
        }
    }

    override fun onClickScrool(position: Int) {
        scrollToTop(position)
    }

    private fun watchBefore(position: Int) {
        val dialog = AlertDialog.Builder(this@TemporadaActivity)
            .setTitle(R.string.title_marcar_ep_anteriores)
            .setMessage(R.string.msg_marcar_ep_anteriores)
            .setPositiveButton(R.string.ok) { _, _ ->
                val user = mAuth!!.currentUser!!.uid

                val childUpdates = HashMap<String, Any>()
                for (i in 0..position) {
                    childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/userEps/$i/assistido"] = true
                }

                if (position == seasons?.userEps?.size!! - 1) {
                    childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/visto/"] = true
                } else {
                    childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/visto/"] = temporadaTodaAssistida(position)
                }

                myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
                    if (databaseError == null) {
                        databaseReference.child(user).child("seguindo")
                            .child(serie_id.toString()).child("seasons")
                            .child(temporada_position.toString())
                            .child("userEps")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (i in 0..position) {
                                            val userEp = dataSnapshot.child(i.toString()).getValue(UserEp::class.java)
                                            (recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(userEp, i)
                                        }
                                    } else {
                                        Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                    } else {
                        Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
                    }
                }

                Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.no) { _, _ ->
                val user = mAuth!!.currentUser!!.uid
                val childUpdates = HashMap<String, Any>()

                childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/userEps/$position/assistido"] = true
                childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/visto/"] = temporadaTodaAssistida(position)

                myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
                    if (databaseError == null) {
                        databaseReference.child(user).child("seguindo")
                            .child(serie_id.toString()).child("seasons")
                            .child(temporada_position.toString())
                            .child("userEps").child(position.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val userEp = dataSnapshot.getValue(UserEp::class.java)
                                        (recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(userEp, position)
                                    } else {
                                        Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                    } else {
                        Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
            }
            .create()
        dialog.show()
    }

    private fun addEpWatch(position: Int) {
        val user = mAuth!!.currentUser!!.uid

        val childUpdates = HashMap<String, Any>()

        childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/userEps/$position/assistido"] = true
        childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/visto/"] = temporadaTodaAssistida(position)

        myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
            if (databaseError == null) {
                databaseReference.child(user).child("seguindo")
                    .child(serie_id.toString()).child("seasons")
                    .child(temporada_position.toString())
                    .child("userEps").child(position.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val userEp = dataSnapshot.getValue(UserEp::class.java)
                                (recycleView_temporada.adapter as TemporadaFoldinAdapter)
                                    .notificarMudanca(userEp, position)
                            } else {
                                Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
            } else {
                Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT).show()
    }

    private fun removeWatch(position: Int) {
        val user = mAuth!!.currentUser!!.uid

        val childUpdates = HashMap<String, Any>()

        childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/userEps/$position/assistido"] = false
        childUpdates["/$user/seguindo/$serie_id/seasons/$temporada_position/visto/"] = false

        myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
            if (databaseError == null) {
                databaseReference
                    .child(user).child("seguindo")
                    .child(serie_id.toString()).child("seasons")
                    .child(temporada_position.toString())
                    .child("userEps").child(position.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val userEp = dataSnapshot.getValue(UserEp::class.java)
                                (recycleView_temporada.adapter as TemporadaFoldinAdapter)
                                    .notificarMudanca(userEp, position)
                                Toast.makeText(this@TemporadaActivity, R.string.marcado_nao_assistido, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
            } else {
                Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToTop(currentCard: Int) {
        val layoutManager = recycleView_temporada.layoutManager as LinearLayoutManager?
        layoutManager?.scrollToPositionWithOffset(currentCard, 0)
    }

    private fun setnotaIMDB(position: Int, ratingBar: Int) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                FilmeService.ratedTvshowEpsodioGuest(serie_id, seasons?.seasonNumber!!,
                    seasons!!.userEps!![position].episodeNumber, ratingBar, applicationContext)
            } catch (ex: Exception) {
            }
        }
        job.cancel()
    }

    private fun temporadaTodaAssistida(position: Int): Boolean {

        for (userEp in seasons!!.userEps!!) {
            if (seasons!!.userEps!![position] != userEp) {
                if (!userEp.isAssistido) {
                    return false
                }
            }
        }
        return true
    }

    private fun isAssistidoAnteriores(position: Int): Boolean {
        for (i in 0 until position) {
            if (!seasons!!.userEps!![i].isAssistido) {
                return true
            }
        }
        return false
    }

    private fun setListener() {

        postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists() && seguindo) {
                    seasons = dataSnapshot.getValue(UserSeasons::class.java)
                    PopCornApplication.getInstance().bus.post(seasons!!)
                    recycleView_temporada.adapter!!.notifyDataSetChanged()
                } else {
                    recycleView_temporada.adapter = TemporadaFoldinAdapter(this@TemporadaActivity,
                        tvSeason!!, seasons, seguindo,
                        this@TemporadaActivity)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        myRef!!.child(mAuth!!.currentUser!!.uid)
            .child("seguindo")
            .child(serie_id.toString())
            .child("seasons")
            .child(temporada_position.toString())
            .addValueEventListener(postListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (postListener != null) {
            myRef!!.child(mAuth!!.currentUser!!.uid)
                .child("seguindo")
                .child(serie_id.toString())
                .child("seasons")
                .child(temporada_position.toString())
                .removeEventListener(postListener!!)
        }
        subscription!!.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun getDados() {
        Api(this).getTvSeasons(serie_id, temporada_id, temporada_position)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TvSeasons> {
                override fun onCompleted() {

                    if (tvSeason == null) {
                        return
                    }
                    supportActionBar!!.title = if (!tvSeason!!.name!!.isEmpty()) tvSeason!!.name else nome_temporada

                    if (mAuth!!.currentUser != null) {
                        myRef!!.child(mAuth!!.currentUser!!.uid)
                            .child("seguindo")
                            .child(serie_id.toString())
                            .child("seasons")
                            .child(temporada_position.toString())
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        // Get user value
                                        if (dataSnapshot.exists()) {
                                            seguindo = true
                                            seasons = dataSnapshot.getValue(UserSeasons::class.java)
                                            recycleView_temporada.adapter = TemporadaFoldinAdapter(this@TemporadaActivity,
                                                tvSeason!!, seasons, seguindo, this@TemporadaActivity)
                                        } else {
                                            seguindo = false
                                            recycleView_temporada.adapter = TemporadaFoldinAdapter(this@TemporadaActivity,
                                                tvSeason!!, null, seguindo, this@TemporadaActivity)
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                        setListener()
                    } else {
                        recycleView_temporada.adapter = TemporadaFoldinAdapter(this@TemporadaActivity,
                            tvSeason!!, seasons, seguindo,
                            this@TemporadaActivity)
                    }
                }

                override fun onError(e: Throwable) {
                    Crashlytics.logException(e)
                    Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                }

                override fun onNext(tvSeasons: TvSeasons) {
                    tvSeason = tvSeasons
                }
            })
    }
}
