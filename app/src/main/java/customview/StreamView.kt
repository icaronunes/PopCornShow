package customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.icaro.filme.R
import domain.reelgood.Availability
import filme.adapter.StreamMovieDelegatesAdapter
import kotlinx.android.synthetic.main.bottom_streaming.view.group_recycler_list
import kotlinx.android.synthetic.main.bottom_streaming.view.group_types_list
import kotlinx.android.synthetic.main.bottom_streaming.view.label_bay
import kotlinx.android.synthetic.main.bottom_streaming.view.label_rent
import kotlinx.android.synthetic.main.bottom_streaming.view.label_stream
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
class StreamView : FrameLayout {

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

    @SuppressLint("CustomViewStyleable")
    private fun initView(attrs: AttributeSet?) {
        attrs ?: return

        with(context.obtainStyledAttributes(attrs, R.styleable.StreamView)) {
            try {
                title = getString(R.styleable.StreamView_title_streaming)
                error = getBoolean(R.styleable.StreamView_error, false)
                labelStream = getString(R.styleable.StreamView_label_stream)
                labelRent = getString(R.styleable.StreamView_label_rent)
                labelBay = getString(R.styleable.StreamView_label_bay)
                iconSource = getDrawable(R.styleable.StreamView_errorImg)
            } finally {
                recycle()
            }
        }
    }

    var iconSource: Drawable? by Delegates.observable<Drawable?>(null) { _, _, iconErro ->
        stream_error.setImageDrawable(iconErro)
        invalidate()
        requestLayout()
    }
    var rent: List<Availability> by Delegates.observable(listOf()) { _, _, listRent ->
        rcRent.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StreamMovieDelegatesAdapter(false, purchase = false).apply {
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
            adapter = StreamMovieDelegatesAdapter(false, purchase = true).apply {
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
            adapter = StreamMovieDelegatesAdapter(true).apply {
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

    var labelStream: String? by Delegates.observable<String?>(context.getString(R.string.stream)) { _, _, title ->
        label_stream.text = title
        invalidate()
        requestLayout()
    }

    var labelRent: String? by Delegates.observable<String?>(context.getString(R.string.rent)) { _, _, title ->
        label_rent.text = title
        invalidate()
        requestLayout()
    }

    var labelBay: String? by Delegates.observable<String?>(context.getString(R.string.purchase)) { _, _, title ->
        label_bay.text = title
        invalidate()
        requestLayout()
    }
}
