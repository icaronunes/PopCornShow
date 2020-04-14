package episodio

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import applicaton.BaseFragment
import br.com.icaro.filme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.CrewItem
import domain.EpisodesItem
import domain.FilmeService
import domain.UserEp
import domain.UserSeasons
import kotlinx.android.synthetic.main.epsodio_fragment.air_date
import kotlinx.android.synthetic.main.epsodio_fragment.ep_director
import kotlinx.android.synthetic.main.epsodio_fragment.ep_image
import kotlinx.android.synthetic.main.epsodio_fragment.ep_rating
import kotlinx.android.synthetic.main.epsodio_fragment.ep_rating_button
import kotlinx.android.synthetic.main.epsodio_fragment.ep_sinopse
import kotlinx.android.synthetic.main.epsodio_fragment.ep_title
import kotlinx.android.synthetic.main.epsodio_fragment.ep_votos
import kotlinx.android.synthetic.main.epsodio_fragment.ep_write
import kotlinx.android.synthetic.main.epsodio_fragment.wrapper_rating_ep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pessoa.activity.PersonActivity
import utils.ConstFirebase.ASSISTIDO
import utils.ConstFirebase.FOLLOW
import utils.ConstFirebase.NOTA
import utils.ConstFirebase.SEASONS
import utils.ConstFirebase.USER
import utils.ConstFirebase.USEREPS
import utils.ConstFirebase.VISTO
import utils.Constant
import utils.UtilsApp
import utils.parseDateShot
import utils.setPicasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioFragment : BaseFragment(), ValueEventListener {

    private var tvshowId: Int = 0
    private var color: Int = 0
    private var position: Int = 0
    private var temporadaPosition: Int = 0
    private var numeroRated: Float = 0.toFloat()

    private var episode: EpisodesItem? = null
    private var userEp: UserEp? = null
    private var seguindo: Boolean = false
    private var mAuth: FirebaseAuth? = null
    private var myRef: DatabaseReference? = null
    private var databaseReference: DatabaseReference? = null
    private var userListener: ValueEventListener? = null
    private var seasons: UserSeasons? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            episode = arguments?.getSerializable(Constant.EPSODIO) as EpisodesItem
            tvshowId = arguments?.getInt(Constant.TVSHOW_ID)!!
            color = arguments?.getInt(Constant.COLOR_TOP)!!
            seguindo = arguments?.getBoolean(Constant.SEGUINDO)!!
            position = arguments?.getInt(Constant.POSICAO)!!
            temporadaPosition = arguments?.getInt(Constant.TEMPORADA_POSITION)!!
        }

        if (seguindo) {
            mAuth = FirebaseAuth.getInstance()
            myRef = FirebaseDatabase.getInstance()
                    .getReference(USER)
                    .child(mAuth!!.currentUser!!.uid)
                    .child(FOLLOW)
                    .child(tvshowId.toString())
                    .child(SEASONS)
                    .child(temporadaPosition.toString())
                    .child(USEREPS)
                    .child(position.toString())

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference(USER)
                    .child(mAuth!!.currentUser!!.uid)
                    .child(FOLLOW)
                    .child(tvshowId.toString())
                    .child(SEASONS)
                    .child(temporadaPosition.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.epsodio_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setListener()
        setAirDate()
        setVote()
        setImage()
        setSinopse()
        setName()
        setDirect()
        setWriter()
        setColorButton()
    }

    private fun setDirect() {
        episode?.crew?.firstOrNull { it?.department?.equals("Directing")!! }?.let { director ->
            ep_director.text = director.name
            ep_director.setOnClickListener {
                nextPersonActivity(director)
            }
        }
    }

    private fun setWriter() {
        episode?.crew?.firstOrNull { it?.department?.equals("Writing")!! }?.let { written ->
            ep_write.text = written.name
            ep_write.setOnClickListener {
                nextPersonActivity(written)
            }
        }
    }

    private fun nextPersonActivity(it: CrewItem) {
        startActivity(Intent(context, PersonActivity::class.java).apply {
            putExtra(Constant.NOME_PERSON, it.name)
            putExtra(Constant.PERSON_ID, it.id)
        })
    }

    private fun setListener() {
        userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userEp = dataSnapshot.getValue(UserEp::class.java)
                if (userEp != null) {
                    try {
                        if (userEp?.isAssistido!!) {
                            ep_rating_button.text = resources.getText(R.string.classificar_visto)
                            userEp?.nota?.let {
                                ep_rating.rating = it * 2
                            }
                        } else {
                            ep_rating_button.text = resources.getText(R.string.classificar)
                            userEp?.nota?.let {
                                ep_rating.rating = it * 2
                            }
                        }
                    } catch (e: NoSuchMethodError) {
                        Toast.makeText(context, R.string.ops, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        if (seguindo) {
            myRef?.addValueEventListener(userListener!!)
            databaseReference!!.addValueEventListener(this@EpsodioFragment)
        }
    }

    override fun onCancelled(dataSnapshot: DatabaseError) {
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        seasons = dataSnapshot.getValue(UserSeasons::class.java)
        seasons?.userEps?.get(position)?.nota?.let {
            numeroRated = it
        }
    }

    override fun onDestroyView() {
        if (userListener != null) myRef?.removeEventListener(userListener!!)
        databaseReference?.removeEventListener(this@EpsodioFragment)
        super.onDestroyView()
    }

    private fun setButtonRating(available: String?) {
        var date: Date? = null

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (available != null) date = sdf.parse(available)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (UtilsApp.verifyLaunch(date) && seguindo) {
            ep_rating_button.setOnClickListener {
                dialogSetRating()
            }

            wrapper_rating_ep.setOnClickListener {
                dialogSetRating()
            }
        } else {
            ep_rating_button.visibility = View.GONE
        }
    }

    private fun dialogSetRating() {
        val alertDialog = Dialog(context!!)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_custom_rated)

        val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
        val naoVisto = alertDialog.findViewById<View>(R.id.cancel_rated) as Button

        if (userEp != null) {
            if (!userEp!!.isAssistido) {
                naoVisto.visibility = View.INVISIBLE
            }
        } else {
            naoVisto.visibility = View.INVISIBLE
        }

        episode?.name.let {
            alertDialog.findViewById<TextView>(R.id.rating_title).let {
                it.text = if (episode?.name != null) episode!!.name else ""
            }
        }

        val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
        ratingBar.rating = numeroRated
        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

        alertDialog.window?.setLayout(width, height)
        alertDialog.show()

        naoVisto.setOnClickListener {
            if (seguindo) {
                val childUpdates = HashMap<String, Any>()

                childUpdates["/$USEREPS/$position/$ASSISTIDO"] = false
                childUpdates["/$VISTO/"] = false
                childUpdates["/$USEREPS/$position/$NOTA"] = 0
                databaseReference?.updateChildren(childUpdates)
            }
            alertDialog.dismiss()
        }

        ok.setOnClickListener {
            if (seguindo) {
                val childUpdates = HashMap<String, Any>()
                childUpdates["/$USEREPS/$position/$ASSISTIDO"] = true
                childUpdates["/$VISTO"] = temporadaTodaAssistida()
                childUpdates["/$USEREPS/$position/$NOTA"] = ratingBar.rating
                databaseReference?.updateChildren(childUpdates)

                setRatedTvShowGuest(ratingBar)
            }
            alertDialog.dismiss()
        }
    }

    private fun setRatedTvShowGuest(ratingBar: RatingBar) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                FilmeService.ratedTvshowEpsodioGuest(tvshowId, seasons?.seasonNumber!!,
                        episode?.episodeNumber!!, ratingBar.rating.toInt(), context)
            } catch (ex: Exception) {
            }
        }
        job.cancel()
    }

    private fun setColorButton() {
        ep_rating_button.setTextColor(color)
    }

    private fun temporadaTodaAssistida(): Boolean {
        for (userEp in seasons?.userEps!!) {
            if (episode?.id != userEp.id) {
                if (!userEp.isAssistido) {
                    return false
                }
            }
        }
        return true
    }

    private fun setSinopse() {
        episode?.overview.let {
            ep_sinopse.text = it
        }
    }

    private fun setImage() {

        if (!episode?.stillPath.isNullOrBlank()) {
            ep_image.setPicasso(episode?.stillPath, 5, img_erro = R.drawable.top_empty)
        } else {
            ep_image.setPicasso("", img_erro = R.drawable.top_empty)
        }
    }

    private fun setVote() {

        episode?.voteAverage.let { vote ->
            episode?.voteCount.let { count ->
                ep_votos?.text = "$vote/$count"
                ep_votos.visibility = View.VISIBLE
            }
        }
    }

    private fun setAirDate() {
        episode?.airDate?.let {
            air_date.text = it.parseDateShot()
            setButtonRating(it)
        }
    }

    private fun setName() {
        ep_title?.text = if (episode?.name?.isNotEmpty()!!) episode?.name else context!!.getString(R.string.sem_nome)
    }

    companion object {

        fun newInstance(
            tvEpisode: EpisodesItem,
            tvshow_id: Int,
            color: Int,
            seguindo: Boolean,
            position: Int,
            temporada_position: Int
        ): Fragment {

            val fragment = EpsodioFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.EPSODIO, tvEpisode)
            bundle.putInt(Constant.TVSHOW_ID, tvshow_id)
            bundle.putInt(Constant.COLOR_TOP, color)
            bundle.putBoolean(Constant.SEGUINDO, seguindo)
            bundle.putInt(Constant.POSICAO, position)
            bundle.putInt(Constant.TEMPORADA_POSITION, temporada_position)
            fragment.arguments = bundle
            return fragment
        }
    }
}
