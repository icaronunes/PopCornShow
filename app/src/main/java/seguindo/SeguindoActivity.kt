package seguindo

import activity.BaseActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.UserTvshow
import kotlinx.android.synthetic.main.activity_usuario_list.linear_usuario_list
import kotlinx.android.synthetic.main.activity_usuario_list.progress
import kotlinx.android.synthetic.main.activity_usuario_list.tabLayout
import kotlinx.android.synthetic.main.activity_usuario_list.viewpage_usuario
import utils.ConstFirebase
import utils.UtilsApp
import java.util.ArrayList

/**
 * Created by icaro on 25/11/16.
 */
class SeguindoActivity : BaseActivity(), ValueEventListener {

    private lateinit var fallowDataRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario_list)
        setUpToolBar()
        supportActionBar?.setTitle(R.string.seguindo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (UtilsApp.isNetWorkAvailable(this)) {
            iniciarFirebases()
        } else {
            snack()
        }
    }

    private fun iniciarFirebases() {
        loadingVisibility()
        fallowDataRef = FirebaseDatabase.getInstance().getReference(ConstFirebase.USER)
        fallowDataRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(ConstFirebase.FOLLOW)
            .addListenerForSingleValueEvent(this)
    }

    private fun loadingVisibility(visibility: Int = View.VISIBLE) {
        progress?.visibility = visibility
    }

    private fun snack() {
        Snackbar.make(linear_usuario_list, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    iniciarFirebases()
                } else {
                    snack()
                }
            }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewPagerTabs(userTvshowFire: MutableList<UserTvshow>?) {
        viewpage_usuario.apply {
            offscreenPageLimit = 1
            currentItem = 2
            tabLayout?.setupWithViewPager(viewpage_usuario)
            tabLayout?.setSelectedTabIndicatorColor(ActivityCompat.getColor(this@SeguindoActivity, R.color.accent))
            adapter = FollowingAdapater(this@SeguindoActivity, supportFragmentManager,
                userTvshowFire!!)
        }
    }

    override fun onCancelled(p0: DatabaseError) {
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val userTvshowFire = ArrayList<UserTvshow>()
        if (dataSnapshot.exists()) {
            try {
                dataSnapshot.children
                    .asSequence()
                    .map {
                        it.getValue(UserTvshow::class.java)
                    }
                    .forEach { userTvshowFire.add(it!!) }
            } catch (e: Exception) {
            }
        }
        setupViewPagerTabs(userTvshowFire)
        loadingVisibility(View.GONE)
    }

    override fun onDestroy() {
        super.onDestroy()
        fallowDataRef.removeEventListener(this)
    }
}
