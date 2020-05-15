package com.jobc.popularmoviestest.main.utils

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(
    private val offset: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if(parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)) {
             outRect.set(Rect(0, 0, 0, dpToPx(offset)))
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(Rect(0, dpToPx(offset), 0, 0))
        }
    }

    private fun dpToPx(dp: Int) =
        dp * Resources.getSystem().displayMetrics.density.toInt()
}