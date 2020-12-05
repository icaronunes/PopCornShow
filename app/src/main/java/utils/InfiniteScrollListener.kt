package utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(
    val getMedia: () -> Unit = {},
    private val layoutManager: LinearLayoutManager
) :
    RecyclerView.OnScrollListener() { // Todo - codigo chamado mais de uma vez

    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 3
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {

            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                Log.i("InfiniteScrollListener", "End reached")
                getMedia()
                loading = true
            }
        }
    }
}
