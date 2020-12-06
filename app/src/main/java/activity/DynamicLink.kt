package activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import br.com.icaro.filme.R
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import filme.activity.MovieDetailsActivity
import main.MainActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.makeToast

/**
 * Created by icaro on 16/12/16.
 */

class DynamicLink : AppCompatActivity() {

    companion object {
        const val ACTION = "action"
        const val MOVIE = "movie"
        const val TVSHOW = "tvshow"
        const val NAME = "name"
        const val ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDynamicLinks()
    }

    private fun setUpDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    if (pendingDynamicLinkData != null) {
                        callIntent(pendingDynamicLinkData.link!!)
                    } else {
                        callMainActivity()
                    }
                    // Handle the deep link. For example, open the linked
                    // content, or apply promotional credit to the user's
                    // account.
                }
                .addOnFailureListener(this) {
                    callMainActivity()
                }
    }

    private fun callIntent(deepLink: Uri) {
        var id: String? = ""
        var action: String? = ""
        var name: String? = ""
        // Exemplo
        // https://br.com.icaro.filme/?action=movie&id=58
        // https://br.com.icaro.filme/?action=tvshow&id=58

        for (param in deepLink.queryParameterNames) {
            when (param) {
                ACTION -> action = deepLink.getQueryParameter(ACTION)
                ID -> id = deepLink.getQueryParameter(ID)!!
                NAME -> name = deepLink.getQueryParameter(ACTION)
            }
        }

        if (action.isNullOrBlank() || id.isNullOrBlank()) {
            callMainActivity()
        } else {
            try {
                id.toInt()
                when (action) {
                    MOVIE -> {
                        val stackBuilder = TaskStackBuilder.create(this@DynamicLink)
                        stackBuilder.addParentStack(MainActivity::class.java)
                        stackBuilder.addNextIntent(Intent(this@DynamicLink, MovieDetailsActivity::class.java).apply {
                            putExtra(Constant.ID, id.toInt())
                        })
                        stackBuilder.startActivities()
                        finish()
                    }
                    TVSHOW -> {
                        val stackBuilder = TaskStackBuilder.create(this@DynamicLink)
                        stackBuilder.addParentStack(MainActivity::class.java)
                        stackBuilder.addNextIntent(Intent(this@DynamicLink, TvShowActivity::class.java).apply {
                            putExtra(Constant.NOME_TVSHOW, name)
                            putExtra(Constant.ID, id.toInt())
                        })
                        stackBuilder.startActivities()
                        finish()
                    }
                    else -> callMainActivity()
                }
            } catch (ex: NumberFormatException) {
                callMainActivity()
                makeToast(R.string.ops)
                finish()
            }
        }
    }

    private fun callMainActivity() {
        startActivity(Intent(this@DynamicLink, MainActivity::class.java))
        finish()
    }
}
