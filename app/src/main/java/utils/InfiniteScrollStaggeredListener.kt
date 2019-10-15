package utils

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class InfiniteScrollStaggeredListener(val anuncio: () -> Unit = {}, val getMedia: () -> Unit = {}, private val gridLayout: StaggeredGridLayoutManager) :
    RecyclerView.OnScrollListener() { // Todo - codigo chamado mais de uma vez

    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 1
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var totaltem = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {

            visibleItemCount = recyclerView.childCount
            totalItemCount = gridLayout.itemCount
            firstVisibleItem = gridLayout.findFirstCompletelyVisibleItemPositions(intArrayOf(0, 1))[0]

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                    Log.i("InfiniteScrollListener", "loading")
                }
            }

            if (!loading && totalItemCount - 4 <= firstVisibleItem) {
                getMedia()
                loading = true
            }
        }
    }
}
