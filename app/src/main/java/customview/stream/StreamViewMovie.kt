package customview.stream

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.icaro.filme.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import domain.reelgood.movie.Availability
import customview.stream.adapter.StreamMovieDelegatesAdapter
import kotlinx.android.synthetic.main.bottom_streaming.view.coord
import kotlinx.android.synthetic.main.bottom_streaming.view.group_recycler_list
import kotlinx.android.synthetic.main.bottom_streaming.view.group_types_list
import kotlinx.android.synthetic.main.bottom_streaming.view.label_bay
import kotlinx.android.synthetic.main.bottom_streaming.view.label_rent
import kotlinx.android.synthetic.main.bottom_streaming.view.label_stream
import kotlinx.android.synthetic.main.bottom_streaming.view.open_bar
import kotlinx.android.synthetic.main.bottom_streaming.view.rcBay
import kotlinx.android.synthetic.main.bottom_streaming.view.rcRent
import kotlinx.android.synthetic.main.bottom_streaming.view.rcStream
import kotlinx.android.synthetic.main.bottom_streaming.view.stream_error
import kotlinx.android.synthetic.main.bottom_streaming.view.title_streaming
import utils.gone
import utils.visible
import kotlin.properties.Delegates

/**
 * TODO: document your custom view class.
 * adicionar
 *   app:behavior_peekHeight="0dp"
app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior"
na implementacao da view no xml
 */
class StreamViewMovie : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    init {
        inflate(context, R.layout.bottom_streaming, this)
    }

    lateinit var titleMovie: String

    @SuppressLint("CustomViewStyleable")
    private fun initView(attrs: AttributeSet?) {
        attrs ?: return

        with(context.obtainStyledAttributes(attrs, R.styleable.StreamViewMovie)) {
            try {
                title = getString(R.styleable.StreamViewMovie_title_streaming)
                error = getBoolean(R.styleable.StreamViewMovie_error, false)
                labelStream = getString(R.styleable.StreamViewMovie_label_stream)
                labelRent = getString(R.styleable.StreamViewMovie_label_rent)
                labelBay = getString(R.styleable.StreamViewMovie_label_bay)
                iconSource = getDrawable(R.styleable.StreamViewMovie_errorImg)
                titleMovie = getString(R.styleable.StreamViewMovie_movie_name) ?: ""
            } finally {
                recycle()
            }
        }
    }

    fun isListsEmpty() = rent.isEmpty() && bay.isEmpty() && stream.isEmpty()
    fun setClose(sheet: BottomSheetBehavior<StreamViewMovie>) {
        open_bar.setOnClickListener {
            sheet.state = when (sheet.state) {
                BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                else -> BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private var iconSource: Drawable? by Delegates.observable<Drawable?>(null) { _, _, iconErro ->
        stream_error.setImageDrawable(iconErro)
        invalidate()
        requestLayout()
    }
    var rent: List<Availability> by Delegates.observable(listOf()) { _, _, listRent ->
        rcRent.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StreamMovieDelegatesAdapter(false, purchase = false, titleMedia = titleMovie).apply {
                addStream(listRent)
            }
        }
        error = false
        invalidate()
        requestLayout()
    }

    var bay: List<Availability> by Delegates.observable(listOf()) { _, _, listBay ->
        rcBay.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StreamMovieDelegatesAdapter(false, purchase = true, titleMedia = titleMovie).apply {
                addStream(listBay)
            }
        }
        error = false
        invalidate()
        requestLayout()
    }

    var stream: List<Availability> by Delegates.observable(listOf()) { _, _, listStream ->
        rcStream.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StreamMovieDelegatesAdapter(true, titleMedia = titleMovie).apply {
                addStream(listStream)
            }
        }
        error = false
        invalidate()
        requestLayout()
    }

    var error: Boolean by Delegates.observable(false) { _, _, visible ->
        if (visible) {
            group_types_list.gone()
            group_recycler_list.gone()
            stream_error.visible()
            coord.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        } else {
            group_types_list.visible()
            group_recycler_list.visible()
            stream_error.gone()
        }
        invalidate()
        requestLayout()
    }

    var title: String? by Delegates.observable<String?>("") { _, _, title ->
        title_streaming.text = title
    }

    private var labelStream: String? by Delegates.observable(context.getString(R.string.stream)) { _, _, title ->
        label_stream.text = title
        invalidate()
        requestLayout()
    }

    private var labelRent: String? by Delegates.observable(context.getString(R.string.rent)) { _, _, title ->
        label_rent.text = title
        invalidate()
        requestLayout()
    }

    private var labelBay: String? by Delegates.observable(context.getString(R.string.purchase)) { _, _, title ->
        label_bay.text = title
        invalidate()
        requestLayout()
    }
}
