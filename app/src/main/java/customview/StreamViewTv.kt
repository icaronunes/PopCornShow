package customview

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
import domain.reelgood.Availability
import filme.adapter.StreamTvDelegatesAdapter
import kotlinx.android.synthetic.main.bottom_streaming.view.coord
import kotlinx.android.synthetic.main.bottom_streaming.view.label_stream
import kotlinx.android.synthetic.main.bottom_streaming.view.open_bar
import kotlinx.android.synthetic.main.bottom_streaming.view.title_streaming
import kotlinx.android.synthetic.main.bottom_streaming_tv.view.stream_error
import kotlinx.android.synthetic.main.bottom_streaming_tv.view.tvRc
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
class StreamViewTv : FrameLayout {

    private lateinit var name: String

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {
        initView(attrs)
    }

    init {
        inflate(context, R.layout.bottom_streaming_tv, this)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initView(attrs: AttributeSet?) {
        attrs ?: return

        with(context.obtainStyledAttributes(attrs, R.styleable.StreamViewTv)) {
            try {
                error = getBoolean(R.styleable.StreamViewTv_error_tv, false)
                iconSource = getDrawable(R.styleable.StreamViewTv_errorImg_tv)
            } finally {
                recycle()
            }
        }
    }

    fun setClose(sheet: BottomSheetBehavior<StreamViewTv>) {
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

    fun fillStream(name: String, stream: List<String>) {
        this.name = name
        this.stream = stream
    }

    private var stream: List<String> by Delegates.observable(listOf()) { _, _, listStream ->
        if (listStream.isEmpty()) {
            error = true
        } else {
            error = false
            tvRc.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = StreamTvDelegatesAdapter(true, titleMedia = name).apply {
                    addStream(listStream.map {
                        Availability().apply {
                            sourceName = it
                        }
                    })
                }
            }
        }
        invalidate()
        requestLayout()
    }

    var error: Boolean by Delegates.observable(false) { _, _, visible ->
        if (visible) {
            stream_error.visible()
            coord.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        } else {
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
}
