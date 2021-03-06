package com.hossam.hasanin.base.externals

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


fun RecyclerView.onEndReachedStaggerdLayout(block: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val visibleItems = recyclerView.layoutManager!!.childCount
                val totalCount = recyclerView.layoutManager!!.itemCount
                val pastVisibleItems =
                    (recyclerView.layoutManager!! as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)
                if (pastVisibleItems != null && pastVisibleItems.isNotEmpty()){
                    val pastVisibleItem = pastVisibleItems[0]
                    Log.v("koko" , "pastVisibleItem $pastVisibleItem visibleItems $visibleItems totalCount $totalCount")

                    if (visibleItems + pastVisibleItem == totalCount ) {
                        block()
                    }
                }
            }
        }
    })
}

fun RecyclerView.onEndReachedLinearLayout(block: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val visibleItems = recyclerView.layoutManager!!.childCount
                val totalCount = recyclerView.layoutManager!!.itemCount
                val pastVisibleItem =
                    (recyclerView.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
                if (visibleItems + pastVisibleItem == totalCount ) {
                    block()
                }
            }
        }
    })
}